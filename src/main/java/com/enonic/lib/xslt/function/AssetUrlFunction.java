package com.enonic.lib.xslt.function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

import static com.enonic.lib.xslt.function.ParamsHelper.singleValue;

@Component(immediate = true)
public final class AssetUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "assetUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final AssetUrlParams urlParams = new AssetUrlParams();

        final Multimap<String, String> arguments = params.getArgs();

        urlParams.portalRequest( PortalRequestAccessor.get() ); // TODO: remove this, XP8 must resolve the request
        urlParams.path( singleValue( arguments, "_path" ) );
        urlParams.application( singleValue( arguments, "_application" ) );
        urlParams.contextPathType( singleValue( arguments, "_contextPath" ) );
        urlParams.type( singleValue( arguments, "_type" ) );

        arguments.forEach( ( key, value ) -> urlParams.getParams().put( key, value ) );

        return this.urlService.assetUrl( urlParams );
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
