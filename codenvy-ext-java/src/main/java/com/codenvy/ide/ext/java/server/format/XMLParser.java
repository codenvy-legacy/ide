package com.codenvy.ide.ext.java.server.format;

import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.util.loging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Nikitenko
 */
public class XMLParser extends DefaultHandler {

    private Map settings;

    public Map getSettings() {
        return settings;
    }

    @Override
    public void startDocument()
            throws SAXException {
        settings = new HashMap<String,String>();
    }

    @Override
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {
        if (qName.equals("setting")) {
            String id = attributes.getValue("id");
            String value = attributes.getValue("value");
            settings.put(id, value);
        }
    }
}
