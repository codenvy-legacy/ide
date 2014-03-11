package com.codenvy.ide.ext.java.server.format;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Map;

/**
 * @author Roman Nikitenko
 */
@Path("formattingSettings/{ws-id}")
public class FormatService {
    private static final Logger LOG = LoggerFactory.getLogger(FormatService.class);

    @GET
    @Path("codenvy")
    @Produces({MediaType.APPLICATION_JSON})
    public Map getFormatSettings() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLParser parserXML = new XMLParser();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(getClass().getResourceAsStream("codenvy-codestyle-eclipse_.xml"), parserXML);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("It is not possible to parse file 'codenvy-codestyle-eclipse_.xml'", e);
        }
        return parserXML.getSettings();
    }

}
