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
package org.exoplatform.ide.client.editor.custom;

import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class EditorsListGrid extends ListGrid<EditorInfo>
{
   Column<EditorInfo, SafeHtml> entryNameColumn;
   
   public EditorsListGrid()
   {     
      SafeHtmlCell htmlCell = new SafeHtmlCell();
      entryNameColumn = new Column<EditorInfo, SafeHtml>(htmlCell)
      {

         @Override
         public SafeHtml getValue(final EditorInfo item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               public String asString()
               {
                  if(item.isDefault())
                   {
                      getCellTable().getSelectionModel().setSelected(item, true);
                      
                      return item.getEditor().getDescription()+"&nbsp;[Default]";         
                   }
                   else
                   {
                      return item.getEditor().getDescription();
                   }
               }
            };
            return html;
         }

      };
      
      entryNameColumn.setSortable(true);
      getCellTable().addColumn(entryNameColumn, "Editors");
      getCellTable().setColumnWidth(entryNameColumn, 100, Unit.PCT);

   }
   
   @Override
   public void setValue(List<EditorInfo> editorInfo)
   {
      super.setValue(editorInfo);
      
      // Add comparator
      getColumnSortHandler().setComparator(entryNameColumn, new Comparator<EditorInfo>()
      {
         public int compare(EditorInfo item1, EditorInfo item2)
         {
            return item1.getEditor().getDescription().compareTo(item2.getEditor().getDescription());
         }
      });
   }
}
