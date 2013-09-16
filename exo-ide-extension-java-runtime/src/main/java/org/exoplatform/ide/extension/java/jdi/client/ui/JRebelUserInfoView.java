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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.JRebelUserInfoPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: May 14, 2013 2:38:19 PM valeriy $
 */
public class JRebelUserInfoView extends ViewImpl implements JRebelUserInfoPresenter.Display {
    
    public static final int HEIGHT = 294;

    public static final int WIDTH = 380;

    public static final String ID = "ideJRebelUserInfoView";
    
    private final String OK_BUTTON_ID = "eXoJRebelUserInfoViewOkButton";

    private final String CANCEL_BUTTON_ID = "eXoJRebelUserInfoViewCancelButton";
    
    private final String JREBEL_PROFILE_FIRSTNAME_ID = "jrebelprofilefirstname";

    private final String JREBEL_PROFILE_LASTNAME_ID = "jrebelprofilelastname";

    private final String JREBEL_PROFILE_PHONE_ID = "jrebelprofilephone";

    private final String JREBEL_ERROR_MESSAGE_LABEL_ID = "jrebelerrormessagelabel";

    private static JRebelUserInfoViewUiBinder uiBinder = GWT.create(JRebelUserInfoViewUiBinder.class);

    interface JRebelUserInfoViewUiBinder extends UiBinder<Widget, JRebelUserInfoView> {
    }
    
    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;
    
    @UiField
    TextInput jRebelProfileFirstName;

    @UiField
    TextInput jRebelProfileLastName;

    @UiField
    TextInput jRebelProfilePhone;

    @UiField
    Label jRebelErrorMessageLabel;
    
    public JRebelUserInfoView() {
        super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.jRebelUserInfoViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        
        okButton.setButtonId(OK_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        jRebelErrorMessageLabel.setID(JREBEL_ERROR_MESSAGE_LABEL_ID);
        jRebelProfileFirstName.getElement().setId(JREBEL_PROFILE_FIRSTNAME_ID);
        jRebelProfileLastName.getElement().setId(JREBEL_PROFILE_LASTNAME_ID);
        jRebelProfilePhone.getElement().setId(JREBEL_PROFILE_PHONE_ID);

    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public HasValue<String> getJRebelFirstNameField() {
        return jRebelProfileFirstName;
    }

    @Override
    public HasValue<String> getJRebelLastNameField() {
        return jRebelProfileLastName;
    }

    @Override
    public HasValue<String> getJRebelPhoneNumberField() {
        return jRebelProfilePhone;
    }

    @Override
    public void setJRebelErrorMessageLabel(String message) {
        jRebelErrorMessageLabel.setValue(message);
    }
}
