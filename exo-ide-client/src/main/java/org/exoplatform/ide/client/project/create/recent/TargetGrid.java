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

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.paas.recent.PaaS;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2011 evgen $
 * 
 */
public class TargetGrid extends ListGrid<PaaS>
{
   private Column<PaaS, String> nameColumn;

   private Column<PaaS, SafeHtml> imageColumn;

   public TargetGrid()
   {
      nameColumn = new Column<PaaS, String>(new TextCell())
      {

         @Override
         public String getValue(PaaS paas)
         {
            return paas.getTitle();
         }
      };

      imageColumn = new Column<PaaS, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final PaaS paas)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return (paas.getImage() != null) ? paas.getImage().toString() : "";
               }
            };
            return html;
         }
      };

      getCellTable().addColumn(imageColumn);
      getCellTable().setColumnWidth(imageColumn, 16, Unit.PX);
      getCellTable().addColumn(nameColumn);
   }
}
