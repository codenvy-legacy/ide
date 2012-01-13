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
package org.exoplatform.ide.client.template.ui;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateListGrid<T extends Template> extends ListGrid<T>
{

   private static final String ID = "ideCreateFileFromTemplateFormTemplateListGrid";

   private static final String NAME = IDE.TEMPLATE_CONSTANT.listGridName();

   protected static final String DESCRIPTION = IDE.TEMPLATE_CONSTANT.listGridDescription();

   public TemplateListGrid()
   {
      super();
      setID(ID);
      initColumns();
   }

   public void selectLastItem()
   {
      T item = items.get(items.size() - 1);
      getCellTable().getSelectionModel().setSelected(item, true);
   }

   // ------- Implementation ------------------

   /**
    * Return URL to icon of template according to type of template: FileTemplate or ProjectTemplate and according to mime type if
    * FileTemplate.
    * 
    * @param template
    * @return String
    */
   protected ImageResource getItemIcon(Template template)
   {
      if (template instanceof FileTemplate)
      {
         return ImageUtil.getIcon(((FileTemplate)template).getMimeType());
      }
      else if (template instanceof ProjectTemplate)
      {
         return ProjectResolver.getImageForProject(((ProjectTemplate)template).getType());
      }

      return null;
   }

   /**
    * Create columns.
    */
   protected void initColumns()
   {
      // --- icon column -----
      ImageResourceCell iconCell = new ImageResourceCell();
      Column<T, ImageResource> iconColumn = new Column<T, ImageResource>(iconCell)
      {
         @Override
         public ImageResource getValue(T item)
         {
            return getItemIcon(item);
         }
      };

      getCellTable().addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
      getCellTable().setColumnWidth(iconColumn, 28, Unit.PX);

      // --- name column -----
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
                  if (item.isDefault())
                  {
                     return "<span title=\"" + item.getName() + "\">" + item.getName() + "</span>";
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

      nameColumn.setCellStyleNames("default-cursor");
      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().setColumnWidth(nameColumn, 40, Unit.PCT);

      // --- description column -----
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
                     return "<span title=\"" + item.getDescription() + "\">" + item.getDescription() + "</span>";
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

      entryNameColumn.setCellStyleNames("default-cursor");
      getCellTable().addColumn(entryNameColumn, DESCRIPTION);
      getCellTable().setColumnWidth(entryNameColumn, 60, Unit.PCT);
   }

}
