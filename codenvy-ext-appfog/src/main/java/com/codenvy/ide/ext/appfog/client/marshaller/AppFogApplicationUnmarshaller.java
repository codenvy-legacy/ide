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
package com.codenvy.ide.ext.appfog.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for AppFog application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class AppFogApplicationUnmarshaller implements Unmarshallable<AppfogApplication> {
    private DtoClientImpls.AppfogApplicationImpl application;

    /**
     * Create unmarshaller.
     *
     * @param application
     */
    public AppFogApplicationUnmarshaller(DtoClientImpls.AppfogApplicationImpl application) {
        this.application = application;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.AppfogApplicationImpl application = DtoClientImpls.AppfogApplicationImpl.deserialize(text);

        this.application.setName(application.getName());
        this.application.setUris(application.getUris());
        this.application.setInstances(application.getInstances());
        this.application.setRunningInstances(application.getRunningInstances());
        this.application.setState(application.getState());
        this.application.setServices(application.getServices());
        this.application.setVersion(application.getVersion());
        this.application.setEnv(application.getEnv());
        this.application.setResources(application.getResources());
        this.application.setStaging(application.getStaging());
        this.application.setDebug(application.getDebug());
        this.application.setMeta(application.getMeta());
        this.application.setInfra(application.getInfra());
    }

    /** {@inheritDoc} */
    @Override
    public AppfogApplication getPayload() {
        return application;
    }
}