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
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.SystemInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for AppFog system info.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class SystemInfoUnmarshaller implements Unmarshallable<SystemInfo> {
    private DtoClientImpls.SystemInfoImpl systemInfo;

    /**
     * Create unmarshaller.
     *
     * @param systemInfo
     */
    public SystemInfoUnmarshaller(DtoClientImpls.SystemInfoImpl systemInfo) {
        this.systemInfo = systemInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.SystemInfoImpl systemInfo = DtoClientImpls.SystemInfoImpl.deserialize(text);

        this.systemInfo.setUsage(systemInfo.getUsage());
        this.systemInfo.setLimits(systemInfo.getLimits());
        this.systemInfo.setDescription(systemInfo.getDescription());
        this.systemInfo.setUser(systemInfo.getUser());
        this.systemInfo.setVersion(systemInfo.getVersion());
        this.systemInfo.setName(systemInfo.getName());
        this.systemInfo.setSupport(systemInfo.getSupport());
        this.systemInfo.setFrameworks(systemInfo.getFrameworks());
    }

    /** {@inheritDoc} */
    @Override
    public DtoClientImpls.SystemInfoImpl getPayload() {
        return systemInfo;
    }
}