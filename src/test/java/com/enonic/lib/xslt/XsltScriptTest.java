package com.enonic.lib.xslt;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.enonic.lib.xslt.function.ViewFunctionService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XsltScriptTest
    extends ScriptRunnerSupport
{
    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        addService( ViewFunctionService.class, new MockViewFunctionService() );
    }

    @Override
    public String getScriptTestFile()
    {
        return "/test/xslt-test.js";
    }

    private void assertXmlEquals( final String expectedXml, final String actualXml )
    {
        assertEquals( cleanupXml( expectedXml ), cleanupXml( actualXml ) );
    }

    public void assertXmlEquals( final ResourceKey resource, final String actualXml )
    {
        assertXmlEquals( loadResource( resource ).readString(), actualXml );
    }

    private String cleanupXml( final String xml )
    {
        try
        {
            final Document document = DocumentBuilderFactory.newInstance().
                newDocumentBuilder().
                parse( new InputSource( new StringReader( xml ) ) );

            stripWhitespaceTextNodes( document );

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty( OutputKeys.INDENT, "no" );
            final StringWriter writer = new StringWriter();
            transformer.transform( new DOMSource( document ), new StreamResult( writer ) );
            return writer.toString();
        }
        catch ( final Exception e )
        {
            throw new IllegalStateException( e );
        }
    }

    private static void stripWhitespaceTextNodes( final Node node )
    {
        final NodeList children = node.getChildNodes();
        for ( int i = children.getLength() - 1; i >= 0; i-- )
        {
            final Node child = children.item( i );
            if ( child.getNodeType() == Node.TEXT_NODE && child.getNodeValue().trim().isEmpty() )
            {
                node.removeChild( child );
            }
            else
            {
                stripWhitespaceTextNodes( child );
            }
        }
    }
}
