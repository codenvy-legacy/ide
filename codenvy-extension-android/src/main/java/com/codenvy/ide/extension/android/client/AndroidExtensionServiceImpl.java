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
package com.codenvy.ide.extension.android.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AndroidExtensionServiceImpl extends AndroidExtensionService {
    private final String restContext;

    private static final String RUN_APPLICATION = Utils.getWorkspaceName() + "/android/run";

    public AndroidExtensionServiceImpl(String restContext) {
        this.restContext = restContext;
    }

    @Override
    public void start(String apkUrl, String oauthToken, ProjectModel project, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        String requestUrl = restContext + RUN_APPLICATION;

        String params = new StringBuilder().append("apk=").append(apkUrl).append("&oauth_token=").append(oauthToken).append("&projectname=")
                                           .append(project.getName()).append("&projecttype=").append(project.getProjectType()).toString();
        AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
                    .requestStatusHandler(new StartApplicationStatusHandler(project.getName()))
                    .data(params).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED).send(callback);
    }
}
