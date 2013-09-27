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
package org.exoplatform.ide.extension.googleappengine.client.logs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

import java.util.LinkedHashMap;

public class LogView extends ViewImpl implements LogsPresenter.Display {
    public static final String VIEW_ID = "ideLogView";

    public static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.logsViewTitle();

    public static final String LOGS_BUTTON_ID = "ideLogViewGetLogsButton";

    public static final String SEVERITY_FIELD_ID = "ideLogViewSeverityField";

    public static final String NUM_DAYS_FIELD_ID = "ideLogViewNumDaysField";

    public static final String LOGS_CONTENT_ID = "ideLogViewLogsContent";

    private static LogViewUiBinder uiBinder = GWT.create(LogViewUiBinder.class);

    interface LogViewUiBinder extends UiBinder<Widget, LogView> {
    }

    @UiField
    Element content;

    @UiField
    TextInput numDaysField;

    @UiField
    SelectItem severityField;

    @UiField
    ImageButton getLogsButton;

    public LogView() {
        super(VIEW_ID, ViewType.OPERATION, TITLE, new Image(GAEClientBundle.INSTANCE.logs()));
        add(uiBinder.createAndBindUi(this));

        content.setId(LOGS_CONTENT_ID);
        numDaysField.setName(NUM_DAYS_FIELD_ID);
        severityField.setName(SEVERITY_FIELD_ID);
        getLogsButton.setButtonId(LOGS_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.logs.LogsPresenter.Display#getLogsButton() */
    @Override
    public HasClickHandlers getLogsButton() {
        return getLogsButton;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.logs.LogsPresenter.Display#getDaysField() */
    @Override
    public HasValue<String> getDaysField() {
        return numDaysField;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.logs.LogsPresenter.Display#getSeverityField() */
    @Override
    public HasValue<String> getSeverityField() {
        return severityField;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.logs.LogsPresenter.Display#setLogs(java.lang.String) */
    @Override
    public void setLogs(String logsContent) {
        content.setInnerText(logsContent);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.logs.LogsPresenter.Display#setSeverities(java.util.LinkedHashMap) */
    @Override
    public void setSeverities(LinkedHashMap<String, String> values) {
        severityField.setValueMap(values);
    }
}
