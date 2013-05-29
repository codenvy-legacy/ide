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
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesUser;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for CloudBees user.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CloudBeesUserUnmarshaller implements Unmarshallable<CloudBeesUser> {
    private DtoClientImpls.CloudBeesUserImpl user;

    /**
     * Create unmarshaller.
     *
     * @param user
     */
    public CloudBeesUserUnmarshaller(DtoClientImpls.CloudBeesUserImpl user) {
        this.user = user;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.CloudBeesUserImpl user = DtoClientImpls.CloudBeesUserImpl.deserialize(text);

        this.user.setEmail(user.getEmail());
        this.user.setFirst_name(user.getFirst_name());
        this.user.setLast_name(user.getLast_name());
        this.user.setName(user.getName());
        this.user.setPassword(user.getPassword());
        this.user.setRole(user.getRole());
        this.user.setId(user.getId());
        this.user.setAccounts(user.getAccounts());
        this.user.setSsh_keys(user.getSsh_keys());
    }

    /** {@inheritDoc} */
    @Override
    public CloudBeesUser getPayload() {
        return user;
    }
}