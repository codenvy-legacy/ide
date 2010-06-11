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
package org.exoplatform.ideall.client.hotkeys;

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

public class HotKeyItemListGrid extends ListGrid<HotKeyItem>
{

   public interface Style
   {

      final static String GROUP = "exo-customizeToolbar-commandGroup";

   }

   private final static String TITLE = "Control";
   
   private final static String CONTROL = "Binding";
   
   private final static String GROUP = "Group";
   
   public HotKeyItemListGrid()
   {
      setCanSort(false);
      setCanGroupBy(false);
      setCanFocus(false);
      setSelectionType(SelectionStyle.SINGLE);
      setCanFreezeFields(false);
      setGroupStartOpen("all");
      setGroupByField(GROUP);
      
      ListGridField fieldName = new ListGridField(TITLE, TITLE);
//      fieldName.setCanHide(false);
      
      ListGridField fieldControl = new ListGridField(CONTROL, CONTROL);
//      fieldControl.setCanHide(false);
      
      ListGridField fieldGroup = new ListGridField(GROUP, GROUP);
      fieldGroup.setHidden(true);
      
      setFields(fieldName, fieldControl, fieldGroup);
   }
   
   @Override
   protected void setRecordFields(ListGridRecord record, HotKeyItem item)
   {
      String controlName = item.getControlId();
      
      if (controlName.indexOf("/") >= 0)
      {
         controlName = controlName.substring(controlName.lastIndexOf("/") + 1);
      }
      
      while (controlName.indexOf("\\") >= 0) {
         controlName = controlName.replace("\\", "/");
      }
      
      String title = "<span>" + Canvas.imgHTML(item.getIcon()) + "&nbsp;" + controlName + "</span>";
      
      record.setAttribute(TITLE, title);
      record.setAttribute(CONTROL, item.getHotKey());
      record.setAttribute(GROUP, item.getGroup());
   }

}
