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

package org.exoplatform.ide.client.project.properties;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PropertyUtil {

    public static String getHumanReadableName(String name) {
        String result = "";

        if (name.indexOf(":") >= 0) {
            name = name.substring(name.indexOf(":") + 1);
        }

        String upper = name.toUpperCase();
        String lower = name.toLowerCase();

        boolean up = true;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') {
                result += " ";
                up = true;
            } else if (name.charAt(i) == '-') {
                result += " ";
                up = true;
                continue;
            }

            if (up) {
                result += upper.charAt(i);
                up = false;
            } else {
                result += lower.charAt(i);
            }
        }

        return result;
    }

}
