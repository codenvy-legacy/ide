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
package org.exoplatform.ide.extension.heroku.client.logs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.LogsAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.LogsResponse;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for application's logs view. View must be pointed in Views.gwt.xml file.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 19, 2011 2:28:02 PM anya $
 */
public class LogsPresenter extends GitPresenter implements ShowLogsHandler, LoggedInHandler, ViewClosedHandler {

    public interface Display extends IsView {
        HasClickHandlers getShowLogButton();

        void addLog(String logContent);

        TextFieldItem getLogLinesCount();

        void enableShowLogButton(boolean enable);

        void focusInLogLinesField();
    }

    /** Presenter's display. */
    private Display display;

    /**
     *
     */
    public LogsPresenter() {
        IDE.addHandler(ShowLogsEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getLogLinesCount().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableShowLogButton(isCorrectValue());
            }
        });

        display.getShowLogButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getLogs();
            }
        });

        display.getLogLinesCount().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isCorrectValue()) {
                    getLogs();
                }
            }
        });
    }

    /**
     * Checks, whether the value of log lines max count is entered correctly.
     *
     * @return {@link Boolean} <code>true</code> if value is correct
     */
    private boolean isCorrectValue() {
        boolean enabled =
                display.getLogLinesCount().getValue() != null && !display.getLogLinesCount().getValue().trim().isEmpty();
        try {
            int value = Integer.parseInt(display.getLogLinesCount().getValue());
            return enabled && value > 0 && value <= 500;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** @see org.exoplatform.ide.extension.heroku.client.logs.ShowLogsHandler#onShowLogs(org.exoplatform.ide.extension.heroku.client.logs
     * .ShowLogsEvent) */
    @Override
    public void onShowLogs(ShowLogsEvent event) {
        if (makeSelectionCheck()) {
            getLogs();
        }
    }

    /** Get the application's logs. */
    protected void getLogs() {
        int logLines = (display != null && isCorrectValue()) ? Integer.parseInt(display.getLogLinesCount().getValue()) : 0;

//      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
        String projectId = getSelectedProject().getId();

        try {
            HerokuClientService.getInstance().logs(null, vfs.getId(), projectId, logLines,
                                                   new LogsAsyncRequestCallback(this) {
                                                       @Override
                                                       protected void onSuccess(LogsResponse result) {
                                                           showLogs(result.getLogs());
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }

    }

    /**
     * Display logs content.
     *
     * @param logContent
     *         content of the logs
     */
    protected void showLogs(String logContent) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            display.enableShowLogButton(false);
            display.focusInLogLinesField();
        }
        display.addLog(logContent);
    }

    /** @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client
     * .login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getLogs();
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display)
            display = null;
    }
}
