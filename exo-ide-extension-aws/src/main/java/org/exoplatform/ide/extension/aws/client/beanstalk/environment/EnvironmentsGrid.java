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
package org.exoplatform.ide.extension.aws.client.beanstalk.environment;

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

   private final String NAME = "Name";

   private final String SOLUTION_STACK = "Solution Stack";

   private final String URL = "URL";

   private final String APPLICATION = "Application";

   private final String VERSION = "Version";

   private final String STATUS = "Status";

   private final String TERMINATE = "Terminate";

   private final String REBUILD = "Rebuild";

   private Column<EnvironmentInfo, String> terminateColumn;

   private Column<EnvironmentInfo, String> rebuildColumn;

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

      Column<EnvironmentInfo, String> appNameColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return environmentInfo.getApplicationName();
         }
      };

      Column<EnvironmentInfo, String> appVersionColumn = new Column<EnvironmentInfo, String>(new TextCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
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

      Column<EnvironmentInfo, String> urlColumn = new Column<EnvironmentInfo, String>(new LinkCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return environmentInfo.getEndpointUrl();
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

      rebuildColumn = new Column<EnvironmentInfo, String>(new ButtonCell())
      {

         @Override
         public String getValue(EnvironmentInfo environmentInfo)
         {
            return REBUILD;
         }
      };

      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().addColumn(solutionStackColumn, SOLUTION_STACK);
      getCellTable().addColumn(appNameColumn, APPLICATION);
      getCellTable().addColumn(appVersionColumn, VERSION);
      getCellTable().addColumn(statusColumn, STATUS);
      getCellTable().addColumn(urlColumn, URL);
      getCellTable().setColumnWidth(urlColumn, "130px");
      getCellTable().addColumn(terminateColumn, TERMINATE);
      getCellTable().addColumn(rebuildColumn, REBUILD);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.HasEnvironmentActions#addTerminateHandler(com.google.gwt.event.logical.shared.SelectionHandler)
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

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environment.HasEnvironmentActions#addRebuildHandler(com.google.gwt.event.logical.shared.SelectionHandler)
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
      return "<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s
         + " target=\"_blank\">View Running Version</a><br>";
   }
}
