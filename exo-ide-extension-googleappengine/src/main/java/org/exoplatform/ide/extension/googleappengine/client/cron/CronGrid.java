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
package org.exoplatform.ide.extension.googleappengine.client.cron;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.model.CronEntry;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 25, 2012 3:07:57 PM anya $
 */
public class CronGrid extends ListGrid<CronEntry> {
    private final String ID = "ideCronGrid";

    private final String URL = GoogleAppEngineExtension.GAE_LOCALIZATION.cronUrlTitle();

    private final String DESCRIPTION = GoogleAppEngineExtension.GAE_LOCALIZATION.cronDescriptionTitle();

    private final String SCHEDULE = GoogleAppEngineExtension.GAE_LOCALIZATION.cronScheduleTitle();

    private final String TIMEZONE = GoogleAppEngineExtension.GAE_LOCALIZATION.cronTimezoneTitle();

    public CronGrid() {
        super();

        setID(ID);

        Column<CronEntry, SafeHtml> urlColumn = new Column<CronEntry, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final CronEntry cronEntry) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return "<b>" + cronEntry.getUrl() + "</b>";
                    }
                };
                return html;
            }
        };

        Column<CronEntry, String> descriptionColumn = new Column<CronEntry, String>(new TextCell()) {
            @Override
            public String getValue(CronEntry cronEntry) {
                return cronEntry.getDescription();
            }
        };

        Column<CronEntry, String> scheduleColumn = new Column<CronEntry, String>(new TextCell()) {
            @Override
            public String getValue(CronEntry cronEntry) {
                return cronEntry.getSchedule();
            }
        };

        Column<CronEntry, String> timezoneColumn = new Column<CronEntry, String>(new TextCell()) {
            @Override
            public String getValue(CronEntry cronEntry) {
                return cronEntry.getTimezone();
            }
        };

        getCellTable().addColumn(urlColumn, URL);
        getCellTable().setColumnWidth(urlColumn, "25%");
        getCellTable().addColumn(descriptionColumn, DESCRIPTION);
        getCellTable().setColumnWidth(descriptionColumn, "30%");
        getCellTable().addColumn(scheduleColumn, SCHEDULE);
        getCellTable().setColumnWidth(scheduleColumn, "25%");
        getCellTable().addColumn(timezoneColumn, TIMEZONE);
        getCellTable().setColumnWidth(timezoneColumn, "20%");
    }
}
