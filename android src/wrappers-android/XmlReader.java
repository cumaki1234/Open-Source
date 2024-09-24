package org.sourceforge.kga.wrappers;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;

public class XmlReader
{

    public static final int START_ELEMENT = XmlPullParser.START_TAG;
    public static final int END_DOCUMENT = XmlPullParser.END_DOCUMENT;
    public static final int END_ELEMENT = XmlPullParser.END_TAG;

    XmlPullParser parser;
    boolean firstItem = true;
    boolean hasNext = true;

    public XmlReader(InputStream in) throws XmlException
    {
        parser = Xml.newPullParser();
        try
        {
            parser.setInput(in, "UTF-8");
        }
        catch (XmlPullParserException e)
        {
            throw new XmlException(e);
        }
    }

    public boolean hasNext()
    {
        return hasNext;
    }

    public int next() throws XmlException
    {
        try
        {
            if (!firstItem)
                parser.next();
            firstItem = false;
            hasNext = parser.getEventType() != XmlPullParser.END_DOCUMENT;
            return parser.getEventType();
        }
        catch (Exception e)
        {
            throw new XmlException(e);
        }
    }

    public String getAttributeValue(String namespace, String name)
    {
        return parser.getAttributeValue(namespace, name);
    }

    public String getLocalName()
    {
        return parser.getName();
    }

    public String getText()
    {
        return parser.getText();
    }
}