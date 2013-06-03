/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.cloudfoundry.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for CloudFoundry provisioned service.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ProvisionedServiceUnmarshaller implements Unmarshallable<ProvisionedService> {
    private DtoClientImpls.ProvisionedServiceImpl service;

    /**
     * Create unmarshaller.
     *
     * @param service
     */
    public ProvisionedServiceUnmarshaller(DtoClientImpls.ProvisionedServiceImpl service) {
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.ProvisionedServiceImpl service = DtoClientImpls.ProvisionedServiceImpl.deserialize(text);

        this.service.setName(service.getName());
        this.service.setType(service.getType());
        this.service.setVendor(service.getVendor());
        this.service.setVendor(service.getVersion());
    }

    /** {@inheritDoc} */
    @Override
    public ProvisionedService getPayload() {
        return service;
    }
}