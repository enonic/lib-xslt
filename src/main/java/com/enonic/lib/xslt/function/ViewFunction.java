package com.enonic.lib.xslt.function;

public interface ViewFunction
{
    String getName();

    Object execute( ViewFunctionParams params );
}
