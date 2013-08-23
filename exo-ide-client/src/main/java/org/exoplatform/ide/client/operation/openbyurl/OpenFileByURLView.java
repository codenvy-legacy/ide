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
package org.exoplatform.ide.client.operation.openbyurl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * View for opening file by URL.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenFileByURLView extends ViewImpl implements
                                                org.exoplatform.ide.client.operation.openbyurl.OpenFileByURLPresenter.Display {

    /** View ID. */
    public static final String ID = "ide.openFileByURL.view";

    private static final String URL_FIELD_ID = "ide.openFileByURL.view.URL";

    /** Initial width of this view */
    private static final int WIDTH = 550;

    /** Initial height of this view */
    private static final int HEIGHT = 170;

    private static OpenFileByURLViewUiBinder uiBinder = GWT.create(OpenFileByURLViewUiBinder.class);

    interface OpenFileByURLViewUiBinder extends UiBinder<Widget, OpenFileByURLView> {
    }

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLViewTitle();

    /** URL text field. */
    @UiField
    TextInput urlField;

    /** Open button. */
    @UiField
    ImageButton openButton;

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    /** Creates view instance. */
    public OpenFileByURLView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.url()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        urlField.setName(URL_FIELD_ID);
    }

    /** @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#getURLField() */
    @Override
    public TextFieldItem getURLField() {
        return urlField;
    }

    /** @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#getOpenButton() */
    @Override
    public HasClickHandlers getOpenButton() {
        return openButton;
    }

    /** @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#setOpenButtonEnabled(boolean) */
    @Override
    public void setOpenButtonEnabled(boolean enabled) {
        openButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

}
