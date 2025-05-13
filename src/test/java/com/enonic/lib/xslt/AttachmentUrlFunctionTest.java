package com.enonic.lib.xslt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.lib.xslt.function.AttachmentUrlFunction;
import com.enonic.lib.xslt.function.ViewFunctionParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.PortalUrlService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AttachmentUrlFunctionTest
{
    private PortalUrlService urlService;

    private AttachmentUrlFunction function;

    @BeforeEach
    void setUp()
    {
        urlService = mock( PortalUrlService.class );
        function = new AttachmentUrlFunction();
        function.setUrlService( urlService );
    }

    @Test
    void testName()
    {
        assertEquals( "attachmentUrl", function.getName() );
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
        args.put( "_name", "name" );
        args.put( "_label", "label" );
        args.put( "_download", "true" );
        args.put( "a", "1" );
        args.put( "b", "2" );
        args.put( "b", "3" );

        when( params.getArgs() ).thenReturn( args );

        when( urlService.attachmentUrl( any( AttachmentUrlParams.class ) ) ).thenReturn( "mockedUrl" );

        // test
        Object result = function.execute( params );

        // verify
        ArgumentCaptor<AttachmentUrlParams> captor = ArgumentCaptor.forClass( AttachmentUrlParams.class );
        verify( urlService ).attachmentUrl( captor.capture() );

        AttachmentUrlParams capturedParams = captor.getValue();

        assertEquals( "absolute", capturedParams.getType() );
        assertEquals( ContextPathType.RELATIVE, capturedParams.getContextPathType() );
        assertEquals( "id", capturedParams.getId() );
        assertEquals( "some/path", capturedParams.getPath() );
        assertEquals( "name", capturedParams.getName() );
        assertEquals( "label", capturedParams.getLabel() );
        assertTrue( capturedParams.isDownload() );

        Multimap<String, String> queryParams = capturedParams.getParams();

        assertEquals( 1, queryParams.get( "a" ).size() );
        assertTrue( queryParams.get( "a" ).contains( "1" ) );

        assertEquals( 2, queryParams.get( "b" ).size() );
        assertTrue( queryParams.get( "b" ).contains( "2" ) );
        assertTrue( queryParams.get( "b" ).contains( "3" ) );

        assertEquals( "mockedUrl", result );
    }
}
