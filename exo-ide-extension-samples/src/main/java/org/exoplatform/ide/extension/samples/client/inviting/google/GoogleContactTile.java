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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class GoogleContactTile extends Composite {

    private static GoogleContactTileUiBinder uiBinder = GWT.create(GoogleContactTileUiBinder.class);

    interface GoogleContactTileUiBinder extends UiBinder<Widget, GoogleContactTile> {
    }

    interface Style extends CssResource {
        String userFieldBody();

        String userFieldBodySelected();
    }

    @UiField
    Style style;

    @UiField
    FlowPanel userFieldBody;

    @UiField
    CheckBox checkBox;

    @UiField
    Image avatarImage;

    @UiField
    Label name, company, email;

    private GoogleContact googleContact;

    private GoogleContactSelectionChangedHandler selectionChangedHandler;

    public GoogleContactTile(GoogleContact contact) {
        googleContact = contact;

        initWidget(uiBinder.createAndBindUi(this));

        //avatarImage.setUrl(user.getAvatarUrl());

        if (contact.getPhotoBase64() != null) {
            String url = "data:image/jpg;base64," + contact.getPhotoBase64();
            avatarImage.setUrl(url);
        } else {
            avatarImage.setUrl(SamplesClientBundle.INSTANCE.userDefaultPhoto().getSafeUri());
        }

        name.setText(contact.getName());
        name.setTitle(name.getText());
        //company.setText(user. getCompany());
        if (!contact.getEmailAddresses().isEmpty()) {
            email.setText(contact.getEmailAddresses().get(0));
            email.setTitle(email.getText());
        }

        checkBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setSelected(checkBox.getValue());
                if (selectionChangedHandler != null) {
                    selectionChangedHandler.onGoogleContactSelectionChanged(googleContact, checkBox.getValue().booleanValue());
                }
            }
        });

        avatarImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setSelected(!checkBox.getValue());
                if (selectionChangedHandler != null) {
                    selectionChangedHandler.onGoogleContactSelectionChanged(googleContact, checkBox.getValue().booleanValue());
                }
            }
        });
    }

    public void setSelectionChangedHandler(GoogleContactSelectionChangedHandler selectionChangedHandler) {
        this.selectionChangedHandler = selectionChangedHandler;
    }

    public boolean isSelected() {
        return checkBox.getValue();
    }

    public void setSelected(boolean selected) {
        checkBox.setValue(selected);
        if (selected) {
            userFieldBody.setStyleName(style.userFieldBodySelected());
        } else {
            userFieldBody.setStyleName(style.userFieldBody());
        }
    }
}
