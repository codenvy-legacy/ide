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
package org.exoplatform.ide.client.project.create.recent;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.util.ProjectResolver;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2011 evgen $
 * 
 */
public class ProjectTemplateGrid extends ListGrid<ProjectTemplate>
{
   private Column<ProjectTemplate, String> descriptionColumn;

   private Column<ProjectTemplate, ImageResource> imageColumn;

   public ProjectTemplateGrid()
   {
      descriptionColumn = new Column<ProjectTemplate, String>(new TextCell())
      {

         @Override
         public String getValue(ProjectTemplate projectTemplate)
         {
            return projectTemplate.getDescription();
         }
      };

      imageColumn = new Column<ProjectTemplate, ImageResource>(new ImageResourceCell())
      {

         @Override
         public ImageResource getValue(ProjectTemplate projectTemplate)
         {
            return ProjectResolver.getImageForProject(ProjectType.fromValue(projectTemplate.getType()));
         }
      };

      getCellTable().addColumn(imageColumn);
      getCellTable().setColumnWidth(imageColumn, 16, Unit.PX);
      getCellTable().addColumn(descriptionColumn);
   }
}
