/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.openshift.client.marshaller;

import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import java.util.List;

/**
 * Unmarshaller for Lists.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ListUnmarshaller implements Unmarshallable<List<String>> {

    protected List<String> list;

    /**
     * Create unmarshaller.
     *
     * @param list
     */
    public ListUnmarshaller(List<String> list) {
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) {
        if (response.getText().length() == 0) {
            return;
        }
        JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.get(i).isString().stringValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getPayload() {
        return list;
    }
}