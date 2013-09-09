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
package org.exoplatform.ide.client.framework.paas;

import com.google.gwt.user.client.ui.Composite;

import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 12:44:17 PM anya $
 */
public interface HasPaaSActions {
    void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler);

    void deploy(ProjectModel project, DeployResultHandler deployResultHandler);

    Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler);

    boolean validate();

    void deployFirstTime(String projectName, ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler);
}
