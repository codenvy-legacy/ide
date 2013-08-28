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
import org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;

/**
 * View for breakpoint properties.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BreakpointPropertiesView.java May 8, 2012 13:47:19 PM azatsarynnyy $
 */
public class BreakpointPropertiesView extends ViewImpl implements BreakpointPropertiesPresenter.Display {

    public static final int HEIGHT = 240;

    public static final int WIDTH = 460;

    public static final String ID = "ideBreakpointPropertiesView";

    private static final String OK_BUTTON_ID = "ideBreakpointPropertiesViewOKButton";

    private static final String CANCEL_BUTTON_ID = "ideBreakpointPropertiesViewCancelButton";

    private static final String CONDITION_FIELD_ID = "ideBreakpointPropertiesViewConditionField";

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    TextAreaInput conditionField;

    interface BreakpointPropertiesViewUiBinder extends UiBinder<Widget, BreakpointPropertiesView> {
    }

    private static BreakpointPropertiesViewUiBinder uiBinder = GWT.create(BreakpointPropertiesViewUiBinder.class);

    public BreakpointPropertiesView() {
        super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.breakpointPropertiesViewTitle(), null, WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        conditionField.setName(CONDITION_FIELD_ID);
        okButton.setButtonId(OK_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter.Display#getOKButton() */
    @Override
    public HasClickHandlers getOKButton() {
        return okButton;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter.Display#getCondition() */
    @Override
    public HasValue<String> getCondition() {
        return conditionField;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter.Display#focusInConditionField() */
    @Override
    public void focusInConditionField() {
        conditionField.focus();
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter.Display#setCondition(java.lang.String) */
    @Override
    public void setCondition(String expression) {
        conditionField.setText(expression);
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.BreakpointPropertiesPresenter.Display#setOkButtonEnable(boolean) */
    @Override
    public void setOkButtonEnable(boolean isEnable) {
        okButton.setEnabled(isEnable);
    }

}
