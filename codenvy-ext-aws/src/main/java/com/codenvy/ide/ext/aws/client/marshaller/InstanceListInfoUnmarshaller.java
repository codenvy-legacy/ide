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
package com.codenvy.ide.ext.aws.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for instance info list.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InstanceListInfoUnmarshaller implements Unmarshallable<JsonArray<InstanceInfo>> {
    private JsonArray<InstanceInfo> instances;

    /**
     * Create unmarshaller.
     *
     * @param instances
     */
    public InstanceListInfoUnmarshaller(JsonArray<InstanceInfo> instances) {
        this.instances = instances;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        JSONArray array = JSONParser.parseStrict(text).isArray();
        if (array == null) {
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.get(i).isObject();
            String value = (jsonObject.isObject() != null) ? jsonObject.isObject().toString() : "";

            DtoClientImpls.InstanceInfoImpl info = DtoClientImpls.InstanceInfoImpl.deserialize(value);
            instances.add(info);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<InstanceInfo> getPayload() {
        return instances;
    }
}
