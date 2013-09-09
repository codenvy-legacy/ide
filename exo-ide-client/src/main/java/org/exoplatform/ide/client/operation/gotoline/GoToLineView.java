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
package org.exoplatform.ide.client.operation.gotoline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class GoToLineView extends ViewImpl implements
                                           org.exoplatform.ide.client.operation.gotoline.GoToLinePresenter.Display {

    private static final String ID = "ideGoToLineForm";

    private static final int WIDTH = 400;

    private static final int HEIGHT = 160;

    private static final String GO_BUTTON_ID = "ideGoToLineFormGoButton";

    private static final String CANCEL_BUTTON_ID = "ideGoToLineFormCancelButton";

    private static final String LINE_NUMBER_FIELD = "ideGoToLineFormLineNumberField";

    private static final String LINE_NUMBER_RANGE_LABEL = "ideGoToLineFormLineRangeLabel";

    private static GoTolineViewUiBinder uiBinder = GWT.create(GoTolineViewUiBinder.class);

    interface GoTolineViewUiBinder extends UiBinder<Widget, GoToLineView> {
    }

    @UiField
    TextInput lineNumberField;

    @UiField
    Label rangeLabel;

    @UiField
    ImageButton goButton;

    @UiField
    ImageButton cancelButton;

    private static final String TITLE = IDE.EDITOR_CONSTANT.goToLineTitle();

    public GoToLineView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.goToLine()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        setCloseOnEscape(true);

        lineNumberField.setName(LINE_NUMBER_FIELD);
        rangeLabel.getElement().setId(LINE_NUMBER_RANGE_LABEL);
        goButton.setButtonId(GO_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.client.EditorPresenter.GoToLinePresenter.Display#getCancelButton() */
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.client.EditorPresenter.GoToLinePresenter.Display#getGoButton() */
    public HasClickHandlers getGoButton() {
        return goButton;
    }

    /** @see org.exoplatform.ide.client.EditorPresenter.GoToLinePresenter.Display#removeFocusFromLineNumber() */
    public void removeFocusFromLineNumber() {
    }

    /** @see org.exoplatform.ide.client.EditorPresenter.edit.action.GoToLinePresenter.Display#getLineNumber() */
    @Override
    public TextFieldItem getLineNumber() {
        return lineNumberField;
    }

    /** @see org.exoplatform.ide.client.edit.GoToLinePresenter.Display#setFocusInLineNumberField() */
    @Override
    public void setFocusInLineNumberField() {
        lineNumberField.focus();
    }

    /** @see org.exoplatform.ide.client.edit.GoToLinePresenter.Display#setCaptionLabel(java.lang.String) */
    @Override
    public void setCaptionLabel(String caption) {
        rangeLabel.setText(caption);
    }

}
