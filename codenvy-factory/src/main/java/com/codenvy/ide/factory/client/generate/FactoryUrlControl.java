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
package com.codenvy.ide.factory.client.generate;

import com.codenvy.ide.factory.client.FactoryClientBundle;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.DisableInTempWorkspace;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

import static com.codenvy.ide.factory.client.FactoryExtension.LOCALIZATION_CONSTANTS;

/**
 * Control to share opened project by the Factory URL.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryUrlControl.java Jun 11, 2013 11:25:21 AM azatsarynnyy $
 */
@RolesAllowed({"workspace/developer"})
@DisableInTempWorkspace
public class FactoryUrlControl extends SimpleControl implements IDEControl, VfsChangedHandler, ProjectOpenedHandler,
                                                    ProjectClosedHandler {

    public static final String  ID     = LOCALIZATION_CONSTANTS.factoryURLControlId();

    private static final String TITLE  = LOCALIZATION_CONSTANTS.factoryURLControlTitle();

    private static final String PROMPT = LOCALIZATION_CONSTANTS.factoryURLControlPrompt();

    public FactoryUrlControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(FactoryClientBundle.INSTANCE.share(), FactoryClientBundle.INSTANCE.shareDisabled());
        setEvent(new ShareWithFactoryUrlEvent());
        setVisible(true);
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
     */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setEnabled(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        if (event.getVfsInfo() == null) {
            setEnabled(false);
        }
    }

}
