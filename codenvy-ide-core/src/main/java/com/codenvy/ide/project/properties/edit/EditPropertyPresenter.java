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
package com.codenvy.ide.project.properties.edit;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.project.properties.PropertyUtil;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * Presenter for changing the properties value.
 * The values are separated by ",".
 * 
 * @author Ann Shumilova
 */
@Singleton
public class EditPropertyPresenter implements EditPropertyView.ActionDelegate {

    private EditPropertyView        view;

    private Property                property;

    private AsyncCallback<Property> callback;

    @Inject
    public EditPropertyPresenter(EditPropertyView view) {
        this.view = view;
        view.setDelegate(this);


    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        view.close();
        String value = view.getPropertyValue();
        property.setValue(Collections.createArray(value.split(",")));
        callback.onSuccess(property);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /**
     * Edit the given property's value.
     * 
     * @param property property to edit
     * @param callback callback,when editing is finished
     */
    public void editProperty(@NotNull Property property, @NotNull AsyncCallback<Property> callback) {
        this.callback = callback;
        this.property = property;
        view.setOkButtonEnabled(false);
        view.setPropertyName(PropertyUtil.getHumanReadableName(property.getName()));
        String value = ("" + Arrays.asList(property.getValue())).replaceAll("(^.|.$)", "").replace(", ", ",");
        value = value.replace("[", "");
        value = value.replace("]", "");
        view.setPropertyValue(value);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        boolean enabled = (view.getPropertyValue() != null && view.getPropertyValue().length() > 0);
        view.setOkButtonEnabled(enabled);
    }
}
