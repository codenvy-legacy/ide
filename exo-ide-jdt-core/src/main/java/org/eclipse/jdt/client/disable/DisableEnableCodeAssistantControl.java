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
package org.eclipse.jdt.client.disable;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.JdtExtension;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 *  Control to get code assistant information.
 *
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 */
@RolesAllowed({"developer"})
public class DisableEnableCodeAssistantControl extends SimpleControl implements IDEControl, ProjectOpenedHandler, ProjectClosedHandler {

    private boolean isJavaProject        = false;

    private DisableEnableCodeAssistantEvent enableCodeAssistant  = new DisableEnableCodeAssistantEvent(true);
    private DisableEnableCodeAssistantEvent disableCodeAssistant = new DisableEnableCodeAssistantEvent(false);

    public DisableEnableCodeAssistantControl() {
        super(JdtExtension.LOCALIZATION_CONSTANT.disableEnableCodeAssistantId());
        setTitle(JdtExtension.LOCALIZATION_CONSTANT.disableCodeAssistantControlTitle());
        setPrompt(JdtExtension.LOCALIZATION_CONSTANT.disableEnableCodeAssistantControlPrompt());
        setEvent(disableCodeAssistant);
        //TODO need sam image
        setImages(JdtClientBundle.INSTANCE.quickFix(), JdtClientBundle.INSTANCE.quickFixDisabled());
    }

    @Override
    public void initialize() {
        setEnabled(true);
        setVisible(false);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        isJavaProject = JdtExtension.get().isProjectSupported(event.getProject().getProjectType());
        setVisible(isJavaProject);
        setState(isJavaProject);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        isJavaProject = false;
        setVisible(isJavaProject);
    }

    public void setState(boolean codeAssistantEnabled){
        if(codeAssistantEnabled){
            setEvent(disableCodeAssistant);
            setTitle(JdtExtension.LOCALIZATION_CONSTANT.enableCodeAssistantControlTitle());
        }else{
            setEvent(enableCodeAssistant);
            setTitle(JdtExtension.LOCALIZATION_CONSTANT.disableCodeAssistantControlTitle());
        }
    }

}
