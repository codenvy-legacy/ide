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
package org.exoplatform.ide.client.hotkeys;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CustomizeHotKeysView extends ViewImpl implements
                                                   org.exoplatform.ide.client.hotkeys.CustomizeHotKeysPresenter.Display {

    public static final String ID = "ideCustomizeHotKeysView";

    public static final String MSG_LABEL_ID = "ideCustomizeHotKeysMessageLabel";

    private static final String TITLE = IDE.PREFERENCES_CONSTANT.customizeHotkeysTitle();

    /** Initial width of this view */
    private static final int WIDTH = 725;

    /** Initial height of this view */
    private static final int HEIGHT = 390;

    private static CustomizeHotKeysViewUiBinder uiBinder = GWT.create(CustomizeHotKeysViewUiBinder.class);

    interface CustomizeHotKeysViewUiBinder extends UiBinder<Widget, CustomizeHotKeysView> {
    }

    @UiField
    ImageButton bindButton;

    @UiField
    ImageButton unbindButton;

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton defaultsButton;

    @UiField
    HotKeyItemListGrid hotKeyItemListGrid;

    @UiField
    TextInput hotKeyField;

    @UiField
    Label messageLabel;

    public CustomizeHotKeysView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.customizeHotKeys()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        messageLabel.getElement().setId(MSG_LABEL_ID);

        hotKeyField.setReadOnly(true);
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public HasClickHandlers getDefaultsButton() {
        return defaultsButton;
    }

    @Override
    public HasClickHandlers getBindButton() {
        return bindButton;
    }

    @Override
    public HasClickHandlers getUnbindButton() {
        return unbindButton;
    }

    @Override
    public ListGridItem<HotKeyItem> getHotKeyItemListGrid() {
        return hotKeyItemListGrid;
    }

    @Override
    public HasValue<String> getHotKeyField() {
        return hotKeyField;
    }

    @Override
    public HotKeyItem getSelectedItem() {
        return hotKeyItemListGrid.getSelectedItems().get(0);
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        okButton.setEnabled(enabled);
    }

    @Override
    public void setBindButtonEnabled(boolean enabled) {
        bindButton.setEnabled(enabled);
    }

    @Override
    public void setUnbindButtonEnabled(boolean enabled) {
        unbindButton.setEnabled(enabled);
    }

    @Override
    public void setHotKeyFieldEnabled(boolean enabled) {
        hotKeyField.setEnabled(enabled);
    }

    @Override
    public void focusOnHotKeyField() {
        hotKeyField.focus();
    }

    @Override
    public void showError(String text) {
        messageLabel.setText(text);
    }

}
