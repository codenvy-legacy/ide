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

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.upload.UploadFilePresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Upload file Action
 *
 * @author Roman Nikitenko
 */
@Singleton
public class UploadFileAction extends Action {

    private UploadFilePresenter presenter;
    private boolean enabled = false;

    @Inject
    public UploadFileAction(UploadFilePresenter presenter, CoreLocalizationConstant locale, EventBus eventBus) {
        super(locale.uploadFileName(), locale.uploadFileDescription(), null);
        this.presenter = presenter;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                enabled = true;
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                enabled = false;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {

            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent event) {
        event.getPresentation().setEnabled(enabled);
    }
}
