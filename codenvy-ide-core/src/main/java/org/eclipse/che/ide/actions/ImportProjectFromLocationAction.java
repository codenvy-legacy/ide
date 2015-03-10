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

import org.eclipse.che.ide.projectimport.wizard.presenter.ImportProjectWizardPresenter;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.projectimport.wizard.presenter.ImportProjectWizardPresenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Import project Action
 *
 * @author Roman Nikitenko
 */
@Singleton
public class ImportProjectFromLocationAction extends Action {

    private final ImportProjectWizardPresenter presenter;
    private final AnalyticsEventLogger         eventLogger;
    private final AppContext                   appContext;

    @Inject
    public ImportProjectFromLocationAction(ImportProjectWizardPresenter presenter,
                                           CoreLocalizationConstant locale,
                                           AnalyticsEventLogger eventLogger,
                                           Resources resources,
                                           AppContext appContext) {
        super(locale.importProjectName(), locale.importProjectDescription(), null, resources.importProject());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent event) {
        eventLogger.log(this);
        presenter.show();
    }

    @Override
    public void update(ActionEvent e) {
        if (appContext.getCurrentProject() == null) {
            e.getPresentation().setEnabled(appContext.getCurrentUser().isUserPermanent());
        } else {
            e.getPresentation().setEnabled(!appContext.getCurrentProject().isReadOnly());
        }
    }
}
