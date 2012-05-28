/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 * 
 */
public class CronGrid extends ListGrid<CronEntry>
{
   private final String ID = "ideCronGrid";

   private final String URL = GoogleAppEngineExtension.GAE_LOCALIZATION.cronUrlTitle();

   private final String DESCRIPTION = GoogleAppEngineExtension.GAE_LOCALIZATION.cronDescriptionTitle();

   private final String SCHEDULE = GoogleAppEngineExtension.GAE_LOCALIZATION.cronScheduleTitle();

   private final String TIMEZONE = GoogleAppEngineExtension.GAE_LOCALIZATION.cronTimezoneTitle();

   public CronGrid()
   {
      super();

      setID(ID);

      Column<CronEntry, SafeHtml> urlColumn = new Column<CronEntry, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final CronEntry cronEntry)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return "<a>" + cronEntry.getUrl() + "</a>";
               }
            };
            return html;
         }
      };

      Column<CronEntry, String> descriptionColumn = new Column<CronEntry, String>(new TextCell())
      {
         @Override
         public String getValue(CronEntry cronEntry)
         {
            return cronEntry.getDescription();
         }
      };

      Column<CronEntry, String> scheduleColumn = new Column<CronEntry, String>(new TextCell())
      {
         @Override
         public String getValue(CronEntry cronEntry)
         {
            return cronEntry.getSchedule();
         }
      };

      Column<CronEntry, String> timezoneColumn = new Column<CronEntry, String>(new TextCell())
      {
         @Override
         public String getValue(CronEntry cronEntry)
         {
            return cronEntry.getTimezone();
         }
      };

      getCellTable().addColumn(urlColumn, URL);
      getCellTable().setColumnWidth(urlColumn, "35%");
      getCellTable().addColumn(descriptionColumn, DESCRIPTION);
      getCellTable().setColumnWidth(descriptionColumn, "35%");
      getCellTable().addColumn(scheduleColumn, SCHEDULE);
      getCellTable().setColumnWidth(scheduleColumn, "15%");
      getCellTable().addColumn(timezoneColumn, TIMEZONE);
      getCellTable().setColumnWidth(timezoneColumn, "15%");
   }
}
