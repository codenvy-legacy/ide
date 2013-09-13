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
package org.exoplatform.gwtframework.commons.util;

public class BrowserResolver {
    public static final Browser CURRENT_BROWSER = getCurrentBrowser();

    private static Browser getCurrentBrowser() {
        for (Browser browser : Browser.values()) {
            if (browser.isCurrent()) {
                return browser;
            }
        }

        return null;
    }

    // http://www.javascripter.net/faq/operatin.htm

    /** @return true if the browser is launched within the Mac OS */
    public native static boolean isMacOs() /*-{
        return (navigator.appVersion.indexOf("Mac") != -1);
    }-*/;

    // http://www.quirksmode.org/js/detect.html
    public enum Browser {
        IE("IE", "/MSIE/.test(navigator.userAgent)"),
        SAFARI("safari", "/Apple Computer, Inc/.test(navigator.vendor)"),
        CHROME("chrome", "/Google Inc./.test(navigator.vendor)"),
        FIREFOX("firefox", "/Firefox/.test(navigator.userAgent)");

        private String browser;

        private String test;

        Browser(String browser, String test) {
            this.browser = browser;
            this.test = test;
        }

        @Override
        public String toString() {
            return this.browser;
        }

        public native boolean isCurrent() /*-{
            return eval(this.@org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::test);
        }-*/;
    }
}
