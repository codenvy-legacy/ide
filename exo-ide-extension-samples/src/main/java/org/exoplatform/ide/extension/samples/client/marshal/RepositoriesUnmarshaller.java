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
package org.exoplatform.ide.extension.samples.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.git.shared.GitHubRepository;

import java.util.List;

/**
 * Umarshaller for repositories list.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Apr 2, 2012 10:39:45 AM anya $
 */
public class RepositoriesUnmarshaller implements Unmarshallable<List<GitHubRepository>> {
    /** Repositories list. */
    private List<GitHubRepository> repositories;

    /**
     * @param repositories
     *         extended repositories
     */
    public RepositoriesUnmarshaller(List<GitHubRepository> repositories) {
        this.repositories = repositories;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();

        if (jsonArray == null) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONValue value = jsonArray.get(i);
            String payload = value.isObject().toString();
            AutoBean<GitHubRepository> autoBean =
                    AutoBeanCodex.decode(SamplesExtension.AUTO_BEAN_FACTORY, GitHubRepository.class, payload);
            repositories.add(autoBean.as());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<GitHubRepository> getPayload() {
        return repositories;
    }
}
