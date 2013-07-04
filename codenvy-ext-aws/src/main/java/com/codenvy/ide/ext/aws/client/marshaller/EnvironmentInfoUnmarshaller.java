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
package com.codenvy.ide.ext.aws.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for environment info.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class EnvironmentInfoUnmarshaller implements Unmarshallable<EnvironmentInfo> {
    private DtoClientImpls.EnvironmentInfoImpl environmentInfo;

    /**
     * Create unmarshaller.
     *
     * @param environmentInfo
     */
    public EnvironmentInfoUnmarshaller(DtoClientImpls.EnvironmentInfoImpl environmentInfo) {
        this.environmentInfo = environmentInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject environmentObject = JSONParser.parseStrict(text).isObject();
        if (environmentObject == null) {
            return;
        }

        DtoClientImpls.EnvironmentInfoImpl dtoEnvironmentInfo = DtoClientImpls.EnvironmentInfoImpl.deserialize(text);
        environmentInfo.setSolutionStackName(dtoEnvironmentInfo.getSolutionStackName());
        environmentInfo.setVersionLabel(dtoEnvironmentInfo.getVersionLabel());
        environmentInfo.setApplicationName(dtoEnvironmentInfo.getApplicationName());
        environmentInfo.setDescription(dtoEnvironmentInfo.getDescription());
        environmentInfo.setCname(dtoEnvironmentInfo.getCname());
        environmentInfo.setCreated(dtoEnvironmentInfo.getCreated());
        environmentInfo.setEndpointUrl(dtoEnvironmentInfo.getEndpointUrl());
        environmentInfo.setHealth(dtoEnvironmentInfo.getHealth());
        environmentInfo.setStatus(dtoEnvironmentInfo.getStatus());
        environmentInfo.setTemplateName(dtoEnvironmentInfo.getTemplateName());
        environmentInfo.setUpdated(dtoEnvironmentInfo.getUpdated());
        environmentInfo.setId(dtoEnvironmentInfo.getId());
        environmentInfo.setName(dtoEnvironmentInfo.getName());
    }

    /** {@inheritDoc} */
    @Override
    public EnvironmentInfo getPayload() {
        return environmentInfo;
    }
}
