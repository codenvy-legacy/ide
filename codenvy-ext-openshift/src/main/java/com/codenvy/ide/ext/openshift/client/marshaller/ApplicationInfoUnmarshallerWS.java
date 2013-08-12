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
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for application info for websockets.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoUnmarshallerWS implements Unmarshallable<AppInfo> {
    private DtoClientImpls.AppInfoImpl appInfo;

    /**
     * Create unmarshaller.
     *
     * @param appInfo
     */
    public ApplicationInfoUnmarshallerWS(DtoClientImpls.AppInfoImpl appInfo) {
        this.appInfo = appInfo;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public AppInfo getPayload() {
        return appInfo;
    }
}
