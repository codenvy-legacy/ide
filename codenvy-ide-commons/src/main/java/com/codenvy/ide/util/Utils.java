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
package com.codenvy.ide.util;

import com.google.gwt.user.client.Window;

/**
 * A smattering of useful methods.
 *
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 */
public class Utils {
    /** @return workspace name */
    public static native String getWorkspaceName() /*-{
        return $wnd.ws;
    }-*/;

    /** @return <code>true</code> if Codenvy launched in SDK runner and <code>false</code> otherwise */
    public static boolean isAppLaunchedInSDKRunner() {
        return Window.Location.getParameter("h") != null && Window.Location.getParameter("p") != null;
    }
}