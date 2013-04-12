/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
