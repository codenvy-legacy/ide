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

package com.codenvy.ide.ext.gae.client.project.cron;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.shared.CronEntry;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link CronTabPaneView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class CronTabPaneViewImpl extends Composite implements CronTabPaneView {
    interface CronTabPaneViewImplUiBinder extends UiBinder<Widget, CronTabPaneViewImpl> {
    }

    private static CronTabPaneViewImplUiBinder uiBinder = GWT.create(CronTabPaneViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<CronEntry> cronTable = new CellTable<CronEntry>();

    @UiField
    Button btnUpdate;

    @UiField(provided = true)
    GAELocalization constant;

    private ActionDelegate delegate;

    /**
     * Constructor for View.
     */
    @Inject
    public CronTabPaneViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initCronTable();

        initWidget(widget);
    }

    /**
     * Initialize cron Cell table.
     */
    private void initCronTable() {
        cronTable.setWidth("100%", true);
        cronTable.setAutoHeaderRefreshDisabled(true);
        cronTable.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyPanel = new HTMLPanel("No crons found.");
        cronTable.setEmptyTableWidget(emptyPanel);

        final SelectionModel<CronEntry> selectionModel = new NoSelectionModel<CronEntry>();
        cronTable.setSelectionModel(selectionModel);

        Column<CronEntry, SafeHtml> urlColumn = new Column<CronEntry, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final CronEntry object) {
                return new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return "<b>" + object.getUrl() + "</b>";
                    }
                };
            }
        };

        Column<CronEntry, String> descriptionColumn = new Column<CronEntry, String>(new TextCell()) {
            @Override
            public String getValue(CronEntry object) {
                return object.getDescription();
            }
        };

        Column<CronEntry, String> scheduleColumn = new Column<CronEntry, String>(new TextCell()) {
            @Override
            public String getValue(CronEntry object) {
                return object.getSchedule();
            }
        };

        Column<CronEntry, String> timezoneColumn = new Column<CronEntry, String>(new TextCell()) {
            @Override
            public String getValue(CronEntry object) {
                return object.getTimezone();
            }
        };

        cronTable.addColumn(urlColumn, constant.cronUrlTitle());
        cronTable.setColumnWidth(urlColumn, "25%");
        cronTable.addColumn(descriptionColumn, constant.cronDescriptionTitle());
        cronTable.setColumnWidth(descriptionColumn, "30%");
        cronTable.addColumn(scheduleColumn, constant.cronDescriptionTitle());
        cronTable.setColumnWidth(scheduleColumn, "25%");
        cronTable.addColumn(timezoneColumn, constant.cronDescriptionTitle());
        cronTable.setColumnWidth(timezoneColumn, "20%");
    }

    /** {@inheritDoc} */
    @Override
    public void setCronEntryData(JsonArray<CronEntry> entries) {
        List<CronEntry> cronEntries = new ArrayList<CronEntry>(entries.size());
        for (int i = 0; i < entries.size(); i++) {
            cronEntries.add(entries.get(i));
        }

        cronTable.setRowData(cronEntries);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnUpdate")
    public void onUpdateButtonClicked(ClickEvent event) {
        delegate.onUpdateButtonClicked();
    }
}
