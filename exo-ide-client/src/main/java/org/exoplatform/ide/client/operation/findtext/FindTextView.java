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
package org.exoplatform.ide.client.operation.findtext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 */
public class FindTextView extends ViewImpl implements
                                           org.exoplatform.ide.client.operation.findtext.FindTextPresenter.Display {

    private static final String ID = "ideFindReplaceTextView";

    private static final int DEFAULT_WIDTH = 470;

    private static final int DEFAULT_HEIGHT = 230;

    private static final String TITLE = IDE.EDITOR_CONSTANT.findTextTitle();

    @UiField
    ImageButton findTextButton;

    @UiField
    ImageButton replaceButton;

    @UiField
    ImageButton replaceFindButton;

    @UiField
    ImageButton replaceAllButton;

    @UiField
    TextInput findTextField;

    @UiField
    TextInput replaceTextField;

    @UiField
    CheckBox caseSensitiveField;

    @UiField
    Label findResultLabel;

    interface FindTextViewUiBinder extends UiBinder<Widget, FindTextView> {
    }

    /** UIBinder instance */
    private static FindTextViewUiBinder uiBinder = GWT.create(FindTextViewUiBinder.class);

    public FindTextView() {
        super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.findText()), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getCaseSensitiveField() */
    public HasValue<Boolean> getCaseSensitiveField() {
        return caseSensitiveField;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getFindButton() */
    public HasClickHandlers getFindButton() {
        return findTextButton;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getFindField() */
    public TextFieldItem getFindField() {
        return findTextField;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceAllButton() */
    public HasClickHandlers getReplaceAllButton() {
        return replaceAllButton;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceButton() */
    public HasClickHandlers getReplaceButton() {
        return replaceButton;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceField() */
    public TextFieldItem getReplaceField() {
        return replaceTextField;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableFindButton(boolean) */
    public void enableFindButton(boolean isEnable) {
        findTextButton.setEnabled(isEnable);
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceFindButton() */
    public HasClickHandlers getReplaceFindButton() {
        return replaceFindButton;
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceFindButton(boolean) */
    public void enableReplaceFindButton(boolean isEnable) {
        replaceFindButton.setEnabled(isEnable);
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceAllButton(boolean) */
    public void enableReplaceAllButton(boolean isEnable) {
        replaceAllButton.setEnabled(isEnable);
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceButton(boolean) */
    public void enableReplaceButton(boolean isEnable) {
        replaceButton.setEnabled(isEnable);
    }

    /** @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getResultLabel() */
    public HasValue<String> getResultLabel() {
        return findResultLabel;
    }

    /** @see org.exoplatform.ide.client.edit.FindTextPresenter.Display#focusInFindField() */
    @Override
    public void focusInFindField() {
        findTextField.focus();
    }

}
