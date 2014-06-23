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
package com.codenvy.ide.extension.runner.client.shell;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implements {@link ShellConsoleView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ShellConsoleViewImpl extends BaseView<ShellConsoleView.ActionDelegate> implements ShellConsoleView {

    interface RunnerConsoleViewImplUiBinder extends UiBinder<Widget, ShellConsoleViewImpl> {
    }

    @UiField
    Frame webShellFrame;

    @Inject
    public ShellConsoleViewImpl(PartStackUIResources resources, RunnerConsoleViewImplUiBinder uiBinder) {
        super(resources);
        container.add(uiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("runner-shell-console-minimizeButton");
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setUrl(String url) {
        webShellFrame.setUrl(url);
    }

}
