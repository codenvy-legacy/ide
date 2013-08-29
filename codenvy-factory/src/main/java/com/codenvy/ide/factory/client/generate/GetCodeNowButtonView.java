/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.factory.client.generate;

import com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import static com.codenvy.ide.factory.client.FactoryExtension.LOCALIZATION_CONSTANTS;

/**
 * View for {@link GetCodeNowButtonPresenter}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GetCodeNowButtonView.java Jun 11, 2013 12:19:01 PM azatsarynnyy $
 */
public class GetCodeNowButtonView extends ViewImpl implements Display {

    private static final String           ID                          = LOCALIZATION_CONSTANTS.factoryURLViewId();

    private static final String           TITLE                       = LOCALIZATION_CONSTANTS.factoryURLViewTitle();

    private static final int              HEIGHT                      = 400;

    private static final int              WIDTH                       = 750;

    private static final String           WEBSITES_URL_FIELD_ID       = LOCALIZATION_CONSTANTS.factoryURLFieldWebsitesURLId();

    private static final String           GITHUB_URL_FIELD_ID         = LOCALIZATION_CONSTANTS.factoryURLFieldGitHubURLId();

    private static final String           DIRECT_SHARING_URL_FIELD_ID = LOCALIZATION_CONSTANTS.factoryURLFieldDirectSharingURLId();

    private static final String           OK_BUTTON_ID                = LOCALIZATION_CONSTANTS.factoryURLButtonOkId();

    private static FactoryURLViewUiBinder uiBinder                    = GWT.create(FactoryURLViewUiBinder.class);

    interface FactoryURLViewUiBinder extends UiBinder<Widget, GetCodeNowButtonView> {
    }

//    @UiField
//    CheckBox      showCounter;

//    /** Vertical style radio button. */
//    @UiField
//    RadioButton   verticalStyleField;

//    /** Horizontal style radio button. */
//    @UiField
//    RadioButton   horizontalStyleField;

    /** Preview area is displayed to let the user see the style of configured CodeNow button. */
    @UiField
    Frame         previewFrame;

    @UiField
    TextAreaInput websitesURLField;

    @UiField
    TextAreaInput gitHubURLField;

    @UiField
    TextAreaInput directSharingURLField;

    @UiField
    Image         shareFacebookButton;

    @UiField
    Image         shareGooglePlusButton;

    @UiField
    Image         shareTwitterButton;

    @UiField
    Image         shareEmailButton;

    @UiField
    ImageButton   okButton;

    public GetCodeNowButtonView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        websitesURLField.setName(WEBSITES_URL_FIELD_ID);
        gitHubURLField.setName(GITHUB_URL_FIELD_ID);
        directSharingURLField.setName(DIRECT_SHARING_URL_FIELD_ID);
        okButton.setId(OK_BUTTON_ID);

        websitesURLField.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                websitesURLField.selectAll();
            }
        });
        gitHubURLField.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                gitHubURLField.selectAll();
            }
        });
        directSharingURLField.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                directSharingURLField.selectAll();
            }
        });
    }

//    /**
//     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShowCounterField()
//     */
//    @Override
//    public HasValue<Boolean> getShowCounterField() {
//        return showCounter;
//    }

//    /**
//     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getVerticalStyleField()
//     */
//    @Override
//    public HasValue<Boolean> getVerticalStyleField() {
//        return verticalStyleField;
//    }

//    /**
//     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getHorizontalStyleField()
//     */
//    @Override
//    public HasValue<Boolean> getHorizontalStyleField() {
//        return horizontalStyleField;
//    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getPreviewFrame()
     */
    @Override
    public Frame getPreviewFrame() {
        return previewFrame;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getWebsitesURLField()
     */
    @Override
    public HasValue<String> getWebsitesURLField() {
        return websitesURLField;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getGitHubURLField()
     */
    @Override
    public HasValue<String> getGitHubURLField() {
        return gitHubURLField;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getDirectSharingURLField()
     */
    @Override
    public HasValue<String> getDirectSharingURLField() {
        return directSharingURLField;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareFacebookButton()
     */
    @Override
    public HasClickHandlers getShareFacebookButton() {
        return shareFacebookButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareGooglePlusButton()
     */
    @Override
    public HasClickHandlers getShareGooglePlusButton() {
        return shareGooglePlusButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareTwitterButton()
     */
    @Override
    public HasClickHandlers getShareTwitterButton() {
        return shareTwitterButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareEmailButton()
     */
    @Override
    public HasClickHandlers getShareEmailButton() {
        return shareEmailButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getOkButton()
     */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

}
