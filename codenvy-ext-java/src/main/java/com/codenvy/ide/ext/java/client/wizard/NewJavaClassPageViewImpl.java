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
 * Default implementation for {@link NewJavaClassPageView}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NewJavaClassPageViewImpl extends Composite implements NewJavaClassPageView {
    interface NewJavaClassPageViewImplUiBinder extends UiBinder<DockLayoutPanel, NewJavaClassPageViewImpl> {
    }

    private static NewJavaClassPageViewImplUiBinder ourUiBinder = GWT.create(NewJavaClassPageViewImplUiBinder.class);

    private ActionDelegate delegate;

    @UiField
    ListBox parents;

    @UiField
    TextBox typeName;

    @UiField
    ListBox types;

    @Inject
    public NewJavaClassPageViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Handler for ChangeEvent
     *
     * @param event
     *         the ChangeEvent
     */
    @UiHandler("parents")
    void handleParentChanged(ChangeEvent event) {
        delegate.parentChanged(parents.getSelectedIndex());
    }

    /**
     * Handler for KeyUpEvent
     *
     * @param event
     *         the KeyUpEvent
     */
    @UiHandler(value = {"typeName"})
    void handleKeyUpEvent(KeyUpEvent event) {
        delegate.checkTypeName();
    }

    /** {@inheritDoc} */
    @Override
    public String getClassName() {
        return typeName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getClassType() {
        return types.getItemText(types.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setClassTypes(JsonArray<String> classTypes) {
        for (String s : classTypes.asIterable()) {
            types.addItem(s);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setParents(JsonArray<String> parentNames) {
        for (String s : parentNames.asIterable()) {
            parents.addItem(s);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void disableAllUi() {
        parents.setEnabled(false);
        types.setEnabled(false);
        typeName.setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void selectParent(int index) {
        if (parents.getItemCount() > index) {
            parents.setItemSelected(index, true);
        }
    }
}