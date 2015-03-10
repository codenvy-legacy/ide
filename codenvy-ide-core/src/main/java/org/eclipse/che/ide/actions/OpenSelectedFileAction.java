/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Vitaliy Guliy
 */
@Singleton
public class OpenSelectedFileAction extends Action {

    private final AppContext           appContext;
    private final SelectionAgent       selectionAgent;
    private final EventBus             eventBus;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public OpenSelectedFileAction(AppContext appContext,
                                  SelectionAgent selectionAgent,
                                  EventBus eventBus,
                                  AnalyticsEventLogger eventLogger,
                                  Resources resources) {
        super("Open", null, null, resources.defaultFile());
        this.appContext = appContext;
        this.selectionAgent = selectionAgent;
        this.eventBus = eventBus;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() instanceof FileNode) {
            FileNode fileNode = (FileNode)selection.getFirstElement();
            eventBus.fireEvent(new FileEvent(fileNode, FileEvent.FileOperation.OPEN));
        }
    }

    @Override
    public void update(ActionEvent e) {
        if (appContext.getCurrentProject() == null) {
            e.getPresentation().setVisible(false);
            return;
        }

        Selection<?> selection = selectionAgent.getSelection();
        final boolean isFileSelected = selection != null && selection.getFirstElement() instanceof FileNode;
        e.getPresentation().setVisible(isFileSelected);
    }
}
