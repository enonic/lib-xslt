package com.enonic.lib.xslt;

import java.io.StringWriter;
import java.net.URL;

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

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;

public final class XsltProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( XsltProcessor.class );

    private final TransformerFactory factory;

    private final XsltProcessorErrors errors;

    private final UriResolverImpl uriResolver;

    private Source xsltSource;

    private Source xmlSource;

    private Transformer transformer;

    private ResourceService resourceService;

    private ResourceKey view;

    public XsltProcessor( final TransformerFactory factory )
    {
        this.factory = factory;
        this.errors = new XsltProcessorErrors();
        this.uriResolver = new UriResolverImpl();
    }

    public void setView( final ResourceKey view )
    {
        final Resource resource = resourceService.getResource( view );
        this.xsltSource = new StreamSource( resource.getUrl().toString() );
        this.view = view;
    }

    public void setModel( final ScriptValue model )
    {
        if ( model != null )
        {
            this.xmlSource = MapToXmlConverter.toSource( model.getMap() );
        }
        else
        {
            this.xmlSource = MapToXmlConverter.toSource( Maps.newHashMap() );
        }
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
            return handleError( (TransformerException) e );
        }

        if ( e instanceof RuntimeException )
        {
            return (RuntimeException) e;
        }

        return Throwables.propagate( e );
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

        return Throwables.propagate( e );
    }

    private ResourceKey toResourceKey( final String systemId )
    {
        try
        {
            return ResourceKey.from( this.view.getApplicationKey(), new URL( systemId ).getPath() );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Could not resolve XSLT resource path: " + systemId );
            return this.view;
        }
    }

    private String doProcess()
        throws Exception
    {
        createTransformer();

        final StringWriter out = new StringWriter();
        final StreamResult result = new StreamResult( out );

        try
        {
            this.transformer.transform( this.xmlSource, result );
            return out.getBuffer().toString();
        }
        finally
        {
            Closeables.close( out, false );
        }
    }

    protected void createTransformer()
        throws Exception
    {
        this.factory.setErrorListener( this.errors );
        this.factory.setURIResolver( uriResolver );
        this.transformer = this.factory.newTransformer( this.xsltSource );
        this.transformer.setErrorListener( this.errors );
        this.transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
    }

    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.uriResolver.setResourceService( resourceService );
    }
}
