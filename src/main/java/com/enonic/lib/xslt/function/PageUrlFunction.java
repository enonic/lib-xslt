package com.enonic.lib.xslt.function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

import static com.enonic.lib.xslt.function.ParamsHelper.singleValue;

@Component(immediate = true)
public final class PageUrlFunction
    implements ViewFunction
{
    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "pageUrl";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final PageUrlParams urlParams = new PageUrlParams();

        final Multimap<String, String> arguments = params.getArgs();

        urlParams.portalRequest( PortalRequestAccessor.get() ); // TODO: remove this, XP8 must resolve the request
        urlParams.type( singleValue( arguments, "_type" ) );
        urlParams.contextPathType( singleValue( arguments, "_contextPath" ) );
        urlParams.id( singleValue( arguments, "_id" ) );
        urlParams.path( singleValue( arguments, "_path" ) );

        arguments.forEach( ( key, value ) -> urlParams.getParams().put( key, value ) );

        return this.urlService.pageUrl( urlParams );
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
