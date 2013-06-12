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
package com.codenvy.ide.factory.client;

import com.codenvy.ide.factory.client.GetCodeNowButtonPresenter.Display;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
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

    private static final String           ID           = LOCALIZATION_CONSTANTS.factoryURLViewId();

    private static final String           TITLE        = LOCALIZATION_CONSTANTS.factoryURLViewTitle();

    private static final int              HEIGHT       = 350;

    private static final int              WIDTH        = 450;

    private static final String           URL_FIELD_ID = LOCALIZATION_CONSTANTS.factoryURLFieldURL();

    private static final String           OK_BUTTON_ID = LOCALIZATION_CONSTANTS.factoryURLButtonOk();

    private static FactoryURLViewUiBinder uiBinder     = GWT.create(FactoryURLViewUiBinder.class);

    interface FactoryURLViewUiBinder extends UiBinder<Widget, GetCodeNowButtonView> {
    }

    @UiField
    TextInput   urlField;

    @UiField
    ImageButton okButton;

    public GetCodeNowButtonView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        urlField.setName(URL_FIELD_ID);
        okButton.setId(OK_BUTTON_ID);
    }

    /**
     * @see com.codenvy.ide.factory.client.GetCodeNowButtonPresenter.Display#getFactoryURLField()
     */
    @Override
    public TextFieldItem getFactoryURLField() {
        return urlField;
    }

    /**
     * @see com.codenvy.ide.factory.client.GetCodeNowButtonPresenter.Display#getOkButton()
     */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.GetCodeNowButtonPresenter.Display#focusURLField()
     */
    @Override
    public void focusURLField() {
        urlField.focus();
    }

    /**
     * @see com.codenvy.ide.factory.client.GetCodeNowButtonPresenter.Display#selectURLField()
     */
    @Override
    public void selectURLField() {
        urlField.selectAll();
    }

}
