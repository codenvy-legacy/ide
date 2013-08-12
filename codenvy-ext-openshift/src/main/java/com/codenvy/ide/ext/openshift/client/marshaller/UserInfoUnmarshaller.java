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
package com.codenvy.ide.ext.openshift.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.openshift.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for user information.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UserInfoUnmarshaller implements Unmarshallable<RHUserInfo> {
    private DtoClientImpls.RHUserInfoImpl userInfo;

    /**
     * Create unmarshaller.
     *
     * @param userInfo
     */
    public UserInfoUnmarshaller(DtoClientImpls.RHUserInfoImpl userInfo) {
        this.userInfo = userInfo;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public RHUserInfo getPayload() {
        return userInfo;
    }
}
