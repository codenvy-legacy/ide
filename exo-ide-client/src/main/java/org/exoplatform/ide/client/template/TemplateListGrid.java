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
package org.exoplatform.ide.client.template;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.util.ImageUtil;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateListGrid<T extends Template> extends ListGrid<T>
{
   private static final String ID = "ideCreateFileFromTemplateFormTemplateListGrid";
   
   public TemplateListGrid()
   {
      super();
      setID(ID);
      initColumns();
   }
   
   public void selectLastItem()
   {
      T item = items.get(items.size());
      getCellTable().getSelectionModel().setSelected(item, true);
   }
   
   //------- Implementation ------------------
   
   /**
    * Return URL to icon of template according to type of template:
    * FileTemplate or ProjectTemplate and according to mime type if FileTemplate.
    * 
    * @param template
    * @return String
    */
   private String getItemIcon(Template template)
   {
      if (template instanceof FileTemplate)
      {
         return ImageUtil.getIcon(((FileTemplate)template).getMimeType());
      }
      else if (template instanceof ProjectTemplate)
      {
         return Images.FileTypes.FOLDER;
      }
      
      return null;
   }
   
   /**
    * Create columns.
    */
   private void initColumns()
   {
      //--- icon column -----
      ImageCell iconCell = new ImageCell();
      Column<T, String> iconColumn = new Column<T, String>(iconCell)
      {

         @Override
         public String getValue(T item)
         {
            return getItemIcon(item);
            
         }

      };
      
      getCellTable().addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
      getCellTable().setColumnWidth(iconColumn, 28, Unit.PX);
      
      //--- name column -----
      SafeHtmlCell htmlCell = new SafeHtmlCell();
      Column<T, SafeHtml> nameColumn = new Column<T, SafeHtml>(htmlCell)
      {

         @Override
         public SafeHtml getValue(final T item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (item.getNodeName() == null)
                  {
                     return "<span title=\"" + item.getName() + "\"><font color=\"#FF0000\">" + item.getName()
                        + "</font></span>";
                  }
                  else
                  {
                     return "<span title=\"" + item.getName() + "\">" + item.getName() + "</span>";
                  }
               }
            };
            return html;
         }

      };
      getCellTable().addColumn(nameColumn, "Name");
      getCellTable().setColumnWidth(nameColumn, 60, Unit.PX);
      
      //--- description column -----
      SafeHtmlCell descCell = new SafeHtmlCell();
      Column<T, SafeHtml> entryNameColumn = new Column<T, SafeHtml>(descCell)
      {

         @Override
         public SafeHtml getValue(final T item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (item.getNodeName() == null)
                  {
                     return "<span title=\"" + item.getDescription() + "\"><font color=\"#FF0000\">"
                        + item.getDescription() + "</font></span>";
                  }
                  else
                  {
                     return "<span title=\"" + item.getDescription() + "\">" + item.getDescription() + "</span>";
                  }
               }
            };
            return html;
         }

      };
      
      getCellTable().addColumn(entryNameColumn, "Entry Point");
      getCellTable().setColumnWidth(entryNameColumn, 100, Unit.PCT);
   }

}
