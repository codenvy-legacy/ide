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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;


/**
 * Default implementation for new Java package View.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NewPackagePageViewImpl extends Composite implements NewPackagePageView {
    interface NewPackagePageViewImplUiBinder extends UiBinder<DockLayoutPanel, NewPackagePageViewImpl> {
    }

    private static NewPackagePageViewImplUiBinder ourUiBinder = GWT.create(NewPackagePageViewImplUiBinder.class);

    @UiField
    TextBox packageName;

    @UiField
    ListBox parents;

    private ActionDelegate delegate;

    @Inject
    public NewPackagePageViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setParents(JsonArray<String> parents) {
        for (String s : parents.asIterable()) {
            this.parents.addItem(s);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getPackageName() {
        return packageName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void disableAllUi() {
        packageName.setEnabled(false);
        parents.setEnabled(false);
    }

    @UiHandler("parents")
    void handleParentChanged(ChangeEvent event) {
        delegate.parentChanged(parents.getSelectedIndex());
    }

    @UiHandler(value = {"packageName"})
    void handleKeyUpEvent(KeyUpEvent event) {
        delegate.checkPackageName();
    }

    /** {@inheritDoc} */
    @Override
    public void selectParent(int index) {
        if (parents.getItemCount() > index) {
            parents.setItemSelected(index, true);
        }
    }

}