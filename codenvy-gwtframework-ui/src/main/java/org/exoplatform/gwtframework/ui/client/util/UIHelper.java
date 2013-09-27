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

package org.exoplatform.gwtframework.ui.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UIHelper {

    // ------------- JSNI methods
    public static native void initGadget() /*-{
        // set width of gadget to 100%
        if ($wnd.frameElement == null) {
            return;
        }
        $wnd.frameElement.style.width = "100%";
    }-*/;

    private static String gadgetURL;

    private static String gadgetImagesURL;

    private static native String initGadgetUrl() /*-{
        // gathering the gadget's URL from the properties url of document.URL
        if ($wnd.gadgets == null) {
            return "";
        }
        return $wnd.gadgets.util.getUrlParameters().url.match(/(.*)\//)[1];
    }-*/;

    public static String getGadgetImagesURL() {
        return gadgetImagesURL;
    }

    public static String getGadgetURL() {
        return gadgetURL;
    }

    static {
        gadgetURL = initGadgetUrl();
        gadgetImagesURL = GWT.getModuleBaseURL();
        if (gadgetImagesURL.endsWith("/")) {
            gadgetImagesURL = gadgetImagesURL.substring(0, gadgetImagesURL.length() - 1);
        }
        gadgetImagesURL += "/images/";
    }

    private static ArrayList<String> fieldNames = new ArrayList<String>();

    private static Timer setReadOnlyTimer = new Timer() {

        @Override
        public void run() {
            for (String fieldName : fieldNames) {
                try {
                    readOnly(fieldName);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }

            fieldNames.clear();
        }

    };

    public static void setAsReadOnly(String fieldName) {
        fieldNames.add(fieldName);
        setReadOnlyTimer.schedule(500);
    }

    private static native void readOnly(String objectName) /*-{
        if (objectName == null) {
            return;
        }

        if ($wnd.document.getElementsByName(objectName) == null) {
            return;
        }
        var textField = $wnd.document.getElementsByName(objectName)[0];
        if (textField == null) {
            return;
        }
        textField.setAttribute("readOnly", "true");
    }-*/;

}
