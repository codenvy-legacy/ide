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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter.Display;

/**
 * View for showing contents of public key.
 */
public class SshPublicKeyView extends ViewImpl implements Display {

    private static final String ID = "ideSshPublicKeyView";

    private static final String TITLE = "Public Ssh Key: ";

    private static SshPublicKeyViewUiBinder uiBinder = GWT.create(SshPublicKeyViewUiBinder.class);

    interface SshPublicKeyViewUiBinder extends UiBinder<Widget, SshPublicKeyView> {
    }

    @UiField
    TextBox publicSshKeyField;

    @UiField
    ImageButton closeButton;

    public SshPublicKeyView() {
        super(ID, ViewType.MODAL, TITLE, null, 380, 105, false);
        add(uiBinder.createAndBindUi(this));
        UIHelper.setAsReadOnly("exoSshPublicKeyField");
    }


    /** {@inheritDoc} */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** {@inheritDoc} */
    @Override
    public HasValue<String> getKeyField() {
        return publicSshKeyField;
    }

    /** {@inheritDoc} */
    @Override
    public void addHostToTitle(String host) {
        setTitle(TITLE + host);
    }

}
