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
package com.codenvy.ide.ext.cloudbees.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.cloudbees.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public class ApplicationInfoUnmarshallerWS implements Unmarshallable<ApplicationInfo> {
    private DtoClientImpls.ApplicationInfoImpl applicationInfo;

    /**
     * Create unmarshaller.
     *
     * @param applicationInfo
     */
    public ApplicationInfoUnmarshallerWS(DtoClientImpls.ApplicationInfoImpl applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.ApplicationInfoImpl applicationInfo = DtoClientImpls.ApplicationInfoImpl.deserialize(text);

        this.applicationInfo.setId(applicationInfo.getId());
        this.applicationInfo.setTitle(applicationInfo.getTitle());
        this.applicationInfo.setStatus(applicationInfo.getStatus());
        this.applicationInfo.setUrl(applicationInfo.getUrl());
        this.applicationInfo.setInstances(applicationInfo.getInstances());
        this.applicationInfo.setSecurityMode(applicationInfo.getSecurityMode());
        this.applicationInfo.setMaxMemory(applicationInfo.getMaxMemory());
        this.applicationInfo.setIdleTimeout(applicationInfo.getIdleTimeout());
        this.applicationInfo.setServerPool(applicationInfo.getServerPool());
        this.applicationInfo.setContainer(applicationInfo.getContainer());
        this.applicationInfo.setClusterSize(applicationInfo.getClusterSize());
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationInfo getPayload() {
        return applicationInfo;
    }
}