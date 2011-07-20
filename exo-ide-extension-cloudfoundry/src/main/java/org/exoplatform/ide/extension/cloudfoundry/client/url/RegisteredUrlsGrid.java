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
package org.exoplatform.ide.extension.cloudfoundry.client.url;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Grid for displaying registered URLs for application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UnmapUrlGrid.java Jul 19, 2011 10:54:05 AM vereshchaka $
 *
 */
public class RegisteredUrlsGrid extends ListGrid<UrlData>
{
   private final String ID = "ideCloudFoundryUnmapUrlGrid";
   
   private final String URL = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationUnmapUrlGridUrlField();
   
   public RegisteredUrlsGrid()
   {
      super();

      setID(ID);
      
      Column<UrlData, Boolean> checkColumn = new Column<UrlData, Boolean>(new CheckboxCell(true, false))
      {
         @Override
         public Boolean getValue(UrlData object)
         {
            return object.isChecked();
         }
      };


      Column<UrlData, String> valueColumn = new Column<UrlData, String>(new TextCell())
      {
         @Override
         public String getValue(UrlData data)
         {
            return data.getUrl();
         }
      };

      getCellTable().addColumn(checkColumn, "");
      getCellTable().setColumnWidth(checkColumn, "25%");
      getCellTable().addColumn(valueColumn, URL);
      getCellTable().setColumnWidth(valueColumn, "75%");
      
      checkColumn.setFieldUpdater(new FieldUpdater<UrlData, Boolean>()
      {
         @Override
         public void update(int index, UrlData object, Boolean value)
         {
            object.setChecked(value);
         }
      });
   }
}
