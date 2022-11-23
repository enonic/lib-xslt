package com.enonic.lib.xslt;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;

public final class XsltProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( XsltProcessor.class );

    private final TransformerFactory factory;

    private final XsltProcessorErrors errors;

    private Source xsltSource;

    private Source xmlSource;

    private final ResourceService resourceService;

    private ResourceKey view;

    public XsltProcessor( final ResourceService resourceService, final TransformerFactory factory )
    {
        this.resourceService = resourceService;
        this.factory = factory;
        this.errors = new XsltProcessorErrors();
        this.factory.setErrorListener( this.errors );
        this.factory.setURIResolver( new UriResolverImpl( resourceService ) );
    }

    public void setView( final ResourceKey view )
    {
        this.xsltSource = new StreamSource( new StringReader( resourceService.getResource( view ).readString() ) );
        this.view = view;
    }

    public void setModel( final ScriptValue model )
    {
            this.xmlSource = MapToXmlConverter.toSource( model != null ? model.getMap() : Map.of() );
    }

    public String process()
    {
        try
        {
            return doProcess();
        }
        catch ( final Exception e )
        {
            if ( this.errors.hasErrors() )
            {
                throw handleError( this.errors.iterator().next() );
            }

            throw handleError( e );
        }
    }

    private RuntimeException handleError( final Exception e )
    {
        if ( e instanceof TransformerException )
        {
            throw handleError( (TransformerException) e );
        }

        if ( e instanceof RuntimeException )
        {
            throw (RuntimeException) e;
        }
        throw new RuntimeException( e );
    }

    private RuntimeException handleError( final TransformerException e )
    {
        final SourceLocator locator = e.getLocator();
        final String systemId = locator != null ? locator.getSystemId() : null;

        if ( systemId != null )
        {
            return ResourceProblemException.create().
                lineNumber( locator.getLineNumber() ).
                resource( toResourceKey( systemId ) ).
                cause( e ).
                message( e.getMessage() ).
                build();
        }

        throw new RuntimeException( e );
    }

    private ResourceKey toResourceKey( final String systemId )
    {
        try
        {
            return ResourceKey.from( this.view.getApplicationKey(), new URL( systemId ).getPath() );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Could not resolve XSLT resource path: {}", systemId );
            return this.view;
        }
    }

    private String doProcess()
        throws Exception
    {
        Transformer transformer = this.factory.newTransformer( this.xsltSource );
        transformer.setErrorListener( this.errors );
        transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );

        final StringWriter out = new StringWriter();
        transformer.transform( this.xmlSource, new StreamResult( out ) );
        return out.toString();
    }
}
