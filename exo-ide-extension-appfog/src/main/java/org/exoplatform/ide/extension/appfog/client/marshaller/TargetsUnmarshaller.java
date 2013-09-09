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
package org.exoplatform.ide.extension.appfog.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.List;

/**
 * Unmarshaller for the list of targets, received from server.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class TargetsUnmarshaller implements Unmarshallable<List<String>> {
    private List<String> targets;

    public TargetsUnmarshaller(List<String> targets) {
        this.targets = targets;
    }

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

    @Override
    public List<String> getPayload() {
        return targets;
    }
}
