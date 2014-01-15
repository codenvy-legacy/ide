/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.project.properties.add;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * Presenter for adding new project's property. 
 * Call {@link AddNewPropertyPresenter#addNewProperty(AsyncCallback)} and set the callback to
 * return new property's name and value.
 * 
 * @author Ann Shumilova
 */
@Singleton
public class AddNewPropertyPresenter implements AddNewPropertyView.ActionDelegate {

    private AddNewPropertyView      view;
    private AsyncCallback<Property> callback;

    @Inject
    public AddNewPropertyPresenter(AddNewPropertyView view) {
        this.view = view;
        view.setDelegate(this);
    }

    /**
     * Add new property to project.
     * 
     * @param callback
     */
    public void addNewProperty(@NotNull AsyncCallback<Property> callback) {
        this.callback = callback;

        view.clearNameField();
        view.clearValueField();
        view.showDialog();
        view.setOkButtonEnabled(false);
    }


    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        String name = view.getPropertyName();
        Array<String> values = Collections.createArray(view.getPropertyValue().split(","));
        Property property = new Property(name, values);
        callback.onSuccess(property);
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        updateButtonState();
    }

    /** {@inheritDoc} */
    @Override
    public void onNameChanged() {
        updateButtonState();
    }

    /**
     * Update the state of buttons(enabled/disabled).
     */
    private void updateButtonState() {
        boolean enabled =
                          view.getPropertyName() != null && !view.getPropertyName().isEmpty() && view.getPropertyValue() != null
                              && !view.getPropertyValue().isEmpty();
        view.setOkButtonEnabled(enabled);
        ;
    }
}
