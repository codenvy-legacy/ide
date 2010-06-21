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

import org.exoplatform.gwtframework.ui.client.component.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ideall.client.ImageUtil;

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

public class CommandItemExListGrid extends ListGrid<CommandItemEx>
{

   public interface Style
   {

      final static String GROUP = "exo-customizeToolbar-commandGroup";

   }

   private final static String TITLE = "Command";

   public CommandItemExListGrid()
   {
      setCanSort(false);
      setCanGroupBy(false);
      setCanFocus(false);
      setSelectionType(SelectionStyle.SINGLE);

      ListGridField fieldName = new ListGridField(TITLE, TITLE);
      setFields(fieldName);
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
   protected void setRecordFields(ListGridRecord record, CommandItemEx item)
   {
      if (item.isGroup())
      {
         String title = item.getTitle();
         title = title.replace("/", "&nbsp;/&nbsp;");
         title = getDivider(title, Style.GROUP);
         record.setAttribute(TITLE, title);
      }
      else
      {
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

         String title = "";
         if (item.getCommand().getNormalImage() != null)
         {
            Image image = new Image(item.getCommand().getNormalImage());
            String imageHTML = ImageUtil.getHTML(image);
            title = "<span>" + imageHTML + "&nbsp;" + commandName + "</span>";
         }
         else

         //         if (item.getCommand().getNormalImage() != null) {
         //            FlowPanel p = new FlowPanel();
         //            DOM.setStyleAttribute(p.getElement(), "left", "-1000px");
         //            DOM.setStyleAttribute(p.getElement(), "top", "-1000px");
         //            DOM.setStyleAttribute(p.getElement(), "width", "16px");
         //            DOM.setStyleAttribute(p.getElement(), "height", "16px");
         //            DOM.setStyleAttribute(p.getElement(), "overflow", "hidden");
         //            RootPanel.get().add(p);            
         //            
         //            Image image = new Image(item.getCommand().getNormalImage());
         //            p.add(image);
         //            String imageHTML = DOM.getInnerHTML(p.getElement());
         //            title = "<span>" + imageHTML + "&nbsp;" + commandName + "</span>";
         //         } else {
         //            title = "<span>" + Canvas.imgHTML(item.getCommand().getIcon()) + "&nbsp;" + commandName + "</span>";
         //         }

         if (item.getCommand().getIcon() != null)
         {
            title = "<span>" + Canvas.imgHTML(item.getCommand().getIcon()) + "&nbsp;" + commandName + "</span>";
         }
         else
         {
            title = "<span>" + commandName + "</span>";
         }

         record.setAttribute(TITLE, title);
      }

   }

}
