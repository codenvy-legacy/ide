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
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for application info.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoUnmarshaller implements Unmarshallable<ApplicationInfo> {
    private DtoClientImpls.ApplicationInfoImpl applicationInfo;

    /**
     * Create unmarshaller.
     *
     * @param applicationInfo
     */
    public ApplicationInfoUnmarshaller(DtoClientImpls.ApplicationInfoImpl applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject appInfoObject = JSONParser.parseStrict(text).isObject();
        if (appInfoObject == null) {
            return;
        }

        DtoClientImpls.ApplicationInfoImpl dtoAppInfo = DtoClientImpls.ApplicationInfoImpl.deserialize(text);
        applicationInfo.setName(dtoAppInfo.getName());
        applicationInfo.setDescription(dtoAppInfo.getDescription());
        applicationInfo.setUpdated(dtoAppInfo.getUpdated());
        applicationInfo.setCreated(dtoAppInfo.getCreated());
        applicationInfo.setVersions(dtoAppInfo.getVersions());
        applicationInfo.setConfigurationTemplates(dtoAppInfo.getConfigurationTemplates());
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationInfo getPayload() {
        return applicationInfo;
    }
}
