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
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.ui.dialogs.info.InfoHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link SshKeyView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 * @author Roman Nikitenko
 */
@Singleton
public class SshKeyViewImpl implements SshKeyView {

    final   SshLocalizationConstant locale;
    private ActionDelegate          delegate;
    private String                  title;
    private Info                    keyWindow;

    /**
     * Create view.
     *
     * @param locale
     */
    @Inject
    protected SshKeyViewImpl(SshLocalizationConstant locale) {
        this.locale = locale;
        this.title = locale.publicSshKeyField();

        keyWindow = new Info(new InfoHandler() {
            @Override
            public void onOk() {
                delegate.onCloseClicked();
            }
        });
        keyWindow.setTitle(title);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setKey(@NotNull String value) {
        keyWindow.setMessage(value);
    }

    /** {@inheritDoc} */
    @Override
    public void addHostToTitle(@NotNull String host) {
        keyWindow.setTitle(title + host);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        keyWindow.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        keyWindow.show();
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return keyWindow.asWidget();
    }
}