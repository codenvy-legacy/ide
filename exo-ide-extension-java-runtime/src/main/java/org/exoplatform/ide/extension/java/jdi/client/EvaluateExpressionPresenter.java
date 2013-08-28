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
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.EvaluateExpressionEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.EvaluateExpressionHandler;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

/**
 * Presenter for evaluate expression view.
 * The view must implement {@link EvaluateExpressionPresenter.Display} interface and pointed in Views.gwt.xml file.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: EvaluateExpressionPresenter.java May 7, 2012 13:29:01 PM azatsarynnyy $
 */
public class EvaluateExpressionPresenter implements EvaluateExpressionHandler, ViewClosedHandler {

    public interface Display extends IsView {
        /**
         * Get evaluate button handler.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getEvaluateButton();

        /**
         * Get close button handler.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCloseButton();

        /**
         * Get expression field value.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getExpression();

        /**
         * Set result field value.
         *
         * @param value
         *         result field value
         */
        void setResult(String value);

        /**
         * Change the enable state of the evalaute button.
         *
         * @param enable
         *         enabled or not
         */
        void enableEvaluateButton(boolean enable);

        /** Give focus to expression field. */
        void focusInExpressionField();
    }

    /** The display. */
    private Display display;

    /** Connected debugger information. */
    private DebuggerInfo debuggerInfo;

    public EvaluateExpressionPresenter() {
        IDE.addHandler(EvaluateExpressionEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display (view) with presenter. */
    public void bindDisplay() {
        display.getEvaluateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doEvaluateExpression();
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getExpression().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean isExpressionFieldNotEmpty = (event.getValue() != null && !event.getValue().trim().isEmpty());
                display.enableEvaluateButton(isExpressionFieldNotEmpty);
            }
        });
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.EvaluateExpressionHandler#onEvaluateExpression(org.exoplatform.ide
     * .extension.java.jdi.client.events.EvaluateExpressionEvent) */
    @Override
    public void onEvaluateExpression(EvaluateExpressionEvent event) {
        debuggerInfo = event.getDebuggerInfo();

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();

            IDE.getInstance().openView(display.asView());

            display.enableEvaluateButton(false);
            display.focusInExpressionField();
        }
    }

    /** Evaluate expression. */
    private void doEvaluateExpression() {
        display.enableEvaluateButton(false);

        try {
            DebuggerClientService.getInstance().evaluateExpression(debuggerInfo.getId(),
                                                                   display.getExpression().getValue(),
                                                                   new AsyncRequestCallback<StringBuilder>(
                                                                           new StringUnmarshaller(new StringBuilder())) {
                                                                       @Override
                                                                       protected void onSuccess(StringBuilder result) {
                                                                           display.setResult(result.toString());
                                                                           display.enableEvaluateButton(true);
                                                                       }

                                                                       @Override
                                                                       protected void onFailure(Throwable exception) {
                                                                           String errorMessage = DebuggerExtension.LOCALIZATION_CONSTANT
                                                                                                                  .evaluateExpressionFailed(
                                                                                                                          exception













                                                                                                                                  .getMessage());
                                                                           display.setResult(errorMessage);
                                                                           display.enableEvaluateButton(true);
                                                                       }
                                                                   });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
            display.enableEvaluateButton(true);
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Deserializer for response's body. */
    private class StringUnmarshaller implements Unmarshallable<StringBuilder> {

        protected StringBuilder builder;

        /** @param callback */
        public StringUnmarshaller(StringBuilder builder) {
            this.builder = builder;
        }

        /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
        @Override
        public void unmarshal(Response response) {
            builder.append(response.getText());
        }

        @Override
        public StringBuilder getPayload() {
            return builder;
        }
    }

}
