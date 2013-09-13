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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for displaying application's logs.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 21, 2011 10:09:19 AM anya $
 */
public class LogsView extends ViewImpl implements LogsPresenter.Display {

    private static LogsViewUiBinder uiBinder = GWT.create(LogsViewUiBinder.class);

    interface LogsViewUiBinder extends UiBinder<Widget, LogsView> {
    }

    private static final String ID = "ideLogsView";

    private static final String GET_LOGS_BUTTON_ID = "ideLogsViewGetLogsButton";

    private static final String LOG_LINES_FIELD_ID = "ideLogsViewLogLinesField";

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    Element content;

    @UiField
    Toolbar toolbar;

    /** Field for max number of logs to be shown. */
    @UiField
    TextInput logLinesField;

    /** Get logs button. */
    private IconButton getLogButton;

    public LogsView() {
        super(ID, ViewType.OPERATION, HerokuExtension.LOCALIZATION_CONSTANT.logsViewTitle(), new Image(
                HerokuClientBundle.INSTANCE.logs()));
        add(uiBinder.createAndBindUi(this));

        logLinesField.setName(LOG_LINES_FIELD_ID);
        getLogButton =
                new IconButton(new Image(HerokuClientBundle.INSTANCE.getLogs()), new Image(
                        HerokuClientBundle.INSTANCE.getLogsDisabled()));
        getLogButton.setTitle(HerokuExtension.LOCALIZATION_CONSTANT.logsViewGetLogsButton());
        toolbar.addItem(getLogButton);
    }

    /** @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#addLog(java.lang.String) */
    @Override
    public void addLog(String logContent) {
        content.setInnerText(logContent);
        scrollPanel.scrollToTop();
    }

    /** @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#getShowLogButton() */
    @Override
    public HasClickHandlers getShowLogButton() {
        return getLogButton;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#getLogLinesCount() */
    @Override
    public TextFieldItem getLogLinesCount() {
        return logLinesField;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#enableShowLogButton(boolean) */
    @Override
    public void enableShowLogButton(boolean enable) {
        getLogButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#focusInLogLinesField() */
    @Override
    public void focusInLogLinesField() {
        logLinesField.focus();
    }

}
