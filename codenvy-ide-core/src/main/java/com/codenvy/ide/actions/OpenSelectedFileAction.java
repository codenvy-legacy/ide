/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
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
