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
package com.codenvy.ide.part.console;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implements {@link ConsolePartView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ConsolePartViewImpl extends BaseView<ConsolePartView.ActionDelegate> implements ConsolePartView {
    interface ConsolePartViewImplUiBinder extends UiBinder<Widget, ConsolePartViewImpl> {
    }

    private static ConsolePartViewImplUiBinder uiBinder = GWT.create(ConsolePartViewImplUiBinder.class);

    @UiField
    FlowPanel consoleArea;

    @Inject
    public ConsolePartViewImpl(PartStackUIResources resources) {
        super(resources);
        container.add(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void print(String message) {
        consoleArea.add(new HTML(message));
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        consoleArea.clear();
    }
}