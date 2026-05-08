package com.enonic.lib.xslt;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class MapToXmlConverter
{
    public static Source toSource( final Map<String, Object> map )
    {
        final Document document = newDocument();
        final Element root = document.createElement( "root" );
        document.appendChild( root );
        serializeMap( document, root, map );
        return new DOMSource( document );
    }

    private static void serializeMap( final Document document, final Element parent, final Map<?, ?> map )
    {
        for ( final Map.Entry<?, ?> item : map.entrySet() )
        {
            final Element child = document.createElement( item.getKey().toString() );
            parent.appendChild( child );
            serializeObject( document, child, item.getValue() );
        }
    }

    private static void serializeObject( final Document document, final Element parent, final Object value )
    {
        if ( value instanceof List )
        {
            serializeList( document, parent, (List<?>) value );
        }
        else if ( value instanceof Map )
        {
            serializeMap( document, parent, (Map<?, ?>) value );
        }
        else
        {
            parent.appendChild( document.createTextNode( value.toString() ) );
        }
    }

    private static void serializeList( final Document document, final Element parent, final List<?> list )
    {
        for ( final Object item : list )
        {
            final Element child = document.createElement( "item" );
            parent.appendChild( child );
            serializeObject( document, child, item );
        }
    }

    private static Document newDocument()
    {
        try
        {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch ( final ParserConfigurationException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
