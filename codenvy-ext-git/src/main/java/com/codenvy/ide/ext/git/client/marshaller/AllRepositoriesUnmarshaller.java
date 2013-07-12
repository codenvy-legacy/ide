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
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import java.util.Set;

/**
 * Umarshaller for all repositories list.
 *
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 */
public class AllRepositoriesUnmarshaller implements Unmarshallable<JsonStringMap<JsonArray<GitHubRepository>>> {
    /** Repositories list. */
    private JsonStringMap<JsonArray<GitHubRepository>> repositories;

    public AllRepositoriesUnmarshaller() {
        this.repositories = JsonCollections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jsonObj = JSONParser.parseStrict(response.getText()).isObject();

        if (jsonObj == null) {
            return;
        }

        Set<String> keys = jsonObj.keySet();

        for (String key : keys) {
            JsonArray<GitHubRepository> repos = JsonCollections.createArray();
            JSONArray jsonArray = jsonObj.get(key).isArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONValue value = jsonArray.get(i);
                String payload = value.isObject().toString();
                DtoClientImpls.GitHubRepositoryImpl repository = DtoClientImpls.GitHubRepositoryImpl.deserialize(payload);
                repos.add(repository);
            }
            repositories.put(key, repos);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonStringMap<JsonArray<GitHubRepository>> getPayload() {
        return repositories;
    }
}