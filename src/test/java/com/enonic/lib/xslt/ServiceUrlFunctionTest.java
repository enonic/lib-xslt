package com.enonic.lib.xslt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.lib.xslt.function.ServiceUrlFunction;
import com.enonic.lib.xslt.function.ViewFunctionParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceUrlFunctionTest
{
    private PortalUrlService urlService;

    private ServiceUrlFunction function;

    @BeforeEach
    void setUp()
    {
        urlService = mock( PortalUrlService.class );
        function = new ServiceUrlFunction( urlService );
    }

    @Test
    void testName()
    {
        assertEquals( "serviceUrl", function.getName() );
    }

    @Test
    void testExecute()
    {
        // prepare
        ViewFunctionParams params = mock( ViewFunctionParams.class );

        Multimap<String, String> args = LinkedHashMultimap.create();
        args.put( "_type", "absolute" );
        args.put( "_contextPath", "relative" );
        args.put( "_service", "serviceName" );
        args.put( "_application", "appName" );
        args.put( "a", "1" );
        args.put( "b", "2" );
        args.put( "b", "3" );

        when( params.getArgs() ).thenReturn( args );

        when( urlService.serviceUrl( any( ServiceUrlParams.class ) ) ).thenReturn( "mockedUrl" );

        // test
        Object result = function.execute( params );

        // verify
        ArgumentCaptor<ServiceUrlParams> captor = ArgumentCaptor.forClass( ServiceUrlParams.class );
        verify( urlService ).serviceUrl( captor.capture() );

        ServiceUrlParams capturedParams = captor.getValue();

        assertEquals( "serviceName", capturedParams.getService() );
        assertEquals( "appName", capturedParams.getApplication() );
        assertEquals( ContextPathType.RELATIVE, capturedParams.getContextPathType() );
        assertEquals( "absolute", capturedParams.getType() );

        Multimap<String, String> queryParams = capturedParams.getParams();

        assertEquals( 1, queryParams.get( "a" ).size() );
        assertTrue( queryParams.get( "a" ).contains( "1" ) );

        assertEquals( 2, queryParams.get( "b" ).size() );
        assertTrue( queryParams.get( "b" ).contains( "2" ) );
        assertTrue( queryParams.get( "b" ).contains( "3" ) );

        assertEquals( "mockedUrl", result );
    }
}
