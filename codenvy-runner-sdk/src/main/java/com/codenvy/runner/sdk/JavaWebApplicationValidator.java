/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.runner.sdk;

import java.util.zip.ZipFile;

/**
 * Validator checks that {@code DeploymentSources} is a valid Java web application.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class JavaWebApplicationValidator {
    public static final String WEB_XML = "WEB-INF" + java.io.File.separatorChar + "web.xml";

    public boolean isValid(ZipFile deployment) {
        return deployment.getEntry(WEB_XML) != null;
    }

}
