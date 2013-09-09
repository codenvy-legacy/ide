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
package org.exoplatform.ide.extension.cloudfoundry.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class LoginInfo {
    static String email;
    static String password;
    static String target = "http://api.cloudfoundry.com";

    static {
        Properties properties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("security.properties");
        if (in != null) {
            try {
                properties.load(in);
                email = (String)properties.get("email");
                password = (String)properties.get("password");
                if (properties.containsKey("target")) {
                    target = (String)properties.get("target");
                }
            } catch (IOException ignored) {
                // Lets resolve this in tests.
            } finally {
                try {
                    in.close();
                } catch (IOException ignored2) {
                }
            }
        }
    }
}
