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
package org.eclipse.jdt.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.create.CreatePackagePresenter.Display;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CreatePackageView extends ViewImpl implements Display {

    private static final String ID = "ideCreatePackageView";

    private static CreatePackageViewUiBinder uiBinder = GWT.create(CreatePackageViewUiBinder.class);

    @UiField
    TextInput packageField;

    @UiField
    ImageButton createButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Label errorLabel;

    @UiField
    Label warningLabel;

    interface CreatePackageViewUiBinder extends UiBinder<Widget, CreatePackageView> {
    }

    public CreatePackageView() {
        super(ID, ViewType.MODAL, JavaEditorExtension.MESSAGES.createPackageTitle(), null, 400, 128, false);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getPackageNameField() */
    @Override
    public HasValue<String> getPackageNameField() {
        return packageField;
    }

    /** @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return createButton;
    }

    /** @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getErrorLabel() */
    @Override
    public HasText getErrorLabel() {
        return errorLabel;
    }

    /** @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#setOkButtonEnabled(boolean) */
    @Override
    public void setOkButtonEnabled(boolean enabled) {
        createButton.setEnabled(enabled);
    }

    /** @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getWarningLabel() */
    @Override
    public HasText getWarningLabel() {
        return warningLabel;
    }

    @Override
    public void focusInPackageNameField() {
        packageField.selectAll();
        packageField.focus();
    }

}
