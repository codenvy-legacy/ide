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
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
public class EnvironmentsGrid extends ListGrid<EnvironmentInfo> implements HasEnvironmentActions
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

   private final String VIEW_CONFIGURATION = AWSExtension.LOCALIZATION_CONSTANT.viewConfigurationButton();

   private final String RESTART = AWSExtension.LOCALIZATION_CONSTANT.restartButton();

   private final String REBUILD = AWSExtension.LOCALIZATION_CONSTANT.rebuildButton();

   private final String TERMINATE = AWSExtension.LOCALIZATION_CONSTANT.terminateButton();

   //private Column<EnvironmentInfo, String> configurationColumn;

   private Column<EnvironmentInfo, String> restartColumn;

   private Column<EnvironmentInfo, String> rebuildColumn;

   private Column<EnvironmentInfo, String> terminateColumn;

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
            if (environmentInfo.getEndpointUrl() == null)
            {
               return "";
            }
            return environmentInfo.getEndpointUrl();
         }
      };

//      configurationColumn = new Column<EnvironmentInfo, String>(new ButtonCell())
//      {
//
//         @Override
//         public String getValue(EnvironmentInfo environmentInfo)
//         {
//            return VIEW_CONFIGURATION;
//         }
//      };

      restartColumn = new Column<EnvironmentInfo, String>(new ButtonCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return RESTART;
         }
      };

      rebuildColumn = new Column<EnvironmentInfo, String>(new ButtonCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return REBUILD;
         }
      };

      terminateColumn = new Column<EnvironmentInfo, String>(new ButtonCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return TERMINATE;
         }
      };

      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().addColumn(solutionStackColumn, SOLUTION_STACK);
      getCellTable().setColumnWidth(solutionStackColumn, "130px");
      getCellTable().addColumn(appVersionColumn, VERSION);
      getCellTable().addColumn(statusColumn, STATUS);
      getCellTable().addColumn(healthColumn, HEALTH);
      getCellTable().addColumn(urlColumn, URL);
      getCellTable().setColumnWidth(urlColumn, "130px");
      //getCellTable().addColumn(configurationColumn, VIEW_CONFIGURATION);
      getCellTable().addColumn(restartColumn, RESTART);
      getCellTable().addColumn(rebuildColumn, REBUILD);
      getCellTable().addColumn(terminateColumn, TERMINATE);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.HasEnvironmentActions#addViewConfigurationHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addViewConfigurationHandler(final SelectionHandler<EnvironmentInfo> handler)
   {
//      configurationColumn.setFieldUpdater(new FieldUpdater<EnvironmentInfo, String>()
//      {
//         @Override
//         public void update(int index, EnvironmentInfo environmentInfo, String value)
//         {
//            handler.onSelection(new SelectionEventImpl(environmentInfo));
//         }
//      });
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.HasEnvironmentActions#addRestartHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addRestartHandler(final SelectionHandler<EnvironmentInfo> handler)
   {
      restartColumn.setFieldUpdater(new FieldUpdater<EnvironmentInfo, String>()
      {
         @Override
         public void update(int index, EnvironmentInfo environmentInfo, String value)
         {
            handler.onSelection(new SelectionEventImpl(environmentInfo));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.HasEnvironmentActions#addRebuildHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addRebuildHandler(final SelectionHandler<EnvironmentInfo> handler)
   {
      rebuildColumn.setFieldUpdater(new FieldUpdater<EnvironmentInfo, String>()
      {
         @Override
         public void update(int index, EnvironmentInfo environmentInfo, String value)
         {
            handler.onSelection(new SelectionEventImpl(environmentInfo));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.HasEnvironmentActions#addTerminateHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addTerminateHandler(final SelectionHandler<EnvironmentInfo> handler)
   {
      terminateColumn.setFieldUpdater(new FieldUpdater<EnvironmentInfo, String>()
      {
         @Override
         public void update(int index, EnvironmentInfo environmentInfo, String value)
         {
            handler.onSelection(new SelectionEventImpl(environmentInfo));
         }
      });
   }

   private class SelectionEventImpl extends SelectionEvent<EnvironmentInfo>
   {
      /**
       * @param selectedItem
       */
      protected SelectionEventImpl(EnvironmentInfo selectedItem)
      {
         super(selectedItem);
      }
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
