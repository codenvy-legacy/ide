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
package com.codenvy.ide.ext.openshift.client;

import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftClientService {
    public void login(String login, String password, AsyncRequestCallback<String> callback) throws RequestException;

    public void getUserInfo(boolean appsInfo, AsyncRequestCallback<RHUserInfo> callback) throws RequestException;

    public void createDomain(String name, boolean alter, AsyncRequestCallback<String> callback) throws RequestException;

    public void createApplication(String name, String vfsId, String projectId, String type, boolean scale,
                                  AsyncRequestCallback<AppInfo> callback) throws RequestException;

    public void createApplicationWS(String name, String vfsId, String projectId, String type, boolean scale,
                                    RequestCallback<AppInfo> callback) throws WebSocketException;

    public void destroyApplication(String name, String vfsId, String projectId, AsyncRequestCallback<String> callback)
            throws RequestException;

    public void getApplicationInfo(String applicationName, String vfsId, String projectId,
                                   AsyncRequestCallback<AppInfo> callback) throws RequestException;

    public void getApplicationTypes(AsyncRequestCallback<List<String>> callback) throws RequestException;

    public void startApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void stopApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void restartApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void getApplicationHealth(String appName, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    public void getCartridges(AsyncRequestCallback<List<String>> callback) throws RequestException;

    public void addCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void deleteCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void startCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void stopCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void restartCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void reloadCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    public void destroyAllApplications(boolean alsoNamespace, String vfsId, String projectId, AsyncRequestCallback<Void> callback)
            throws RequestException;
}
