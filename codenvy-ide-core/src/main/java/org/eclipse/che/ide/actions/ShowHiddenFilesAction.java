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
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.RefreshProjectTreeEvent;
import org.eclipse.che.ide.api.project.tree.TreeSettings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/** @author Artem Zatsarynnyy */
@Singleton
public class ShowHiddenFilesAction extends Action {

    private final AppContext           appContext;
    private final AnalyticsEventLogger eventLogger;
    private final EventBus             eventBus;

    @Inject
    public ShowHiddenFilesAction(AppContext appContext, AnalyticsEventLogger eventLogger, EventBus eventBus,
                                 CoreLocalizationConstant localizationConstant) {
        super(localizationConstant.actionShowHiddenFilesTitle(), localizationConstant.actionShowHiddenFilesDescription(), null, null);
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            TreeSettings treeSettings = currentProject.getCurrentTree().getSettings();
            treeSettings.setShowHiddenItems(!treeSettings.isShowHiddenItems());
            eventBus.fireEvent(new RefreshProjectTreeEvent());
        }
    }
}
