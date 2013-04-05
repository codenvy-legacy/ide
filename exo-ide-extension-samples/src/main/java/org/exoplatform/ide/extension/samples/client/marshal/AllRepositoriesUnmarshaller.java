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
package org.exoplatform.ide.extension.samples.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.git.shared.GitHubRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Umarshaller for all repositories list.
 * 
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 */
public class AllRepositoriesUnmarshaller implements Unmarshallable<Map<String, List<GitHubRepository>>> {
    /** Repositories list. */
    private Map<String, List<GitHubRepository>> repositories;

    /**
     * @param repositories extended repositories
     */
    public AllRepositoriesUnmarshaller(Map<String, List<GitHubRepository>> repositories) {
        this.repositories = repositories;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jsonObj = JSONParser.parseStrict(response.getText()).isObject();

        if (jsonObj == null) {
            return;
        }

        Set<String> keys = jsonObj.keySet();

        for (String key : keys) {
            List<GitHubRepository> repos = new ArrayList<GitHubRepository>();
            JSONArray jsonArray = jsonObj.get(key).isArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONValue value = jsonArray.get(i);
                String payload = value.isObject().toString();
                AutoBean<GitHubRepository> autoBean = AutoBeanCodex.decode(SamplesExtension.AUTO_BEAN_FACTORY,
                                                                           GitHubRepository.class,
                                                                           payload);
                repos.add(autoBean.as());
            }
            repositories.put(key, repos);
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public Map<String, List<GitHubRepository>> getPayload() {
        return repositories;
    }
}
