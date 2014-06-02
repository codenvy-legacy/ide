/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [$today.year] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.server.format;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
        settings = new HashMap<String, String>();
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
