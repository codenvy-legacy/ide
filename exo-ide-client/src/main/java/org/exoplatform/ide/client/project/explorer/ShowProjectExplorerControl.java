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
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectExplorerControl extends SimpleControl implements IDEControl, ViewOpenedHandler,
                                                                         ViewClosedHandler {

    public static final String ID = "Window/Show View/Project Explorer";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.projectExplorerControlTitle();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.projectExplorerControlPrompt();

    /**
     *
     */
    public ShowProjectExplorerControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.projectExplorer(), IDEImageBundle.INSTANCE.projectExplorerDisabled());
        setEvent(new ShowProjectExplorerEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);

        setEnabled(true);
        setVisible(true);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay) {
            setSelected(false);
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewOpenedEvent) */
    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay) {
            setSelected(true);
        }
    }

}
