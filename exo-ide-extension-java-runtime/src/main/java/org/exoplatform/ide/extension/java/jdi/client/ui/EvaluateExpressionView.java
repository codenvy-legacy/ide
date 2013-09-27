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
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter;

/**
 * View for evaluate expression.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: EvaluateExpressionView.java May 7, 2012 12:55:19 PM azatsarynnyy $
 */
public class EvaluateExpressionView extends ViewImpl implements EvaluateExpressionPresenter.Display {

    public static final int HEIGHT = 300;

    public static final int WIDTH = 460;

    public static final String ID = "ideEvaluateExpressionView";

    private static final String EVALUATE_BUTTON_ID = "ideEvaluateExpressionViewEvaluateButton";

    private static final String CLOSE_BUTTON_ID = "ideEvaluateExpressionViewCancelButton";

    private static final String EXPRESSION_FIELD_ID = "ideEvaluateExpressionViewExpressionField";

    private static final String RESULT_FIELD_ID = "ideEvaluateExpressionViewResultField";

    @UiField
    ImageButton evaluateButton;

    @UiField
    ImageButton closeButton;

    @UiField
    TextAreaInput expressionField;

    @UiField
    TextAreaInput resultField;

    interface EvaluateExpressionViewUiBinder extends UiBinder<Widget, EvaluateExpressionView> {
    }

    private static EvaluateExpressionViewUiBinder uiBinder = GWT.create(EvaluateExpressionViewUiBinder.class);

    public EvaluateExpressionView() {
        super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.evaluateExpressionViewTitle(), null, WIDTH,
              HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        expressionField.setName(EXPRESSION_FIELD_ID);
        resultField.setName(RESULT_FIELD_ID);
        evaluateButton.setButtonId(EVALUATE_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter.Display#getEvaluateButton() */
    @Override
    public HasClickHandlers getEvaluateButton() {
        return evaluateButton;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter.Display#getExpression() */
    @Override
    public HasValue<String> getExpression() {
        return expressionField;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter.Display#setResult(java.lang.String) */
    @Override
    public void setResult(String value) {
        resultField.setValue(value);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter.Display#focusInExpressionField() */
    @Override
    public void focusInExpressionField() {
        expressionField.focus();
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.EvaluateExpressionPresenter.Display#enableEvaluateButton(boolean) */
    @Override
    public void enableEvaluateButton(boolean enable) {
        evaluateButton.setEnabled(enable);
    }

}
