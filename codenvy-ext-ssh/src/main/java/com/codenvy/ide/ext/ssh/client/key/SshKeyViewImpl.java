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