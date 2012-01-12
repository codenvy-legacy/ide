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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class PropertiesListGrid extends ListGrid<Property>
{

   public PropertiesListGrid()
   {

      Column<Property, String> nameColumn = new Column<Property, String>(new TextCell())
      {
         @Override
         public String getValue(Property object)
         {
            return PropertyUtil.getHumanReadableName(object.getName());
         }
      };

      Column<Property, String> valueColumn = new Column<Property, String>(new TextCell())
      {
         @Override
         public String getValue(Property object)
         {
            String value = "";
            List values = object.getValue();
            for (Object v : values)
            {
               if (!value.isEmpty())
               {
                  value += "<br>";
               }

               value += v;
            }

            return value;
         }
      };

      nameColumn.setCellStyleNames("default-cursor");
      valueColumn.setCellStyleNames("default-cursor");

      getCellTable().addColumn(nameColumn, "Name");
      getCellTable().addColumn(valueColumn, "Value");
   }

}
