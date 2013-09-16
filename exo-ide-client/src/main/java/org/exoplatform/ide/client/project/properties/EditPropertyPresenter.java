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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EditPropertyPresenter implements ViewClosedHandler {

    public interface Display extends IsView {

        HasValue<String> getNameField();

        HasValue<String> getValueField();

        HasClickHandlers getOkButton();

        void setOkButtonText(String text);

        HasClickHandlers getCancelButton();

    }

    private Display display;

    private Property property;

    private List<Property> propertyList;

    private EditCompleteHandler editCompleteHandler;

    public EditPropertyPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void createProperty(List<Property> propertyList, EditCompleteHandler editCompleteHandler) {
        property = null;
        this.propertyList = propertyList;
        this.editCompleteHandler = editCompleteHandler;

        ensureViewOpened();

        display.setOkButtonText("Create");
        display.getNameField().setValue("");
        display.getValueField().setValue("");
    }

    public void editProperty(Property property, List<Property> propertyList, EditCompleteHandler editCompleteHandler) {
        this.property = property;
        this.propertyList = propertyList;
        this.editCompleteHandler = editCompleteHandler;

        ensureViewOpened();

        display.setOkButtonText("Ok");
        display.getNameField().setValue(property.getName());
        display.getValueField().setValue(getPropertyValue(property.getValue()));
    }

    private String getPropertyValue(List valueList) {
        String value = "";
        for (Object v : valueList) {
            if (!value.isEmpty()) {
                value += "<br>";
            }

            value += v;
        }

        return value;
    }

    private void ensureViewOpened() {
        if (display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    private void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editComplete();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    private boolean isPropertyExistAndNotNull(String propertyName) {
        for (Property property : propertyList) {
            if (propertyName.equals(property.getName()) && property.getValue() != null) {
                return true;
            }
        }

        return false;
    }

    private void setProperty(String propertyName, String propertyValue) {
        for (Property property : propertyList) {
            if (propertyName.equals(property.getName())) {
                List value = new ArrayList();
                value.add(propertyValue);
                property.setValue(value);
                return;
            }
        }

        Property property = new PropertyImpl(propertyName, propertyValue);
        propertyList.add(property);
    }

    private void editComplete() {
        String name = display.getNameField().getValue().trim();
        if (name == null || name.isEmpty()) {
            Dialogs.getInstance().showError("Property name could not be empty.");
            return;
        }

        String value = display.getValueField().getValue().trim();
        if (value == null || value.isEmpty()) {
            Dialogs.getInstance().showError("Property value could not be empty.");
            return;
        }

        if (property == null) {
            // create new

            if (isPropertyExistAndNotNull(name)) {
                Dialogs.getInstance().showError("Property <b>" + name + "</b> already exist.");
                return;
            }

            property = new PropertyImpl(name, value);
            propertyList.add(property);
        } else {
            // edit

            if (!name.equals(property.getName())) {
                // property name changed!

                if (isPropertyExistAndNotNull(name)) {
                    Dialogs.getInstance().showError("Property <b>" + name + "</b> already exist.");
                    return;
                }

                property.setValue(null);
                Property newProperty = new PropertyImpl(name, value);
                propertyList.add(newProperty);
            } else {
                property.setName(name);
                List v = new ArrayList();
                v.add(value);
                property.setValue(v);
            }

        }

        IDE.getInstance().closeView(display.asView().getId());

        if (editCompleteHandler != null) {
            editCompleteHandler.onEditComplete();
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
