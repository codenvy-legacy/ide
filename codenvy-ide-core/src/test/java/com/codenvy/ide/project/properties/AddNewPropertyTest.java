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

import com.codenvy.ide.project.properties.add.AddNewPropertyPresenter;
import com.codenvy.ide.project.properties.add.AddNewPropertyView;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AddNewPropertyPresenter}.
 * 
 * @author Ann Shumilova
 */
public class AddNewPropertyTest extends PropertiesBaseTest {
    @Mock
    private AddNewPropertyView      view;
    @Mock
    private AsyncCallback<Property> callback;

    private AddNewPropertyPresenter presenter;

    /** {@inheritDoc} */
    @Override
    public void disarm() {
        super.disarm();
        presenter = new AddNewPropertyPresenter(view);
    }

    @Test
    public void testShowDialog() {
        presenter.addNewProperty(callback);

        verify(view).clearNameField();
        verify(view).clearValueField();
        verify(view).showDialog();
        verify(view).setOkButtonEnabled(DISABLE_BUTTON);
    }

    @Test
    public void testOnCancelClicked() {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnOkClicked() {
        when(view.getPropertyName()).thenReturn(PROPERTY_PROJECT_TYPE);
        when(view.getPropertyValue()).thenReturn(VALUE_PROJECT_TYPE);
        
        presenter.addNewProperty(callback);
        presenter.onOkClicked();

        verify(view).getPropertyName();
        verify(view).getPropertyValue();
        verify(callback).onSuccess((Property)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnValueChanged() {
        when(view.getPropertyName()).thenReturn(PROPERTY_MIMETYPE);
        when(view.getPropertyValue()).thenReturn(VALUE_MIMETYPE);
        presenter.onValueChanged();
        verify(view).setOkButtonEnabled(ENABLE_BUTTON);

        when(view.getPropertyName()).thenReturn("");
        when(view.getPropertyValue()).thenReturn(VALUE_MIMETYPE);
        presenter.onNameChanged();
        verify(view).setOkButtonEnabled(DISABLE_BUTTON);

        when(view.getPropertyName()).thenReturn(PROPERTY_NATURE_MIXIN);
        when(view.getPropertyValue()).thenReturn("");
        presenter.onValueChanged();
        verify(view, times(2)).setOkButtonEnabled(DISABLE_BUTTON);
    }
}
