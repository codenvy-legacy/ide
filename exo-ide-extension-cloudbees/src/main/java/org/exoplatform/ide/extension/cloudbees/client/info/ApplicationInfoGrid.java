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
package org.exoplatform.ide.extension.cloudbees.client.info;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

import java.util.Map.Entry;

/**
 * Grid for displaying application information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 1, 2011 3:02:57 PM anya $
 *
 */
public class ApplicationInfoGrid extends ListGrid<Entry<String, String>>
{
   private final String ID = "ideCloudBeesApplicationInfoGrid";
   
   private final String NAME = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridNameField();
   
   private final String VALUE = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridValueField();
   
   public ApplicationInfoGrid()
   {
      super();

      setID(ID);

      Column<Entry<String, String>, SafeHtml> nameColumn = new Column<Entry<String, String>, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final Entry<String, String> entry)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return "<b>" + entry.getKey().toUpperCase() + "</b>";
               }
            };
            return html;
         }
      };
      
      Column<Entry<String, String>, String> valueColumn = new Column<Entry<String, String>, String>(new TextCell())
      {
         @Override
         public String getValue(Entry<String, String> entry)
         {
            return entry.getValue();
         }
      };

      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().setColumnWidth(nameColumn, "35%");
      getCellTable().addColumn(valueColumn, VALUE);
      getCellTable().setColumnWidth(valueColumn, "65%");
   }
}
