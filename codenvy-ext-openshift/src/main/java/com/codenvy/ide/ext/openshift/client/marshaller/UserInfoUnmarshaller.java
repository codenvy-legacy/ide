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
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UserInfoUnmarshaller implements Unmarshallable<RHUserInfo> {
    private DtoClientImpls.RHUserInfoImpl userInfo;

    public UserInfoUnmarshaller(DtoClientImpls.RHUserInfoImpl userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.RHUserInfoImpl userInfo = DtoClientImpls.RHUserInfoImpl.deserialize(text);

        this.userInfo.setRhlogin(userInfo.getRhlogin());
        this.userInfo.setNamespace(userInfo.getNamespace());
        this.userInfo.setRhcDomain(userInfo.getRhcDomain());
        this.userInfo.setUuid(userInfo.getUuid());
        this.userInfo.setApps(userInfo.getApps());
    }

    @Override
    public RHUserInfo getPayload() {
        return userInfo;
    }
}
