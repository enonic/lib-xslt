package com.enonic.lib.xslt.function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

import static com.enonic.lib.xslt.function.ParamsHelper.singleValue;

@Component(immediate = true)
public final class ImageUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "imageUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final ImageUrlParams urlParams = new ImageUrlParams();

        final Multimap<String, String> arguments = params.getArgs();

        urlParams.portalRequest( PortalRequestAccessor.get() ); // TODO: remove this, XP8 must resolve the request
        urlParams.type( singleValue( arguments, "_type" ) );
        urlParams.contextPathType( singleValue( arguments, "_contextPath" ) );
        urlParams.id( singleValue( arguments, "_id" ) );
        urlParams.path( singleValue( arguments, "_path" ) );
        urlParams.format( singleValue( arguments, "_format" ) );
        urlParams.quality( singleValue( arguments, "_quality" ) );
        urlParams.filter( singleValue( arguments, "_filter" ) );
        urlParams.background( singleValue( arguments, "_background" ) );
        urlParams.scale( singleValue( arguments, "_scale" ) );

        arguments.forEach( ( key, value ) -> urlParams.getParams().put( key, value ) );

        return this.urlService.imageUrl( urlParams );
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
