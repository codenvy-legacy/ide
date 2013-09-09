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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.git.shared.Remote;

import java.util.List;

/**
 * Unmarshaller for list of remote repositories response.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 4, 2011 12:45:37 PM anya $
 */
public class RemoteListUnmarshaller implements Unmarshallable<List<Remote>>, Constants {
    /** Remote repositories. */
    private List<Remote> remotes;

    /**
     * @param remotes remote repositories
     */
    public RemoteListUnmarshaller(List<Remote> remotes) {
        this.remotes = remotes;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
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
            remotes.add(new Remote(name, url));
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<Remote> getPayload() {
        return remotes;
    }
}
