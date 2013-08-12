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

package com.codenvy.ide.util.browser;


import com.codenvy.ide.util.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/** Utility methods relating to the browser. */
public abstract class BrowserUtils {

    private static final BrowserUtils INSTANCE = GWT.create(BrowserUtils.class);

    abstract boolean isFFox();

    static class Chrome extends BrowserUtils {
        Chrome() {
        }

        @Override
        boolean isFFox() {
            return false;
        }
    }

    static class Firefox extends BrowserUtils {
        Firefox() {
        }

        @Override
        boolean isFFox() {
            return true;
        }
    }

    public static boolean isFirefox() {
        return INSTANCE.isFFox();
    }

    public static boolean isChromeOs() {
        return Window.Navigator.getUserAgent().contains(" CrOS ");
    }

    public static boolean hasUrlParameter(String parameter) {
        return Window.Location.getParameter(parameter) != null;
    }

    public static boolean hasUrlParameter(String parameter, String value) {
        return StringUtils.equalNonEmptyStrings(Window.Location.getParameter(parameter), value);
    }

    private BrowserUtils() {
    }
}
