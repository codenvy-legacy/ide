/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.create;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasAlignment;

import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.template.ui.TemplateListGrid;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2011 evgen $
 * 
 */
public class ProjectTemplateListGrid extends TemplateListGrid<ProjectTemplate>
{
   @Override
   protected void initColumns()
   {
      // --- icon column -----
      ImageResourceCell iconCell = new ImageResourceCell();
      Column<ProjectTemplate, ImageResource> iconColumn = new Column<ProjectTemplate, ImageResource>(iconCell)
      {
         @Override
         public ImageResource getValue(ProjectTemplate item)
         {
            return getItemIcon(item);
         }
      };

      getCellTable().addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
      iconColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
      getCellTable().setColumnWidth(iconColumn, 20, Unit.PX);

      SafeHtmlCell typeCell = new SafeHtmlCell();
      Column<ProjectTemplate, SafeHtml> entryTypeColumn = new Column<ProjectTemplate, SafeHtml>(typeCell)
      {

         @Override
         public SafeHtml getValue(final ProjectTemplate item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (item.getNodeName() == null)
                  {
                     return "<span title=\"" + item.getType() + "\"><nobr>" + item.getType() + "</nobr></span>";
                  }
                  else
                  {
                     return "<span title=\"" + item.getType() + "\">" + item.getType() + "</span>";
                  }
               }
            };
            return html;
         }

      };

      entryTypeColumn.setCellStyleNames("default-cursor");
      getCellTable().addColumn(entryTypeColumn, "Type");
      getCellTable().setColumnWidth(entryTypeColumn, 60, Unit.PX);

      // --- description column -----
      SafeHtmlCell descCell = new SafeHtmlCell();
      Column<ProjectTemplate, SafeHtml> entryNameColumn = new Column<ProjectTemplate, SafeHtml>(descCell)
      {

         @Override
         public SafeHtml getValue(final ProjectTemplate item)
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
      getCellTable().setColumnWidth(entryNameColumn, 70, Unit.PCT);
   }
}
