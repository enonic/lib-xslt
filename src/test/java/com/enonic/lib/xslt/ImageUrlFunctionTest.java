package com.enonic.lib.xslt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.lib.xslt.function.ImageUrlFunction;
import com.enonic.lib.xslt.function.ViewFunctionParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageUrlFunctionTest
{
    private PortalUrlService urlService;

    private ImageUrlFunction function;

    @BeforeEach
    void setUp()
    {
        urlService = mock( PortalUrlService.class );
        function = new ImageUrlFunction();
        function.setUrlService( urlService );
    }

    @Test
    void testName()
    {
        assertEquals( "imageUrl", function.getName() );
    }

    @Test
    void testExecute()
    {
        // prepare
        ViewFunctionParams params = mock( ViewFunctionParams.class );

        Multimap<String, String> args = LinkedHashMultimap.create();
        args.put( "_type", "absolute" );
        args.put( "_contextPath", "relative" );
        args.put( "_id", "id" );
        args.put( "_path", "some/path" );
        args.put( "_format", "format" );
        args.put( "_quality", "10" );
        args.put( "_filter", "filter" );
        args.put( "_background", "background" );
        args.put( "_scale", "scale" );
        args.put( "a", "1" );
        args.put( "b", "2" );
        args.put( "b", "3" );

        when( params.getArgs() ).thenReturn( args );

        when( urlService.imageUrl( any( ImageUrlParams.class ) ) ).thenReturn( "mockedUrl" );

        // test
        Object result = function.execute( params );

        // verify
        ArgumentCaptor<ImageUrlParams> captor = ArgumentCaptor.forClass( ImageUrlParams.class );
        verify( urlService ).imageUrl( captor.capture() );

        ImageUrlParams capturedParams = captor.getValue();

        assertEquals( "absolute", capturedParams.getType() );
        assertEquals( ContextPathType.RELATIVE, capturedParams.getContextPathType() );
        assertEquals( "id", capturedParams.getId() );
        assertEquals( "some/path", capturedParams.getPath() );
        assertEquals( "format", capturedParams.getFormat() );
        assertEquals( "filter", capturedParams.getFilter() );
        assertEquals( 10, capturedParams.getQuality() );
        assertEquals( "background", capturedParams.getBackground() );
        assertEquals( "scale", capturedParams.getScale() );

        Multimap<String, String> queryParams = capturedParams.getParams();

        assertEquals( 1, queryParams.get( "a" ).size() );
        assertTrue( queryParams.get( "a" ).contains( "1" ) );

        assertEquals( 2, queryParams.get( "b" ).size() );
        assertTrue( queryParams.get( "b" ).contains( "2" ) );
        assertTrue( queryParams.get( "b" ).contains( "3" ) );

        assertEquals( "mockedUrl", result );
    }
}
