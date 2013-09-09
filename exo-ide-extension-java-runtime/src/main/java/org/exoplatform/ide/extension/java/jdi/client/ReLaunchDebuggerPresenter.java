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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ReLaunchDebuggerPresenter implements ViewClosedHandler

{
    public interface Display extends IsView {

        HasClickHandlers getCancelButton();
    }

    private Display display;

    private final ApplicationInstance instance;

    public ReLaunchDebuggerPresenter(ApplicationInstance instance) {
        this.instance = instance;
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay(Display d) {
        display = d;

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new StopAppEvent());
                tryConnectDebuger.cancel();
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        tryConnectDebuger.scheduleRepeating(3000);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    protected void connectDebugger() {
        AutoBean<DebuggerInfo> debuggerInfo = DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerInfo.class);
        AutoBeanUnmarshaller<DebuggerInfo> unmarshaller = new AutoBeanUnmarshaller<DebuggerInfo>(debuggerInfo);
        try {
            DebuggerClientService.getInstance().connect(instance.getDebugHost(), instance.getDebugPort(),
                                                        new AsyncRequestCallback<DebuggerInfo>(unmarshaller) {
                                                            @Override
                                                            public void onSuccess(DebuggerInfo result) {
                                                                tryConnectDebuger.cancel();
                                                                IDE.getInstance().closeView(display.asView().getId());
                                                                IDE.eventBus().fireEvent(new DebuggerConnectedEvent(result));
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                // IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                                                            }
                                                        });
        } catch (RequestException e) {
            // IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** A timer for checking events. */
    private Timer tryConnectDebuger = new Timer() {
        @Override
        public void run() {
            connectDebugger();
        }
    };

}
