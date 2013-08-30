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
package org.exoplatform.ide.client.operation.geturl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GetItemURLView extends ViewImpl implements
                                             org.exoplatform.ide.client.operation.geturl.GetItemURLPresenter.Display {

    private static final String ID = "ideGetItemURLForm";

    private static final int DEFAULT_WIDTH = 500;

    private static final int DEFAULT_HEIGHT = 220;

    public static final String PRIVATE_URL_FIELD = "ideGetItemURLFormPrivateURLField";

    public static final String PUBLIC_URL_FIELD = "ideGetItemURLFormPublicURLField";

    public static final String ID_OK_BUTTON = "ideGetItemURLFormOkButton";

    @UiField
    TextBox privateUrlField;

    @UiField
    TextBox publicUrlField;

    @UiField
    ImageButton okButton;

    private static final String TITLE = IDE.NAVIGATION_CONSTANT.getItemUrlTitle();

    interface GetItemURLViewUiBinder extends UiBinder<Widget, GetItemURLView> {
    }

    private static GetItemURLViewUiBinder uiBinder = GWT.create(GetItemURLViewUiBinder.class);

    public GetItemURLView() {
        super(ID, ViewType.POPUP, TITLE, new Image(IDEImageBundle.INSTANCE.url()), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCloseOnEscape(true);

        add(uiBinder.createAndBindUi(this));
        okButton.setButtonId(ID_OK_BUTTON);
        privateUrlField.setName(PRIVATE_URL_FIELD);
        publicUrlField.setName(PUBLIC_URL_FIELD);

        new Timer() {
            @Override
            public void run() {
                privateUrlField.selectAll();
                publicUrlField.selectAll();
                publicUrlField.setFocus(true);
            }
        }.schedule(500);
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public HasValue<String> getPrivateURLField() {
        return privateUrlField;
    }

    @Override
    public HasValue<String> getPublicURLField() {
        return publicUrlField;
    }
}