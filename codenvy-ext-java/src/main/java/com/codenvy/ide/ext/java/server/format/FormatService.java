/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.server.format;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Nikitenko
 */
@Path("code-formatting/")
public class FormatService {
    private static final Logger LOG = LoggerFactory.getLogger(FormatService.class);

    private final Map<String, String> formaters;

    public FormatService() {
        //TODO: temporary fill in constructor in future rework to User Preference
        formaters = new HashMap<>();
        formaters.put("codenvy", "codenvy-codestyle-eclipse_.xml");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Map<String, String> getFormatters() {
        return formaters;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Map getFormatSettings(@PathParam("id") String id) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLParser parserXML = new XMLParser();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(getClass().getResourceAsStream(formaters.get(id)), parserXML);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("It is not possible to parse file " + formaters.get(id), e);
        }
        return parserXML.getSettings();
    }

}
