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

package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
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
                            SelectionAgent selectionAgent, AnalyticsEventLogger eventLogger) {
        super(locale.uploadFileName(), locale.uploadFileDescription(), null);
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
