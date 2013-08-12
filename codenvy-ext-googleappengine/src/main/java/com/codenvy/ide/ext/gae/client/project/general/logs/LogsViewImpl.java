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

package com.codenvy.ide.ext.gae.client.project.general.logs;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.base.BaseView;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link LogsView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 06.08.13 vlad $
 */
@Singleton
public class LogsViewImpl extends BaseView<LogsView.ActionDelegate> implements LogsView {
    interface LogsViewImplUiBinder extends UiBinder<Widget, LogsViewImpl> {
    }

    private static LogsViewImplUiBinder uiBinder = GWT.create(LogsViewImplUiBinder.class);

    @UiField
    Button btnGetLogs;

    @UiField
    TextBox daysCount;

    @UiField
    FlowPanel output;

    @UiField
    ListBox severity;

    @UiField(provided = true)
    GAELocalization constant;

    /**
     * Constructor for View.
     */
    @Inject
    public LogsViewImpl(PartStackUIResources partStackUIResources, GAELocalization constant) {
        super(partStackUIResources);

        this.constant = constant;

        container.add(uiBinder.createAndBindUi(this));

        daysCount.setMaxLength(3);

        daysCount.addKeyPressHandler(numbersOnlyHandler);

        initSeverityListBox();
    }

    /**
     * Initialize severity list box with default values.
     */
    private void initSeverityListBox() {
        severity.addItem("All");
        severity.addItem("Error");
        severity.addItem("Info");
        severity.addItem("Warning");
        severity.addItem("Debug");
        severity.addItem("Critical");
    }

    /** {@inheritDoc} */
    @Override
    public void setLogsContent(String content) {
        output.add(new HTML(content));
    }

    /** {@inheritDoc} */
    @Override
    public int getLogsDaysCount() {
        String days = daysCount.getText();

        return (days != null && !days.isEmpty()) ? Integer.parseInt(days) : 1;
    }

    /** {@inheritDoc} */
    @Override
    public String getLogsSeverity() {
        return severity.getItemText(severity.getSelectedIndex());
    }

    /**
     * Handler that checks input text to prevent input text chars.
     */
    KeyPressHandler numbersOnlyHandler = new KeyPressHandler() {
        @Override
        public void onKeyPress(KeyPressEvent event) {
            TextBox countField = (TextBox)event.getSource();

            if (countField.isReadOnly() || !countField.isEnabled()) {
                return;
            }

            Character ch = event.getCharCode();
            int unicodeChCode = event.getUnicodeCharCode();

            if (!(Character.isDigit(ch) || unicodeChCode == 0)) {
                countField.cancelKey();
            }
        }
    };

    @UiHandler("btnGetLogs")
    public void onGetLogsButtonClicked(ClickEvent event) {
        delegate.onGetLogsButtonClicked();
    }
}
