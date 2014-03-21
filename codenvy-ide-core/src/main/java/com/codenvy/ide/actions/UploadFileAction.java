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
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Roman Nikitenko
 */
@Singleton
public class UploadFileAction extends Action {

    @Inject
    public UploadFileAction(CoreLocalizationConstant localization){
        super(localization.uploadFileName(), localization.uploadFileDescription(), null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
