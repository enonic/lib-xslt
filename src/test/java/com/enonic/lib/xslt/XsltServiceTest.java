package com.enonic.lib.xslt;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;

public class XsltServiceTest
    extends ScriptTestSupport
{
    private XsltService service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        this.service = new XsltService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapp:/test" ) ) );
    }

    @Test
    public void testProcess()
    {
        final XsltProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/test/view/simple.xsl" ) );
        processor.setModel( null );
        processor.process();
    }
}
