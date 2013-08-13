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
package com.codenvy.ide.ext.appfog.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Unmarshaller for the list of targets, received from server.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public class TargetsUnmarshaller implements Unmarshallable<JsonArray<String>> {
    private JsonArray<String> targets;

    /**
     * Create unmarshaller.
     *
     * @param targets
     */
    public TargetsUnmarshaller(JsonArray<String> targets) {
        this.targets = targets;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONValue value = jsonArray.get(i);
            if (value.isString() != null) {
                targets.add(value.isString().stringValue());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getPayload() {
        return targets;
    }
}