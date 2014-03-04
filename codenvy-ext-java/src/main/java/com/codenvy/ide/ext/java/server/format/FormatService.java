package com.codenvy.ide.ext.java.server.format;

import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.util.loging.Log;

import org.xml.sax.SAXException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Roman Nikitenko
 */
@Path("formattingSettings/{ws-id}")
public class FormatService {

    @GET
    @Path("codenvy")
    public JsoStringMap<String> getFormatSettings(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLParser parserXML = new XMLParser();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(new FileInputStream("resources/codenvy-codestyle-eclipse_.xml"), parserXML);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.error(getClass(), e);
        }
        return parserXML.getSettings();
    }

}
