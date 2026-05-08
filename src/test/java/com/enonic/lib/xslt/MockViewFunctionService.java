package com.enonic.lib.xslt;

import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;

import com.enonic.lib.xslt.function.ViewFunctionParams;
import com.enonic.lib.xslt.function.ViewFunctionService;

public final class MockViewFunctionService
    implements ViewFunctionService
{
    @Override
    public Object execute( final ViewFunctionParams params )
    {
        return params.getName() + "(" + sortedToString( params.getArgs() ) + ")";
    }

    private static String sortedToString( final Multimap<String, String> args )
    {
        return new TreeMap<>( args.asMap() ).entrySet().stream().
            map( e -> e.getKey() + "=" + e.getValue() ).
            collect( Collectors.joining( ", ", "{", "}" ) );
    }
}
