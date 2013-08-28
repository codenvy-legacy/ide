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
package org.exoplatform.ide.extension.logreader.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderHandler;
import org.exoplatform.ide.extension.logreader.client.model.LogReaderService;
import org.exoplatform.ide.extension.logreader.client.ui.LogReaderView;
import org.exoplatform.ide.extension.logreader.shared.LogEntry;

/**
 * Presenter for {@link LogReaderView}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class LogReaderPresenter implements ShowLogReaderHandler, ViewClosedHandler {
    public interface Display extends IsView {
        String ID = "ideExtensionLogReaderView";

        HasClickHandlers getNexLogButton();

        HasClickHandlers getPrevLogButton();

        HasClickHandlers getRefreshLogButton();

        void addLog(String logContent);

        void setPrevLogButtonEnabled(boolean enabled);

        void setNextLogButtonEnabled(boolean enabled);

    }

    private Display display;

    private String currentToken;

    /**
     *
     */
    public LogReaderPresenter() {
        IDE.addHandler(ShowLogReaderEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderHandler#onShowlogReader(org.exoplatform.ide.extension
     * .logreader.client.event.ShowLogReaderEvent) */
    @Override
    public void onShowlogReader(ShowLogReaderEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bind();
            IDE.getInstance().openView(display.asView());
            getLogs();
        } else {
            display.asView().setViewVisible();
        }

    }

    /** Bind view to presenter */
    private void bind() {

        display.getNexLogButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getNextLog();
            }
        });

        display.getPrevLogButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                prevLog();
            }
        });

        display.getRefreshLogButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                refreshLog();
            }
        });
    }

    /**
     *
     */
    private void refreshLog() {
        if (currentToken == null) {
            getLogs();
            return;
        }
        try {
            AutoBean<LogEntry> logEntry = LogReaderExtension.AUTO_BEAN_FACTORY.create(LogEntry.class);
            AutoBeanUnmarshaller<LogEntry> unmarshaller = new AutoBeanUnmarshaller<LogEntry>(logEntry);
            LogReaderService.get().getLog(currentToken, new AsyncRequestCallback<LogEntry>(unmarshaller) {

                @Override
                protected void onSuccess(LogEntry result) {
                    display.addLog(result.getContent());
                    updateButtonState(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get previous log */
    private void prevLog() {
        try {
            AutoBean<LogEntry> logEntry = LogReaderExtension.AUTO_BEAN_FACTORY.create(LogEntry.class);
            AutoBeanUnmarshaller<LogEntry> unmarshaller = new AutoBeanUnmarshaller<LogEntry>(logEntry);
            LogReaderService.get().getPrevLog(currentToken, new AsyncRequestCallback<LogEntry>(unmarshaller) {

                @Override
                protected void onSuccess(LogEntry result) {
                    currentToken = result.getLrtoken();
                    display.addLog(result.getContent());
                    updateButtonState(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }

            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Update log navigation control enabling
     *
     * @param log
     */
    private void updateButtonState(LogEntry log) {
        display.setNextLogButtonEnabled(log.isHasNext());
        display.setPrevLogButtonEnabled(log.isHasPrevious());
    }

    /** Send request to LogReader service */
    private void getLogs() {
        try {
            AutoBean<LogEntry> logEntry = LogReaderExtension.AUTO_BEAN_FACTORY.create(LogEntry.class);
            AutoBeanUnmarshaller<LogEntry> unmarshaller = new AutoBeanUnmarshaller<LogEntry>(logEntry);
            LogReaderService.get().getLastLog(new AsyncRequestCallback<LogEntry>(unmarshaller) {

                @Override
                protected void onSuccess(LogEntry result) {
                    display.addLog(result.getContent());
                    currentToken = result.getLrtoken();
                    updateButtonState(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }

            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get next log */
    private void getNextLog() {
        try {
            AutoBean<LogEntry> logEntry = LogReaderExtension.AUTO_BEAN_FACTORY.create(LogEntry.class);
            AutoBeanUnmarshaller<LogEntry> unmarshaller = new AutoBeanUnmarshaller<LogEntry>(logEntry);
            LogReaderService.get().getNextLog(currentToken, new AsyncRequestCallback<LogEntry>(unmarshaller) {

                @Override
                protected void onSuccess(LogEntry result) {
                    currentToken = result.getLrtoken();
                    display.addLog(result.getContent());
                    updateButtonState(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
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
