/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client.welcome;

import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author Vitaliy Guliy
 */
@Singleton
public class GreetingPartPresenter extends BasePresenter implements GreetingPartView.ActionDelegate, GreetingPart {

    private static final String TITLE = "Greeting";

    private GreetingPartView view;

    @Inject
    public GreetingPartPresenter(GreetingPartView view) {
        this.view = view;
        this.view.setTitle(TITLE);
        this.view.setDelegate(this);
    }


    @Override
    public String getTitle() {
        return TITLE;
    }

    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

//    @Nullable
//    @Override
//    public IsWidget getTitleWidget() {
//        return null;
//    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return "Greeting the user";
    }

//    @Override
//    public int getSize() {
//        return 0;
//    }

//    @Override
//    public void onOpen() {
//
//    }

//    @Override
//    public boolean onClose() {
//        return false;
//    }

//    @Override
//    public void addPropertyListener(@NotNull PropertyListener listener) {
//    }

//    @Override
//    public Selection<?> getSelection() {
//        return null;
//    }

//    @Override
//    public void removePropertyListener(@NotNull PropertyListener listener) {
//
//    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void showGreeting() {
    }

//    @Override
//    public void onClearClicked() {
//    }

}
