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
package org.exoplatform.ide.client.toolbar.customize;

import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ide.client.ImageUtil;

import com.google.gwt.user.client.ui.Image;
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

   public final static String COMMAND_ID = "CommandId";

   public final static String ID = "ideToolbarItemListGrid";

   public ToolbarItemListGrid()
   {
      setID(ID);
      setCanSort(false);
      setCanGroupBy(false);

      // setCanFocus(false); // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."
      
      setSelectionType(SelectionStyle.SINGLE);

      ListGridField toolBarItemsField = new ListGridField(TOOLBAR, TOOLBAR);
      ListGridField idField = new ListGridField(COMMAND_ID, COMMAND_ID);
      idField.setHidden(true);

      setFields(toolBarItemsField, idField);
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
         if (title.indexOf("/") >= 0)
         {
            title = title.substring(title.lastIndexOf("/") + 1);
         }
         String id = title;
         while (title.indexOf("\\") >= 0)
         {
            title = title.replace("\\", "/");
         }

         if (item.getCommand() instanceof PopupMenuControl)
         {
            title += "&nbsp;[Popup]";
         }

         if (item.getCommand().getNormalImage() != null)
         {
            Image image = new Image(item.getCommand().getNormalImage());
            String imageHTML = ImageUtil.getHTML(image);
            title = "<span>" + imageHTML + "&nbsp;" + title + "</span>";
         }
         else
         {
            title = "<span>" + Canvas.imgHTML(item.getCommand().getIcon()) + "&nbsp;" + title + "</span>";
         }

         record.setAttribute(TOOLBAR, title);
         record.setAttribute(COMMAND_ID, id);
      }
      else if (item.getType() == ToolbarItem.Type.DELIMITER)
      {
         String title = getDivider("Delimiter", Style.TOOLBAR_DELIMITER);
         record.setAttribute(TOOLBAR, title);
         record.setAttribute(COMMAND_ID, "Delimiter");
      }
      else
      {
         String title = getDivider("Spacer", Style.TOOLBAR_SPACER);
         record.setAttribute(TOOLBAR, title);
         record.setAttribute(COMMAND_ID, "Spacer");
      }
   }

}
