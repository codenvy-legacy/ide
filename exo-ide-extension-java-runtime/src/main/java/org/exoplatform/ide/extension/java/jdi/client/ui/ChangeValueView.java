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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;

/**
 * View for change variable value.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValueView.java Apr 28, 2012 9:47:19 AM azatsarynnyy $
 */
public class ChangeValueView extends ViewImpl implements ChangeValuePresenter.Display {

    public static final int HEIGHT = 240;

    public static final int WIDTH = 460;

    public static final String ID = "ideChangeVariableValueView";

    private static final String CHANGE_BUTTON_ID = "ideChangeVariableValueViewChangeButton";

    private static final String CANCEL_BUTTON_ID = "ideChangeVariableValueViewCancelButton";

    private static final String EXPRESSION_FIELD_ID = "ideChangeVariableValueViewExpressionField";

    @UiField
    ImageButton changeButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Label expressionFieldLabel;

    @UiField
    TextAreaInput expressionField;

    interface ChangeVariableValueViewUiBinder extends UiBinder<Widget, ChangeValueView> {
    }

    private static ChangeVariableValueViewUiBinder uiBinder = GWT.create(ChangeVariableValueViewUiBinder.class);

    public ChangeValueView() {
        super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.changeValueViewTitle(), null, WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        expressionField.setName(EXPRESSION_FIELD_ID);
        changeButton.setButtonId(CHANGE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#getChangeButton() */
    @Override
    public HasClickHandlers getChangeButton() {
        return changeButton;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#getExpression() */
    @Override
    public HasValue<String> getExpression() {
        return expressionField;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#setExpression(java.lang.String) */
    @Override
    public void setExpression(String expression) {
        expressionField.setValue(expression);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#focusInExpressionField() */
    @Override
    public void focusInExpressionField() {
        expressionField.focus();
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#setChangeButtonEnable(boolean) */
    @Override
    public void setChangeButtonEnable(boolean isEnable) {
        changeButton.setEnabled(isEnable);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#selectAllText() */
    @Override
    public void selectAllText() {
        expressionField.selectAll();
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#setExpressionFieldTitle(java.lang.String) */
    @Override
    public void setExpressionFieldTitle(String title) {
        expressionFieldLabel.setIsHTML(true);
        expressionFieldLabel.setValue(title);
    }

}
