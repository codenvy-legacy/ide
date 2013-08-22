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
package com.codenvy.ide.factory.client.generate;

import com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import static com.codenvy.ide.factory.client.FactoryExtension.LOCALIZATION_CONSTANTS;

/**
 * View for {@link CommitChangesPresenter}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CommitChangesView.java Jun 11, 2013 12:19:01 PM azatsarynnyy $
 */
public class CommitChangesView extends ViewImpl implements Display {

    private static final String              ID                   = LOCALIZATION_CONSTANTS.commitChangesViewId();

    private static final String              TITLE                = LOCALIZATION_CONSTANTS.commitChangesViewTitle();

    private static final int                 HEIGHT               = 220;

    private static final int                 WIDTH                = 450;

    private static final String              DESCRIPTION_FIELD_ID = LOCALIZATION_CONSTANTS.commitChangesFieldDescriptionId();

    private static final String              OK_BUTTON_ID         = LOCALIZATION_CONSTANTS.commitChangesButtonContinue();

    private static final String              CONTINUE_BUTTON_ID   = LOCALIZATION_CONSTANTS.commitChangesButtonContinue();

    private static CommitChangesViewUiBinder uiBinder             = GWT.create(CommitChangesViewUiBinder.class);

    interface CommitChangesViewUiBinder extends UiBinder<Widget, CommitChangesView> {
    }

    @UiField
    TextAreaInput descriptionField;

    @UiField
    ImageButton   okButton;

    @UiField
    Anchor        continueButton;

    public CommitChangesView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        descriptionField.getElement().setId(DESCRIPTION_FIELD_ID);
        okButton.setId(OK_BUTTON_ID);
        continueButton.getElement().setId(CONTINUE_BUTTON_ID);
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display#getDescriptionField()
     */
    @Override
    public HasValue<String> getDescriptionField() {
        return descriptionField;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display#setPlaceholderText(java.lang.String)
     */
    @Override
    public void setPlaceholderText(String text) {
        descriptionField.getElement().setAttribute("placeholder", text);
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display#getOkButton()
     */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display#getContinueButton()
     */
    @Override
    public HasClickHandlers getContinueButton() {
        return continueButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display#focusDescriptionField()
     */
    @Override
    public void focusDescriptionField() {
        descriptionField.focus();
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.CommitChangesPresenter.Display#selectDescriptionField()
     */
    @Override
    public void selectDescriptionField() {
        descriptionField.selectAll();
    }

}
