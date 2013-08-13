/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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