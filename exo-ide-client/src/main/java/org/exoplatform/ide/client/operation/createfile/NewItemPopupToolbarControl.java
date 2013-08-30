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
package org.exoplatform.ide.client.operation.createfile;

import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
@RolesAllowed({"developer"})
public class NewItemPopupToolbarControl extends PopupMenuControl implements IDEControl, VfsChangedHandler,
                                                                            ProjectOpenedHandler, ProjectClosedHandler,
                                                                            ViewVisibilityChangedHandler {

    public static final String ID = "File/New *";

    /**
     *
     */
    public NewItemPopupToolbarControl() {
        super(ID);
        setPrompt(IDE.IDE_LOCALIZATION_CONSTANT.newMenu());
        setImages(IDEImageBundle.INSTANCE.newFile(), IDEImageBundle.INSTANCE.newFileDisabled());
        setDelimiterBefore(true);
        setEnabled(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        if (event.getVfsInfo() != null) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if (event.getProject() != null)
            setEnabled(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     *      .client.framework.ui.api.event.ViewVisibilityChangedEvent)
     */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay ||
            event.getView() instanceof ProjectExplorerDisplay ||
            event.getView() instanceof PackageExplorerDisplay) {
            setEnabled(event.getView().isViewVisible());
        }
    }

}
