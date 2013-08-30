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
package org.exoplatform.gwtframework.commons.rest;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProxyUtil {

    private static native String getProxyServiceContext() /*-{
        return $wnd.proxyServiceContext;
    }-*/;

    private static String getCurrentHost() {
        String currentHost = Location.getProtocol() + "//" + Location.getHost();
        return currentHost;
    }

    public static String getCheckedURL(String url) {
        String proxyServiceContext = getProxyServiceContext();
        if (proxyServiceContext == null || "".equals(proxyServiceContext)) {
            return url;
        }

        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            return url;
        }

        String currentHost = getCurrentHost();
        if (url.startsWith(currentHost)) {
            return url;
        }

        return proxyServiceContext + "?url=" + URL.encodeQueryString(url);
    }

}
