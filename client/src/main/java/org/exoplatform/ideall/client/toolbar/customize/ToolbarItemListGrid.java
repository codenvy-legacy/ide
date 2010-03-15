/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.toolbar.customize;

import org.exoplatform.gwtframework.ui.client.component.command.PopupMenuCommand;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarItemListGrid extends ListGrid<ToolbarItem>
{

   public static interface Style
   {

      final static String TOOLBAR_SPACER = "exo-customizeToolbar-spacer";

      final static String TOOLBAR_DELIMITER = "exo-customizeToolbar-delimiter";

   }

   public final static String TOOLBAR = "Toolbar";

   public ToolbarItemListGrid()
   {
      setCanSort(false);
      setCanGroupBy(false);
      setCanFocus(false);
      setSelectionType(SelectionStyle.SINGLE);

      ListGridField toolBarItemsField = new ListGridField(TOOLBAR, TOOLBAR);
      setFields(toolBarItemsField);
   }

   public void selectItem(ToolbarItem item)
   {
      for (ListGridRecord record : getRecords())
      {
         ToolbarItem recordItem = (ToolbarItem)record.getAttributeAsObject(getValuePropertyName());
         if (item == recordItem)
         {
            selectRecord(record);
            return;
         }
      }

      deselectAllRecords();
   }

   private String getDivider(String title, String style)
   {
      String divider =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:20px;\">"
            + "<tr><td><hr></td><td class=\"" + style + "\">&nbsp;" + title + "&nbsp;</td><td><hr></td></tr>"
            + "</table>";

      return divider;
   }

   @Override
   protected void setRecordFields(ListGridRecord record, ToolbarItem item)
   {
      if (item.getType() == ToolbarItem.Type.COMMAND)
      {
         String title = item.getCommand().getId();
         if (title.indexOf("/") >= 0) {
            title = title.substring(title.lastIndexOf("/") + 1);
         }
         
         if (item.getCommand() instanceof PopupMenuCommand) {
            title += "&nbsp;[Popup]";
         }
         
         title =
            "<span>" + Canvas.imgHTML(item.getCommand().getIcon()) + "&nbsp;" + title
               + "</span>";
         record.setAttribute(TOOLBAR, title);
      }
      else if (item.getType() == ToolbarItem.Type.DELIMITER)
      {
         String title = getDivider("Delimiter", Style.TOOLBAR_DELIMITER);
         record.setAttribute(TOOLBAR, title);
      }
      else
      {
         String title = getDivider("Spacer", Style.TOOLBAR_SPACER);
         record.setAttribute(TOOLBAR, title);
      }
   }

}
