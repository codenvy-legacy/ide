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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesListGrid.java Aug 30, 2011 11:43:59 AM vereshchaka $
 *
 */
public class GitHubProjectsListGrid extends ListGrid<ProjectData>
{
   private static final String ID = "ideGithubProjectsGrid";
   
   private static final String REPOSITORY_HEADER = SamplesExtension.LOCALIZATION_CONSTANT.samplesListRepositoryColumn();
   
   private static final String DESCRIPTION_HEADER = SamplesExtension.LOCALIZATION_CONSTANT.samplesListDescriptionColumn();
      
   public GitHubProjectsListGrid()
   {
      super();

      setID(ID);
      
      //Image column
      Column<ProjectData, ImageResource> iconColumn = new Column<ProjectData, ImageResource>(new ImageResourceCell())
      {
         @Override
         public ImageResource getValue(ProjectData item)
         {
            return ProjectResolver.getImageForProject(item.getType());
         }
      };
      
      Column<ProjectData, SafeHtml> repositoryColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final ProjectData item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               public String asString()
               {
                  return item.getName();
               }
            };
            return html;
         }
      };
      
      Column<ProjectData, SafeHtml> descriptionColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final ProjectData item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               public String asString()
               {
                  return "<span>" + item.getDescription() + "</span>";
               }
            };
            return html;
         }
      };
      
      getCellTable().addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
      getCellTable().setColumnWidth(iconColumn, 28, Unit.PX);
      
      getCellTable().addColumn(repositoryColumn, REPOSITORY_HEADER);
      getCellTable().addColumn(descriptionColumn, DESCRIPTION_HEADER);
   }
   
}
