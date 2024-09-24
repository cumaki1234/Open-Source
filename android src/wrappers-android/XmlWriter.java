package org.sourceforge.kga.wrappers;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * Created by tidu8815 on 1/22/2016.
 */
public class XmlWriter
{
    XmlSerializer xml;
    String defaultNs;
    Stack<String> elements = new Stack<>();
    boolean tagIsEmpty = false;

    public XmlWriter(DataOutputStream out, String encoding, String version) throws XmlException
    {
        xml = Xml.newSerializer();
        try
        {
            xml.setOutput(out, encoding);
            xml.startDocument(encoding, true);
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
    }

    public void setDefaultNamespace(String namespace) throws XmlException
    {
        defaultNs = namespace;
    }

    public void writeDefaultNamespace() throws XmlException
    {
        try
        {
            xml.attribute("", "xmlns", defaultNs);
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
    }

    private void checkEmptyElement() throws XmlException
    {
        if (tagIsEmpty)
        {
            try
            {
                xml.endTag("", elements.pop());
            }
            catch (IOException e)
            {
                throw new XmlException(e);
            }
            tagIsEmpty = false;
        }
    }

    private void writeStartElement(String name, boolean empty) throws XmlException
    {
        checkEmptyElement();
        try
        {
            xml.startTag("", name);
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
        elements.push(name);
        tagIsEmpty = empty;
    }

    public void writeStartElement(String name) throws XmlException
    {
        writeStartElement(name, false);
    }

    public void writeEmptyElement(String name) throws XmlException
    {
        writeStartElement(name, true);
    }

    public void writeAttribute(String name, String value) throws XmlException
    {
        try
        {
            xml.attribute("", name, value);
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeCharacters(String s) throws XmlException
    {
        checkEmptyElement();
        try
        {
            xml.text(s);
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeEndElement() throws XmlException
    {
        checkEmptyElement();
        try
        {
            xml.endTag("", elements.pop());
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
    }

    public void flush() throws XmlException
    {
        checkEmptyElement();
        try
        {
            xml.flush();
        }
        catch (IOException e)
        {
            throw new XmlException(e);
        }
    }

    public void close() throws XmlException
    {
        try
        {
            xml.endDocument();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
