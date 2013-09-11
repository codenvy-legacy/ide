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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.ssh.client.SshClientBundle;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

public class SshKeyManagerView extends ViewImpl implements Display {

    private static SshKeyManagerViewUiBinder uiBinder = GWT.create(SshKeyManagerViewUiBinder.class);

    interface SshKeyManagerViewUiBinder extends UiBinder<Widget, SshKeyManagerView> {
    }

    @UiField
    SshKeysGrid keysGrid;

    @UiField
    ImageButton generateButton;

    @UiField
    ImageButton uploadButton;

    @UiField
    ImageButton generateGithubKeyButton;

    public SshKeyManagerView() {
        super(ID, ViewType.MODAL, "Ssh Keys", null, 725, 390, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        generateGithubKeyButton.setImage(new Image(SshClientBundle.INSTANCE.sshKeyGithubGenerate()));
    }

    /** @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getKeyItemGrid() */
    @Override
    public HasSshGrid<KeyItem> getKeyItemGrid() {
        return keysGrid;
    }

    /** @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getGenerateButton() */
    @Override
    public HasClickHandlers getGenerateButton() {
        return generateButton;
    }

    /** @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getUploadButton() */
    @Override
    public HasClickHandlers getUploadButton() {
        return uploadButton;
    }

    @Override
    public HasClickHandlers getGenerateGithubKeyButton() {
        return generateGithubKeyButton;
    }
}
