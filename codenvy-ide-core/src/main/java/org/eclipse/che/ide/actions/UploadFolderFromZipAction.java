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
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.ide.upload.folder.UploadFolderFromZipPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Upload folder from zip Action
 *
 * @author Roman Nikitenko
 */
@Singleton
public class UploadFolderFromZipAction extends ProjectAction {

    private final UploadFolderFromZipPresenter presenter;
    private final SelectionAgent               selectionAgent;
    private final AnalyticsEventLogger         eventLogger;

    @Inject
    public UploadFolderFromZipAction(UploadFolderFromZipPresenter presenter,
                                     CoreLocalizationConstant locale,
                                     SelectionAgent selectionAgent,
                                     AnalyticsEventLogger eventLogger,
                                     Resources resources) {
        super(locale.uploadFolderFromZipName(), locale.uploadFolderFromZipDescription(), resources.uploadFile());
        this.presenter = presenter;
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void updateProjectAction(ActionEvent event) {
        event.getPresentation().setVisible(true);
        boolean enabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            enabled = selection.getFirstElement() != null;
        }
        event.getPresentation().setEnabled(enabled);
    }
}
