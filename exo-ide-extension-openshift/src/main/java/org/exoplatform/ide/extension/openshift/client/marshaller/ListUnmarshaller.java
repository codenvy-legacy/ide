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
package org.exoplatform.ide.extension.openshift.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ListUnmarshaller implements org.exoplatform.gwtframework.commons.rest.Unmarshallable<List<String>> {

    protected List<String> list;

    public ListUnmarshaller(List<String> list) {
        this.list = list;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
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

    @Override
    public List<String> getPayload() {
        return list;
    }
}