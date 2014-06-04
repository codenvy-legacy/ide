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
