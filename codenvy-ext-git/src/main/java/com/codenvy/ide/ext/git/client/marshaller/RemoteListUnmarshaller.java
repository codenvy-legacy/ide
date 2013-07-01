/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for list of remote repositories response.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 4, 2011 12:45:37 PM anya $
 */
public class RemoteListUnmarshaller implements Unmarshallable<JsonArray<Remote>>, Constants {
    /** Remote repositories. */
    private JsonArray<Remote> remotes;

    /**
     * @param remotes
     *         remote repositories
     */
    public RemoteListUnmarshaller(JsonArray<Remote> remotes) {
        this.remotes = remotes;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
        if (array == null || array.size() <= 0)
            return;

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();
            if (object == null)
                continue;
            String name = "";
            String url = "";
            if (object.containsKey(NAME)) {
                name = (object.get(NAME).isString() != null) ? object.get(NAME).isString().stringValue() : name;
            }
            if (object.containsKey(URL)) {
                url = (object.get(URL).isString() != null) ? object.get(URL).isString().stringValue() : url;
            }
            DtoClientImpls.RemoteImpl remote = DtoClientImpls.RemoteImpl.make();
            remote.setName(name);
            remote.setUrl(url);

            remotes.add(remote);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<Remote> getPayload() {
        return remotes;
    }
}