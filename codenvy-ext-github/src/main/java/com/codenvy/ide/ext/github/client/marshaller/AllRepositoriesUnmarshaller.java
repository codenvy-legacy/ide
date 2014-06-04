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
package com.codenvy.ide.ext.github.client.marshaller;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import java.util.Set;

/**
 * Unmarshaller for all repositories list.
 *
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 */
public class AllRepositoriesUnmarshaller implements Unmarshallable<StringMap<Array<GitHubRepository>>> {
    /** Repositories list. */
    private StringMap<Array<GitHubRepository>> repositories;
    private DtoFactory                         dtoFactory;

    public AllRepositoriesUnmarshaller(DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jsonObj = JSONParser.parseStrict(response.getText()).isObject();

        if (jsonObj == null) {
            return;
        }

        Set<String> keys = jsonObj.keySet();
        repositories = Collections.createStringMap();

        for (String key : keys) {
            JSONArray jsonArray = jsonObj.get(key).isArray();
            Array<GitHubRepository> repos = dtoFactory.createListDtoFromJson(jsonArray.toString(), GitHubRepository.class);
            repositories.put(key, repos);
        }
    }

    /** {@inheritDoc} */
    @Override
    public StringMap<Array<GitHubRepository>> getPayload() {
        return repositories;
    }
}