/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
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
