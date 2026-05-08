package com.enonic.lib.xslt;

import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

final class UriResolverImpl
    implements URIResolver
{
    private final ResourceService resourceService;

    public UriResolverImpl( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public Source resolve( final String href, final String base )
        throws TransformerException
    {
        try
        {
            final URI uri = URI.create( base );
            return resolve( href, ResourceKey.from( uri.getSchemeSpecificPart() ) );
        }
        catch ( final Exception e )
        {
            throw new TransformerException( e );
        }
    }

    private Source resolve( final String href, final ResourceKey base )
    {
        final ResourceKey resolvedResourceKey = base.resolve( "../" + href );
        final Resource resolvedResource = resourceService.getResource( resolvedResourceKey );
        if ( !resolvedResource.exists() )
        {
            return null;
        }
        final StreamSource source = new StreamSource( resolvedResource.openStream() );
        source.setSystemId( resolvedResourceKey.toString() );
        return source;
    }
}
