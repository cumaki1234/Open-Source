package org.sourceforge.kga.wrappers;

import javax.xml.stream.*;
import java.io.InputStream;

public class XmlReader
{

    public static final int START_ELEMENT = XMLStreamReader.START_ELEMENT;
    public static final int END_DOCUMENT = XMLStreamReader.END_DOCUMENT;
    public static final int END_ELEMENT = XMLStreamReader.END_ELEMENT;

    XMLStreamReader xml;

    public XmlReader(InputStream in) throws XmlException
    {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty("javax.xml.stream.isCoalescing", true);
        try
        {
            xml = factory.createXMLStreamReader(in, "UTF-8");
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public boolean hasNext() throws XmlException
    {
        try
        {
            return xml.hasNext();
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public boolean isStartElement()
    {
            return xml.isStartElement();
    }

    public boolean isEndElement()
    {
            return xml.isEndElement();
    }

    public int next() throws XmlException
    {
        try
        {
            return xml.next();
        }
        catch (XMLStreamException e)
        {
            throw new XmlException(e);
        }
    }

    public String getAttributeValue(String namespace, String name)
    {
        return xml.getAttributeValue(namespace, name);
    }
    
    public Double getDoubleAttributeValue(String name) {
    	String val = getAttributeValue("",name);
    	if(val==null||val.length()==0) {
    		return null;
    	}
    	return Double.parseDouble(val);
    }
    
    public Integer getIntAttributeValue(String name) {
    	String val = getAttributeValue("",name);
    	if(val==null||val.length()==0) {
    		return null;
    	}
    	return Integer.parseInt(val);
    }

    public String getLocalName()
    {
        return xml.getLocalName();
    }

    public String getText()
    {
        return xml.getText();
    }
}