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
package org.exoplatform.ide.client.workspace;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class EntryPointListGrid extends ListGrid<EntryPoint>
{
   private final String ID = "ideEntryPointListGrid"; 
   
   public EntryPointListGrid()
   {
      super();
      setID(ID);

      SafeHtmlCell htmlCell = new SafeHtmlCell();
      Column<EntryPoint, SafeHtml> entryNameColumn = new Column<EntryPoint, SafeHtml>(htmlCell)
      {

         @Override
         public SafeHtml getValue(final EntryPoint item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return "<span title = \"" + item.getHref() + "\">" + item.getHref() + "</span>";
               }
            };
            return html;
         }

      };
      
      getCellTable().addColumn(entryNameColumn, "Entry Point");
      getCellTable().setColumnWidth(entryNameColumn, 100, Unit.PCT);

   }

}
