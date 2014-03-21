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
package com.codenvy.ide.upload;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author Roman Nikitenko.
 */
public class UploadFileViewImpl extends DialogBox implements UploadFileView{

    public interface UploadFileViewBinder extends UiBinder<Widget, UploadFileViewImpl> {
    }

    @Inject
    public UploadFileViewImpl(UploadFileViewBinder uploadFileViewBinder){
        setWidget(uploadFileViewBinder.createAndBindUi(this));
    }
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

}
