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
package com.codenvy.ide.wizard.newfolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * NewFolderPageViewImpl is the view of NewFolder wizard.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewFolderPageViewImpl extends Composite implements NewFolderPageView {
    private static NewFolderViewUiBinder uiBinder = GWT.create(NewFolderViewUiBinder.class);

    @UiField
    TextBox folderName;

    interface NewFolderViewUiBinder extends UiBinder<Widget, NewFolderPageViewImpl> {
    }

    private ActionDelegate delegate;

    /** Create view. */
    public NewFolderPageViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public String getFolderName() {
        return folderName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("folderName")
    void onFolderNameKeyUp(KeyUpEvent event) {
        delegate.checkEnteredInformation();
    }

    /** {@inheritDoc} */
    @Override
    public void setFolderName(String name) {
        folderName.setText(name);
    }
}