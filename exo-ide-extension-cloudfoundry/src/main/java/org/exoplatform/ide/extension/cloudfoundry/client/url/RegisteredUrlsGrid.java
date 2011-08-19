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

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
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
public class RegisteredUrlsGrid extends ListGrid<String> implements HasUnmapClickHandler
{
   private final String ID = "ideCloudFoundryUnmapUrlGrid";
   
   private final String URL = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationUnmapUrlGridUrlField();
   
   private final String UNMAP_BUTTON_TITLE = CloudFoundryExtension.LOCALIZATION_CONSTANT.unmapButton();
   
   private final String UNMAP_COLUMN_HEADER = CloudFoundryExtension.LOCALIZATION_CONSTANT.unmapUrlListGridColumnTitle();
   
   private Column<String, String> buttonColumn;
   
   public RegisteredUrlsGrid()
   {
      super();

      setID(ID);
      
      buttonColumn = new Column<String, String>(new ButtonCell())
      {
         @Override
         public String getValue(String object)
         {
            return UNMAP_BUTTON_TITLE;
         }
      };
      
      Column<String, SafeHtml> valueColumn = new Column<String, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final String url)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               public String asString()
               {
                  return "<a target=\"_blank\" href=\"http://" + url + "\">" + url + "</a>";
               }
            };
            return html;
         }
      };

      getCellTable().addColumn(valueColumn, URL);
      getCellTable().setColumnWidth(valueColumn, "75%");
      getCellTable().addColumn(buttonColumn, UNMAP_COLUMN_HEADER);
      getCellTable().setColumnWidth(buttonColumn, "25%");
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.url.HasUnmapClickHandler#addUnmapClickHandler(com.google.gwt.event.dom.client.ClickHandler)
    */
   @Override
   public void addUnmapClickHandler(final UnmapHandler handler)
   {
      buttonColumn.setFieldUpdater(new FieldUpdater<String, String>()
      {
         @Override
         public void update(int index, String url, String value)
         {
            handler.onUnmapUrl(url);
         }
      });
   }
}
