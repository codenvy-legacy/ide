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
import com.codenvy.ide.ext.aws.shared.beanstalk.InstanceLog;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for environment logs.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class EnvironmentsLogListUnmarshaller implements Unmarshallable<JsonArray<InstanceLog>> {
    private JsonArray<InstanceLog> instanceLogs;

    /**
     * Create unmarshaller.
     *
     * @param instanceLogs
     */
    public EnvironmentsLogListUnmarshaller(
            JsonArray<InstanceLog> instanceLogs) {
        this.instanceLogs = instanceLogs;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        JSONArray logsArray = JSONParser.parseStrict(text).isArray();
        if (logsArray == null) {
            return;
        }

        for (int i = 0; i < logsArray.size(); i++) {
            JSONObject env = logsArray.get(i).isObject();
            String value = env != null ? env.isObject().toString() : "";

            DtoClientImpls.InstanceLogImpl instanceLog = DtoClientImpls.InstanceLogImpl.deserialize(value);
            instanceLogs.add(instanceLog);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<InstanceLog> getPayload() {
        return instanceLogs;
    }
}
