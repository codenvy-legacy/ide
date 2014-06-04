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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.upload.UploadFilePresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Upload file Action
 *
 * @author Roman Nikitenko
 */
@Singleton
public class UploadFileAction extends Action {

    private final UploadFilePresenter  presenter;
    private final SelectionAgent       selectionAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public UploadFileAction(UploadFilePresenter presenter,
                            CoreLocalizationConstant locale,
                            SelectionAgent selectionAgent, AnalyticsEventLogger eventLogger, Resources resources) {
        super(locale.uploadFileName(), locale.uploadFileDescription(), null, resources.uploadFile());
        this.presenter = presenter;
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Upload file");
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent event) {
        Selection<?> select = selectionAgent.getSelection();
        if (select != null && select.getFirstElement() != null && select.getFirstElement() instanceof Resource) {
            Selection<Resource> selection = (Selection<Resource>)select;
            event.getPresentation().setEnabled(selection.getFirstElement().getId() != null);
        } else event.getPresentation().setEnabled(false);
    }
}
