package com.enonic.lib.xslt;

import com.enonic.lib.xslt.function.ViewFunctionParams;
import com.enonic.lib.xslt.function.ViewFunctionService;

public final class MockViewFunctionService
    implements ViewFunctionService
{
    @Override
    public Object execute( final ViewFunctionParams params )
    {
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }
}
