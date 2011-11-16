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
package org.exoplatform.ide.extension.samples.client.github.load;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.shared.Repository;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesListGrid.java Aug 30, 2011 11:43:59 AM vereshchaka $
 *
 */
public class SamplesListGrid extends ListGrid<Repository>
{
   private static final String ID = "ideGithubSamplesGrid";
   
   private static final String REPOSITORY_HEADER = SamplesExtension.LOCALIZATION_CONSTANT.samplesListListColumnName();
   
   public SamplesListGrid()
   {
      super();

      setID(ID);
      
      Column<Repository, SafeHtml> repositoryColumn = new Column<Repository, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final Repository repo)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               public String asString()
               {
                  return repo.getName();
               }
            };
            return html;
         }
      };
      
      getCellTable().addColumn(repositoryColumn, REPOSITORY_HEADER);
   }

}
