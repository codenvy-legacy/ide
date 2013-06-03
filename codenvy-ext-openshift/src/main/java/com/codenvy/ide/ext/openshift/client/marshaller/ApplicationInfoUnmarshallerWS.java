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
package com.codenvy.ide.ext.openshift.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.openshift.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoUnmarshallerWS implements Unmarshallable<AppInfo> {
    private DtoClientImpls.AppInfoImpl appInfo;

    public ApplicationInfoUnmarshallerWS(DtoClientImpls.AppInfoImpl appInfo) {
        this.appInfo = appInfo;
    }

    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.AppInfoImpl application = DtoClientImpls.AppInfoImpl.deserialize(text);

        this.appInfo.setName(application.getName());
        this.appInfo.setType(application.getType());
        this.appInfo.setCreationTime(application.getCreationTime());
        this.appInfo.setPublicUrl(application.getPublicUrl());
        this.appInfo.setGitUrl(application.getGitUrl());
        this.appInfo.setEmbeddedCartridges(application.getEmbeddedCartridges());
    }

    @Override
    public AppInfo getPayload() {
        return appInfo;
    }
}
