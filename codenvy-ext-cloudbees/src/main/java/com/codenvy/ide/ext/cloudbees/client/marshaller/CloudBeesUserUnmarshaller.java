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