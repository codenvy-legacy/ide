/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.toolbar;

import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDE;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;

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

   public final static String TOOLBAR = IDE.PREFERENCES_CONSTANT.toolbarListGridToolbarColumn();

   public final static String COMMAND_ID = "CommandId";

   // public final static String ID = "ideToolbarItemListGrid";
   public final static String ID = "ide.core.customize-toolbar.toolbar-items-list";

   public ToolbarItemListGrid()
   {
      setID(ID);

      initColumns();
   }

   private String getDivider(String title, String style)
   {

      String divider =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:16px;\">"
            + "<tr><td><hr></td><td class=\"" + style + "\">&nbsp;" + title + "&nbsp;</td><td><hr></td></tr>"
            + "</table>";

      return divider;
   }

   private void initColumns()
   {
      Column<ToolbarItem, SafeHtml> titleColumn = new Column<ToolbarItem, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final ToolbarItem item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return getItemTitle(item);
               }
            };
            return html;
         }

      };
      getCellTable().addColumn(titleColumn, TOOLBAR);
      getCellTable().setColumnWidth(titleColumn, 60, Unit.PX);

   }

   private String getItemTitle(ToolbarItem item)
   {
      if (item.getType() == ToolbarItem.Type.DELIMITER)
      {
         String title = getDivider("Delimiter", Style.TOOLBAR_DELIMITER);
         return title;
      }
      else if (item.getType() != ToolbarItem.Type.COMMAND)
      {
         String title = getDivider("Spacer", Style.TOOLBAR_SPACER);
         return title;
      }

      String title = item.getCommand().getId();
      String imageHTML = null;
      if (title.indexOf("/") >= 0)
      {
         title = title.substring(title.lastIndexOf("/") + 1);
      }
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
         imageHTML = ImageHelper.getImageHTML(image);
      }
      else
      {
         imageHTML = "<img src=\"" + item.getCommand().getIcon() + "\"/>";
      }

      return build(imageHTML, title);

   }

   private String build(String image, String command)
   {
      String h =
         "<div style=\"height: 16px; padding:0px; margin:0px; line-height:16px;\">"
            + (image != null ? "<div style=\"width:16px; height:16px; overflow:hidden; float:left;\">" + image
               + "</div>" : "") + "<div style=\"float:left;\">&nbsp;" + command + "</div></div>";
      return h;
   }

}
