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
package org.exoplatform.ide.client.toolbar.customize;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.ImageUtil;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommandItemExListGrid extends ListGrid<CommandItemEx>
{

   public interface Style
   {

      final static String GROUP = "exo-customizeToolbar-commandGroup";

   }

   private final static String TITLE = "Command";

   private final static String ID = "ideCommandItemExListGrid";

   public CommandItemExListGrid()
   {
      super();
      setID(ID);

      initColumns();

   }
   
   private void initColumns()
   {
      Column<CommandItemEx, SafeHtml> titleColumn = new Column<CommandItemEx, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final CommandItemEx item)
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
      getCellTable().addColumn(titleColumn, TITLE);
      getCellTable().setColumnWidth(titleColumn, 60, Unit.PX);
   }

   private String getDivider(String title, String style)
   {
      String divider =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:20px;\">"
            + "<tr><td><hr></td><td class=\"" + style + "\">&nbsp;" + title + "&nbsp;</td><td><hr></td></tr>"
            + "</table>";

      return divider;
   }
   
   private String getItemTitle(CommandItemEx item)
   {
      if (item.isGroup())
      {
         String title = item.getTitle();
         title = title.replace("/", "&nbsp;/&nbsp;");
         title = getDivider(title, Style.GROUP);
         return title;
      }
      else
      {
         String title = "";
         String commandName = item.getCommand().getId();
         if (commandName.indexOf("/") >= 0)
         {
            commandName = commandName.substring(commandName.lastIndexOf("/") + 1);
         }

         while (commandName.indexOf("\\") >= 0)
         {
            commandName = commandName.replace("\\", "/");
         }

         if (item.getCommand() instanceof PopupMenuControl)
         {
            commandName += "&nbsp;[Popup]";
         }

         if (item.getCommand().getNormalImage() != null)
         {
            Image image = new Image(item.getCommand().getNormalImage());
            String imageHTML = ImageUtil.getHTML(image);
            title = "<span>" + imageHTML + "&nbsp;" + commandName + "</span>";
         }
         else if (item.getCommand().getIcon() != null)
         {
            System.out.println(">>> " + item.getCommand().getIcon());
            title = "<span><img src = \"" + item.getCommand().getIcon() + "\"/>&nbsp;" + commandName + "</span>";
         }
         else
         {
            title = "<span>" + commandName + "</span>";
         }

         return title;
      }
   }

}
