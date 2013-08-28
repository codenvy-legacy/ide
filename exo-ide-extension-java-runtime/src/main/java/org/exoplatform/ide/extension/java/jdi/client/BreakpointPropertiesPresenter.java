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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.*;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

/**
 * Presenter for breakpoint properties view. The view must implement {@link BreakpointPropertiesPresenter.Display} interface and
 * pointed in Views.gwt.xml file.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BreakpointPropertiesPresenter.java May 8, 2012 13:47:01 PM azatsarynnyy $
 */
public class BreakpointPropertiesPresenter implements ShowBreakpointPropertiesHandler, ViewClosedHandler,
                                                      DebuggerConnectedHandler, DebuggerDisconnectedHandler {

    public interface Display extends IsView {
        /**
         * Get OK button handler.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getOKButton();

        /**
         * Get cancel button handler.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCancelButton();

        /**
         * Get condition field value.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getCondition();

        /** Set condition field value. */
        void setCondition(String expression);

        /** Give focus to condition field. */
        void focusInConditionField();

        /**
         * Change the enable state of the OK button.
         *
         * @param isEnable
         *         enabled or not
         */
        void setOkButtonEnable(boolean isEnable);
    }

    /** The display. */
    private Display display;

    /** Current breakpoint. */
    private BreakPoint currentBreakPoint;

    /** Connected debugger. */
    private DebuggerInfo debuggerInfo;

    public BreakpointPropertiesPresenter() {
        IDE.addHandler(ShowBreakpointPropertiesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(DebuggerConnectedEvent.TYPE, this);
        IDE.addHandler(DebuggerDisconnectedEvent.TYPE, this);
    }

    /** Bind display (view) with presenter. */
    public void bindDisplay() {
        display.getOKButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateProperties();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** Update breakpoint's properties. */
    private void updateProperties() {
        setCondition();
    }

    /** Set breakpoint's condition. */
    private void setCondition() {
        if (currentBreakPoint == null) {
            return;
        }

        currentBreakPoint.setCondition(display.getCondition().getValue());

        // delete breakpoint and add it with condition
        try {
            DebuggerClientService.getInstance().deleteBreakPoint(debuggerInfo.getId(), currentBreakPoint,
                                                                 new AsyncRequestCallback<BreakPoint>() {
                                                                     @Override
                                                                     protected void onSuccess(BreakPoint result) {
                                                                         try {
                                                                             DebuggerClientService.getInstance()
                                                                                                  .addBreakPoint(debuggerInfo.getId(),
                                                                                                                 currentBreakPoint,
                                                                                                                 new





































































                                                                                                                         AsyncRequestCallback<BreakPoint>() {
                                                                                                                             @Override
                                                                                                                             protected void onSuccess(
                                                                                                                                     BreakPoint
                                                                                                                                             result) {
                                                                                                                             }

                                                                                                                             @Override
                                                                                                                             protected void
                                                                                                                             onFailure(
                                                                                                                                     Throwable exception) {
                                                                                                                                 if (exception

                                                                                                                                         instanceof ServerException) {
                                                                                                                                     ServerException
                                                                                                                                             e =
                                                                                                                                             (ServerException)exception;
                                                                                                                                     if (e.isErrorMessageProvided()) {
                                                                                                                                         IDE.fireEvent(
                                                                                                                                                 new OutputEvent(
                                                                                                                                                         e.getMessage(),
                                                                                                                                                         Type.ERROR));
                                                                                                                                         return;
                                                                                                                                     }
                                                                                                                                 }

                                                                                                                                 IDE.fireEvent(
                                                                                                                                         new OutputEvent(
                                                                                                                                                 "Can't save breakpoint properties",
                                                                                                                                                 Type.ERROR));
                                                                                                                             }
                                                                                                                         });
                                                                         } catch (RequestException e) {
                                                                             IDE.fireEvent(new ExceptionThrownEvent(e));
                                                                         }
                                                                     }

                                                                     @Override
                                                                     protected void onFailure(Throwable exception) {
                                                                         if (exception instanceof ServerException) {
                                                                             ServerException e = (ServerException)exception;
                                                                             if (e.isErrorMessageProvided()) {
                                                                                 IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
                                                                                 return;
                                                                             }
                                                                         }

                                                                         IDE.fireEvent(new OutputEvent("Can't save breakpoint properties",
                                                                                                       Type.ERROR));
                                                                     }
                                                                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }

        IDE.getInstance().closeView(display.asView().getId());
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.ShowBreakpointPropertiesHandler#onShowBreakpointProperties(org.exoplatform.ide.extension.java.jdi.client.events.ShowBreakpointPropertiesEvent) */
    @Override
    public void onShowBreakpointProperties(ShowBreakpointPropertiesEvent event) {
        currentBreakPoint = event.getBreakPoint();

        if (currentBreakPoint == null) {
            return;
        }

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();

            IDE.getInstance().openView(display.asView());

            display.focusInConditionField();
        }

        loadProperties();
    }

    /** Load breakpoint's properties. */
    private void loadProperties() {
        if (currentBreakPoint != null) {
            display.setCondition(currentBreakPoint.getCondition());
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler#onDebuggerConnected(org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent) */
    @Override
    public void onDebuggerConnected(DebuggerConnectedEvent event) {
        debuggerInfo = event.getDebuggerInfo();
        if (display != null) {
            display.setOkButtonEnable(true);
        }
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler#onDebuggerDisconnected(org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent) */
    @Override
    public void onDebuggerDisconnected(DebuggerDisconnectedEvent event) {
        debuggerInfo = null;
        if (display != null) {
            display.setOkButtonEnable(false);
        }
    }

}
