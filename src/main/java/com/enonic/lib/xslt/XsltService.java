package com.enonic.lib.xslt;

import java.util.function.Supplier;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.lib.xslt.function.ViewFunctionService;
import com.enonic.lib.xslt.function.XsltFunctionLibrary;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class XsltService
    implements ScriptBean
{
    private final Configuration configuration;

    private Supplier<ResourceService> resourceServiceSupplier;

    public XsltService()
    {
        this.configuration = new Configuration();
        this.configuration.setLineNumbering( true );
        this.configuration.setCompileWithTracing( true );
        this.configuration.setValidationWarnings( true );
    }

    public XsltProcessor newProcessor()
    {
        return new XsltProcessor( resourceServiceSupplier.get(), new TransformerFactoryImpl( this.configuration ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.resourceServiceSupplier = context.getService( ResourceService.class );
        final Supplier<ViewFunctionService> service = context.getService( ViewFunctionService.class );
        new XsltFunctionLibrary( service ).registerAll( this.configuration );
    }
}
