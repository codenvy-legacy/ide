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
package org.exoplatform.ide.extension.java.client.datasource;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
@RolesAllowed({"developer"})
public class ConfigureDatasourceControl extends SimpleControl implements IDEControl,
    ProjectOpenedHandler, ProjectClosedHandler {

    public static final String ID = "Project/Datasource...";
    
    public static final String TITLE = "Datasource...";
    
    public static final String PROMPT = "Configure Datasource...";
    
    public ConfigureDatasourceControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(JavaClientBundle.INSTANCE.datasource(), JavaClientBundle.INSTANCE.datasourceDisabled());
        setEvent(new ConfigureDatasourceEvent());
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);        
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if (JavaProjects.contains(event.getProject())) {
            setVisible(true);
            setEnabled(true);
            return;
        }
        
        setEnabled(false);
        setVisible(false);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
        setVisible(false);
    }
    
}
