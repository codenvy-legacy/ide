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

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ExoStyle {

    public static interface Style {

        static final String BLANK = "exo-blank";

    }

    private static String eXoStyleURL;

    public static String getEXoStyleURL() {
        return eXoStyleURL;
    }

    static {
        eXoStyleURL = GWT.getModuleBaseURL();

        if (eXoStyleURL.endsWith("/")) {
            eXoStyleURL = eXoStyleURL.substring(0, eXoStyleURL.length() - 1);
        }
        eXoStyleURL += "/eXoStyle/skin/default/images/";
    }

    public static String getBlankImage() {
        return "<img src=\"" + eXoStyleURL + "blank.gif" + "\" class=\"" + Style.BLANK + "\" />";
    }

}
