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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EditPropertyView extends ViewImpl implements
                                               org.exoplatform.ide.client.project.properties.EditPropertyPresenter.Display {

    public static final String ID = "ideEditProjectPropertyView";

    public static final String TITLE = "Edit Property";

    /** Initial width of this view */
    private static final int WIDTH = 420;

    /** Initial height of this view */
    private static final int HEIGHT = 200;

    private static EditPropertyViewUiBinder uiBinder = GWT.create(EditPropertyViewUiBinder.class);

    interface EditPropertyViewUiBinder extends UiBinder<Widget, EditPropertyView> {
    }

    @UiField
    TextBox nameField;

    @UiField
    TextBox valueField;

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;

    public EditPropertyView() {
        super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectProperties()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        nameField.setReadOnly(true);
    }

    @Override
    public HasValue<String> getNameField() {
        return nameField;
    }

    @Override
    public HasValue<String> getValueField() {
        return valueField;
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public void setOkButtonText(String text) {
        okButton.setText(text);
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

}
