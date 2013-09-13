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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 26, 2011 5:00:39 PM anya $
 */
public class VirtualFileSystemFactory {
    /** VFS factory instance. */
    private static VirtualFileSystemFactory instance;

    private final String VFS_URL = "/" + getWorkspaceName() + "/vfs";

    private String restContext;

    public static VirtualFileSystemFactory getInstance() {
        return instance;
    }

    /** @param workspaceURL */
    public VirtualFileSystemFactory(String restContext) {
        instance = this;
        this.restContext = restContext;
    }
    
    public static native String getWorkspaceName() /*-{
       return $wnd.ws;
    }-*/;

    public void getAvailableFileSystems(AsyncRequestCallback<List<VirtualFileSystemInfo>> callback)
            throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, restContext + VFS_URL).send(callback);
    }
}
