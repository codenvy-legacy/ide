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
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for CloudFoundry provisioned service.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ProvisionedServiceUnmarshaller implements Unmarshallable<AppfogProvisionedService> {
    private DtoClientImpls.AppfogProvisionedServiceImpl service;

    /**
     * Create unmarshaller.
     *
     * @param service
     */
    public ProvisionedServiceUnmarshaller(DtoClientImpls.AppfogProvisionedServiceImpl service) {
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.AppfogProvisionedServiceImpl service = DtoClientImpls.AppfogProvisionedServiceImpl.deserialize(text);

        this.service.setName(service.getName());
        this.service.setType(service.getType());
        this.service.setVendor(service.getVendor());
        this.service.setVendor(service.getVersion());
    }

    /** {@inheritDoc} */
    @Override
    public AppfogProvisionedService getPayload() {
        return service;
    }
}