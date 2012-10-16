/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 12:28:23 PM anya $
 * 
 */
public class EnvironmentsGrid extends ListGrid<EnvironmentInfo>
{
   public class LinkCell extends AbstractSafeHtmlCell<String>
   {

      public LinkCell()
      {
         super(new SafeHtmlListRenderer());
      }

      @Override
      protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb)
      {
         sb.append(data);
      }

   }

   private class SafeHtmlListRenderer implements SafeHtmlRenderer<String>
   {

      @Override
      public SafeHtml render(String object)
      {
         String string = createLinks(object);
         return new SafeHtmlBuilder().appendHtmlConstant(string).toSafeHtml();
      }

      /**
       * @see com.google.gwt.text.shared.SafeHtmlRenderer#render(java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      public void render(String object, SafeHtmlBuilder builder)
      {
         String string = createLinks(object);
         builder.appendHtmlConstant(string);
      }

   }

   private final String ID = "ideEnvironmentsGrid";

   private final String NAME = AWSExtension.LOCALIZATION_CONSTANT.environmentsGridName();

   private final String SOLUTION_STACK = AWSExtension.LOCALIZATION_CONSTANT.environmentsGridStack();

   private final String VERSION = AWSExtension.LOCALIZATION_CONSTANT.environmentsGridVersion();

   private final String STATUS = AWSExtension.LOCALIZATION_CONSTANT.environmentsGridStatus();

   private final String HEALTH = AWSExtension.LOCALIZATION_CONSTANT.environmentsGridHealth();

   private final String URL = AWSExtension.LOCALIZATION_CONSTANT.environmentsGridUrl();

   public EnvironmentsGrid()
   {
      setID(ID);

      Column<EnvironmentInfo, String> nameColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return environmentInfo.getName();
         }
      };

      Column<EnvironmentInfo, String> solutionStackColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return environmentInfo.getSolutionStackName();
         }
      };

      Column<EnvironmentInfo, String> appVersionColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            if (environmentInfo.getVersionLabel() == null)
            {
               return "No version";
            }
            return environmentInfo.getVersionLabel();
         }
      };

      Column<EnvironmentInfo, String> statusColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return environmentInfo.getStatus().name();
         }
      };

      Column<EnvironmentInfo, String> healthColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return environmentInfo.getHealth().name();
         }
      };

      Column<EnvironmentInfo, String> urlColumn = new Column<EnvironmentInfo, String>(new LinkCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
        	if (environmentInfo.getCname() != null)
            {
               return environmentInfo.getCname();
            }
            if (environmentInfo.getEndpointUrl() != null)
            {
               return environmentInfo.getEndpointUrl();
            }
            return "";
         }
      };

      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().addColumn(solutionStackColumn, SOLUTION_STACK);
      getCellTable().addColumn(appVersionColumn, VERSION);
      getCellTable().addColumn(statusColumn, STATUS);
      getCellTable().addColumn(healthColumn, HEALTH);
      getCellTable().addColumn(urlColumn, URL);
   }

   /**
    * @param object
    * @return
    */
   private String createLinks(String s)
   {
      if (s.isEmpty())
      {
         return "n/a";
      }
      return "<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s
         + " target=\"_blank\">View Running Version</a><br>";
   }

}
