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
package com.codenvy.ide.extension.android.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AndroidExtensionServiceImpl extends AndroidExtensionService {
    private final String restContext;

    private static final String RUN_APPLICATION = "/ide/android/run";

    public AndroidExtensionServiceImpl(String restContext) {
        this.restContext = restContext;
    }

    @Override
    public void start(String apkUrl, ProjectModel project, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String requestUrl = restContext + RUN_APPLICATION;

        AsyncRequest.build(RequestBuilder.GET, requestUrl + "?apk=" + apkUrl, true)
                    .requestStatusHandler(new StartApplicationStatusHandler(project.getName()))
                    .send(callback);
    }
}
