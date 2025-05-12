package com.enonic.lib.xslt.function;

import java.util.Collection;

import com.google.common.collect.Multimap;

final class ParamsHelper
{
    private ParamsHelper()
    {
    }

    public static String singleValue( final Multimap<String, String> map, final String name )
    {
        final Collection<String> values = map.removeAll( name );
        if ( values == null )
        {
            return null;
        }

        if ( values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }
}
