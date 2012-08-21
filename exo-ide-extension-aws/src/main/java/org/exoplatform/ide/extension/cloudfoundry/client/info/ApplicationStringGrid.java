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
package org.exoplatform.ide.extension.cloudfoundry.client.info;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

/**
 * Grid for displaying application information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 1, 2011 3:02:57 PM anya $
 *
 */
public class ApplicationStringGrid extends ListGrid<String>
{
   Column<String, String> valueColumn = new Column<String, String>(new TextCell())
   {
      @Override
      public String getValue(String value)
      {
         return value;
      }
   };
   
   public ApplicationStringGrid()
   {
      super();
   }
   
   /**
    * Add one column of list grid.
    * This two actions moved to separate method, because
    * there is need to set the header of column.
    * 
    * @param header
    */
   public void addColumn(String header)
   {
      getCellTable().addColumn(valueColumn, header);
      getCellTable().setColumnWidth(valueColumn, "100%");
   }
}
