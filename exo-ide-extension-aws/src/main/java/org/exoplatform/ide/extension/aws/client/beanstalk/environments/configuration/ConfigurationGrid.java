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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

import java.util.Map.Entry;

/**
 * Grid for displaying environment configuration information.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: ConfigurationGrid.java Oct 5, 2012 5:47:11 PM azatsarynnyy $
 *
 */
public class ConfigurationGrid extends ListGrid<Entry<String, String>>
{
   private final String ID = "ideEnvironmentConfigurationGrid";

   private final String NAME = "Name";

   private final String VALUE = "Value";

   public ConfigurationGrid()
   {
      super();

      setID(ID);

      Column<Entry<String, String>, SafeHtml> nameColumn =
         new Column<Entry<String, String>, SafeHtml>(new SafeHtmlCell())
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
                     return "<b>" + entry.getKey() + "</b>";
                  }
               };
               return html;
            }
         };

      Column<Entry<String, String>, SafeHtml> valueColumn =
         new Column<Entry<String, String>, SafeHtml>(new SafeHtmlCell())
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
//                     if ("url".equalsIgnoreCase(entry.getKey()))
//                     {
//                        return "<a href =\"" + entry.getValue() + "\" target=\"_blank\">" + entry.getValue() + "</a>";
//                     }
                     return entry.getValue();
                  }
               };
               return html;
            }
         };

      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().setColumnWidth(nameColumn, "35%");
      getCellTable().addColumn(valueColumn, VALUE);
      getCellTable().setColumnWidth(valueColumn, "65%");
   }
}
