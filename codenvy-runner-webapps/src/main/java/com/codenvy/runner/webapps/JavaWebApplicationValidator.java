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
package com.codenvy.runner.webapps;

import com.codenvy.api.runner.internal.DeploymentSources;
import com.codenvy.api.runner.internal.DeploymentSourcesValidator;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Validator checks that {@link DeploymentSources} is a valid Java web application.
 *
 * @author Artem Zatsarynnyy
 */
public class JavaWebApplicationValidator implements DeploymentSourcesValidator {
    public static final String WEB_XML = "WEB-INF" + java.io.File.separatorChar + "web.xml";

    @Override
    public boolean isValid(DeploymentSources deployment) {
        if (deployment.isArchive()) {
            try (ZipFile zip = new ZipFile(deployment.getFile())) {
                return zip.getEntry(WEB_XML) != null;
            } catch (IOException e) {
                return false;
            }
        }
        return new java.io.File(deployment.getFile(), WEB_XML).exists();
    }
}
