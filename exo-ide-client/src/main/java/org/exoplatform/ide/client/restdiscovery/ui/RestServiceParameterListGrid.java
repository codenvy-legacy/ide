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
package org.exoplatform.ide.client.restdiscovery.ui;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.restdiscovery.ParamExt;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class RestServiceParameterListGrid extends ListGrid<ParamExt>
{
   
   private static final String NAME = IDE.PREFERENCES_CONSTANT.restServiceListGridNameColumn();

   private static final String TYPE = IDE.PREFERENCES_CONSTANT.restServiceListGridTypeColumn();

   private static final String DEFAULT = IDE.PREFERENCES_CONSTANT.restServiceListGridDefaultColumn();

   public RestServiceParameterListGrid()
   {
      initColumns();
   }

   private void initColumns()
   {
      //name column
      Column<ParamExt, SafeHtml> nameColumn = new Column<ParamExt, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final ParamExt item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (item.getParam() == null)
                  {
                     String title = item.getTitle();
                     title = getDivider(title);
                     return title;
                  }
                  return item.getParam().getName();
               }
            };
            return html;
         }
      };
      getCellTable().addColumn(nameColumn, NAME);

      //type column
      Column<ParamExt, SafeHtml> typeColumn = new Column<ParamExt, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final ParamExt item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (item.getParam() == null)
                     return "";
                  return item.getParam().getType().getLocalName();
               }
            };
            return html;
         }

      };
      getCellTable().addColumn(typeColumn, TYPE);

      //column By default
      Column<ParamExt, SafeHtml> defaultColumn = new Column<ParamExt, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final ParamExt item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (item.getParam() == null)
                     return "";
                  return item.getParam().getDefault();
               }
            };
            return html;
         }

      };
      getCellTable().addColumn(defaultColumn, DEFAULT);

   }

   private String getDivider(String title)
   {
      return "<b><font color=\"#3764A3\">" + title + "</font></b>";
   }

}
