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
package org.exoplatform.ide.client.navigation.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 * Control for show or hide hidden files.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHiddenFilesControl.java Mar 30, 2012 11:50:01 AM azatsarynnyy $
 */
@RolesAllowed({"developer"})
public class ShowHideHiddenFilesControl extends SimpleControl implements IDEControl, ShowHideHiddenFilesHandler,
                                                                         ProjectOpenedHandler, ProjectClosedHandler {

    /** ID of this control. */
    public static final String ID = "View/Show \\ Hide Hidden Files";

    /** Title of this control. */
    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.showHiddenFilesControlTitle();

    /** State of hidden files visibility. */
    private boolean filesAreShown = false;

    private boolean isProjectOpened = false;

    /** Default constructor. */
    public ShowHideHiddenFilesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.showHiddenFiles(), IDEImageBundle.INSTANCE.showHiddenFiles());
        setEvent(new ShowHideHiddenFilesEvent(true));
        setDelimiterBefore(true);
        setCanBeSelected(true);
        setVisible(true);
        setEnabled(true);
    }

    /**
     * Initializes control.
     *
     * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
     */
    @Override
    public void initialize() {
        IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);

        updateState();
    }

    /** Update title, prompt and event. */
    private void updateState() {
        if (!isProjectOpened) {
            setEnabled(false);
            return;
        } else {
            setEnabled(true);
        }

        if (filesAreShown) {
            setEvent(new ShowHideHiddenFilesEvent(false));
        } else {
            setEvent(new ShowHideHiddenFilesEvent(true));
        }

        setSelected(filesAreShown);
    }

    /** @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client
     * .navigation.event.ShowHideHiddenFilesEvent) */
    @Override
    public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event) {
        filesAreShown = event.isFilesShown();
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        isProjectOpened = false;
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        isProjectOpened = true;
        updateState();
    }

}
