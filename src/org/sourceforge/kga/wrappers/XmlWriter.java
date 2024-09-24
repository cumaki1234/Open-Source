package org.sourceforge.kga.wrappers;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.DataOutputStream;

/**
 * Created by tidu8815 on 1/22/2016.
 */
public class XmlWriter
{
    XMLStreamWriter xml;
    String defaultNs;

    public XmlWriter(DataOutputStream out, String encoding, String version) throws XmlException
    {
        XMLOutputFactory factory = XMLOutputFactory.newFactory();
        xml = null;
        try
        {
            xml = factory.createXMLStreamWriter(out, "UTF-8");
            xml.writeStartDocument(encoding, version);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void setDefaultNamespace(String namespace) throws XmlException
    {
        try
        {
            defaultNs = namespace;
            xml.setDefaultNamespace(namespace);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeDefaultNamespace() throws XmlException
    {
        try
        {
            xml.writeDefaultNamespace(defaultNs);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeStartElement(String name) throws XmlException
    {
        try {
            xml.writeStartElement(name);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeEmptyElement(String name) throws XmlException
    {
        try {
            xml.writeEmptyElement(name);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeAttribute(String name, String value) throws XmlException
    {
        try {
            xml.writeAttribute(name, value);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeDoubleAttribute(String name, Double value) throws XmlException{
    	if(value==null) {
    		return;
    	}
    	else {
    		writeAttribute(name,value.toString());
    	}
    }

    public void writeIntAttribute(String name, Integer value) throws XmlException{
    	if(value==null) {
    		return;
    	}
    	else {
    		writeAttribute(name,value.toString());
    	}
    }

    public void writeCharacters(String s) throws XmlException
    {
        try
        {
            xml.writeCharacters(s);
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void writeEndElement() throws XmlException
    {
        try
        {
            xml.writeEndElement();
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void flush() throws XmlException
    {
        try
        {
            xml.flush();
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public void close() throws XmlException
    {
        try
        {
            xml.close();
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }
}
