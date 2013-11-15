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
package com.codenvy.ide.tutorial.parts.part;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The implementation of {@link MyPartView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyPartViewImpl extends BaseView<MyPartView.ActionDelegate> implements MyPartView {
    interface MyPartViewImplUiBinder extends UiBinder<Widget, MyPartViewImpl> {
    }

    private static MyPartViewImplUiBinder ourUiBinder = GWT.create(MyPartViewImplUiBinder.class);

    @UiField
    Button button;

    @Inject
    public MyPartViewImpl(PartStackUIResources resources) {
        super(resources);
        container.add(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("button")
    public void onButtonClicked(ClickEvent event) {
        delegate.onButtonClicked();
    }
}