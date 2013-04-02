/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.java.client.templates;

import com.codenvy.ide.java.client.templates.api.Template;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:48:13 PM 34360 2009-07-22 23:58:59Z evgen $
 */
public class TemplateStore {

    protected interface Templates extends ClientBundle {
        @Source("templates.js")
        TextResource templatesText();

        @Source("codeTemplates.js")
        TextResource codeTemplatesText();
    }

    private static Template[] templates;

    private static Template[] codeTemplates;

    /**
     *
     */
    public TemplateStore() {
        if (templates == null) {
            parseTemplates();
        }
    }

    /** @return  */
    public Template[] getTemplates() {
        return templates;
    }

    /**
     *
     */
    private void parseTemplates() {
        Templates templatesText = GWT.create(Templates.class);
        JSONObject jsonObject = JSONParser.parseLenient(templatesText.templatesText().getText()).isObject();
        JSONArray templatesJson = jsonObject.get("templates").isArray();
        templates = new Template[templatesJson.size()];
        fillTemplates(templatesJson, templates);

        jsonObject = JSONParser.parseLenient(templatesText.codeTemplatesText().getText()).isObject();
        templatesJson = jsonObject.get("templates").isArray();
        codeTemplates = new Template[templatesJson.size()];
        fillTemplates(templatesJson, codeTemplates);
    }

    /** @param templatesJson */
    private void fillTemplates(JSONArray templatesJson, Template[] templates) {
        String name, description, contextTypeId, pattern, id = null;
        boolean isAutoInsertable = false;
        for (int i = 0; i < templatesJson.size(); i++) {
            JSONObject tem = templatesJson.get(i).isObject();
            name = tem.get("name").isString().stringValue();
            description = tem.get("description").isString().stringValue();
            contextTypeId = tem.get("context").isString().stringValue();
            pattern = tem.get("text").isString().stringValue();
            isAutoInsertable = tem.get("autoinsert").isBoolean().booleanValue();
            id = tem.get("id").isString().stringValue();
            templates[i] = new Template(id, name, description, contextTypeId, pattern, isAutoInsertable);
        }
    }

    /**
     * @param contextTypeId
     * @return
     */
    public Template[] getTemplates(String contextTypeId) {
        List<Template> temList = new ArrayList<Template>();
        for (Template t : templates) {
            if (t.getContextTypeId().equals(contextTypeId))
                temList.add(t);
        }

        return temList.toArray(new Template[temList.size()]);
    }

    /**
     * @param id
     * @return
     */
    public Template findTemplateById(String id) {
        for (Template t : codeTemplates) {
            if (t.getId().equals(id))
                return t;
        }
        return null;
    }

}
