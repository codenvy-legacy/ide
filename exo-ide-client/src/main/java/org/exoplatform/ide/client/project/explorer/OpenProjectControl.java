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
package org.exoplatform.ide.client.project.explorer;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 20, 2012 3:46:43 PM anya $
 */
public class OpenProjectControl extends SimpleControl implements IDEControl, ProjectSelectedHandler {
    public static final String ID = "Project/Open.";

    private static final String TITLE = "Open";

    private static final String PROMPT = "Open Project";

    private ProjectModel selectedProject;

    public OpenProjectControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.projectOpened(), IDEImageBundle.INSTANCE.projectOpenedDisabled());
        setShowInMenu(false);
    }

    /** @see org.exoplatform.ide.client.project.explorer.ProjectSelectedHandler#onProjectSelected(org.exoplatform.ide.client.project
     * .explorer.ProjectSelectedEvent) */
    @Override
    public void onProjectSelected(ProjectSelectedEvent event) {
        this.selectedProject = event.getProject();
        boolean selected = (selectedProject != null);
        setShowInContextMenu(selected);
        setEnabled(selected);
        setVisible(selected);
        setEvent(new OpenProjectEvent(selectedProject));
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectSelectedEvent.TYPE, this);
    }
}
