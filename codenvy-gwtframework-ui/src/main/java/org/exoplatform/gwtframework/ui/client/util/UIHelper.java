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

package org.exoplatform.gwtframework.ui.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SAS .
 *
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
