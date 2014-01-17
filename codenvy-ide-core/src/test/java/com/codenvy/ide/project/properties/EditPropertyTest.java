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
package com.codenvy.ide.project.properties;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.project.properties.edit.EditPropertyPresenter;
import com.codenvy.ide.project.properties.edit.EditPropertyView;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test {@link EditPropertyPresenter}.
 * 
 * @author Ann Shumilova
 */
public class EditPropertyTest extends PropertiesBaseTest {
    @Mock
    private EditPropertyView        view;
    @Mock
    private AsyncCallback<Property> callback;
    @Mock
    private Property                property;

    private EditPropertyPresenter   presenter;

    /** {@inheritDoc} */
    @Override
    public void disarm() {
        super.disarm();
        presenter = new EditPropertyPresenter(view);
        when(property.getName()).thenReturn(PROPERTY_NATURE_MIXIN);
        when(property.getValue()).thenReturn(Collections.createArray(VALUE_NATURE_MIXIN));
    }

    @Test
    public void testShowDialog() {
        presenter.editProperty(property, callback);

        verify(view).setOkButtonEnabled(DISABLE_BUTTON);
        verify(view).setPropertyName(anyString());
        verify(view).setPropertyValue(anyString());
        verify(view).showDialog();
    }

    @Test
    public void testOnCancelClicked() {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnOkClicked() {
        when(view.getPropertyValue()).thenReturn(VALUE_RUNNER_NAME);

        presenter.editProperty(property, callback);
        presenter.onOkClicked();

        verify(view).getPropertyValue();
        verify(callback).onSuccess((Property)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnValueChanged() {
        when(view.getPropertyValue()).thenReturn(VALUE_MIMETYPE);
        presenter.onValueChanged();
        verify(view).setOkButtonEnabled(ENABLE_BUTTON);

        when(view.getPropertyValue()).thenReturn("");
        presenter.onValueChanged();
        verify(view).setOkButtonEnabled(DISABLE_BUTTON);
    }
}
