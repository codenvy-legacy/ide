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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * Presenter for change value view.
 * The view must implement {@link ChangeValuePresenter.Display} interface and pointed in Views.gwt.xml file.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValuePresenter.java Apr 28, 2012 9:47:01 AM azatsarynnyy $
 */
public class ChangeValuePresenter implements ChangeValueHandler, ViewClosedHandler {

    public interface Display extends IsView {
        /**
         * Get change button handler.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getChangeButton();

        /**
         * Get cancel button handler.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCancelButton();

        /**
         * Get expression field value.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getExpression();

        /**
         * Set expression field value.
         *
         * @param expression
         *         expression
         */
        void setExpression(String expression);

        /**
         * Change the enable state of the change button.
         *
         * @param isEnable
         *         enabled or not
         */
        void setChangeButtonEnable(boolean isEnable);

        /** Give focus to expression field. */
        void focusInExpressionField();

        /** Select all text in expression field. */
        void selectAllText();

        /**
         * Set title for expression field.
         *
         * @param title
         *         new title for expression field
         */
        void setExpressionFieldTitle(String title);
    }

    /** The display. */
    private Display display;

    /** Variable whose value need to change. */
    private Variable variable;

    /** Connected debugger information. */
    private DebuggerInfo debuggerInfo;

    public ChangeValuePresenter() {
        IDE.addHandler(ChangeValueEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display (view) with presenter. */
    public void bindDisplay() {
        display.getChangeButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doChangeValue();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getExpression().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean isExpressionFieldNotEmpty = (event.getValue() != null && !event.getValue().trim().isEmpty());
                display.setChangeButtonEnable(isExpressionFieldNotEmpty);
            }
        });
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueHandler#onChangeValue(org.exoplatform.ide.extension.java.jdi
     * .client.events.ChangeValueEvent) */
    @Override
    public void onChangeValue(ChangeValueEvent event) {
        variable = event.getVariable();
        debuggerInfo = event.getDebuggerInfo();

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();

            IDE.getInstance().openView(display.asView());

            display.setExpressionFieldTitle(DebuggerExtension.LOCALIZATION_CONSTANT
                                                             .changeValueViewExpressionFieldTitle(variable.getName()));
            display.setExpression(variable.getValue());
            display.focusInExpressionField();
            display.selectAllText();
            display.setChangeButtonEnable(false);
        }
    }

    /** Changes the variable value. */
    private void doChangeValue() {
        final String newValue = display.getExpression().getValue();
        UpdateVariableRequest request = new UpdateVariableRequestImpl(variable.getVariablePath(), newValue);
        try {
            DebuggerClientService.getInstance().setValue(debuggerInfo.getId(), request, new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    IDE.fireEvent(new UpdateVariableValueInTreeEvent(variable, newValue));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }

        IDE.getInstance().closeView(display.asView().getId());
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
