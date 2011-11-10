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
package org.exoplatform.ide.client.project.fromtemplate;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.template.ui.TemplateListGrid;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 25, 2011 evgen $
 *
 */
public class ProjectTemplateListGrid extends TemplateListGrid<ProjectTemplate>
{
   /**
    * @see org.exoplatform.ide.client.template.ui.TemplateListGrid#initColumns()
    */
   @Override
   protected void initColumns()
   {
      super.initColumns();
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
                     return "<span title=\"" + item.getType() + "\"><font color=\"#FF0000\"><nobr>"
                        + item.getType() + "</nobr></font></span>";
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
      getCellTable().setColumnWidth(entryTypeColumn, 50, Unit.PX);
   }
}
