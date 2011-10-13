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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 18, 2011 evgen $
 *
 */
public class ApplicationsListGrid extends ListGrid<CloudfoundryApplication> implements HasApplicationsActions
{
   private Column<CloudfoundryApplication, String> nameColumn;

   private Column<CloudfoundryApplication, Number> instancesColumn;

   private Column<CloudfoundryApplication, String> stateColumn;

   private Column<CloudfoundryApplication, List<String>> urlColumn;

   private Column<CloudfoundryApplication, String> servicesColumn;

   private Column<CloudfoundryApplication, String> startColumn;

   private Column<CloudfoundryApplication, String> stopColumn;

   private Column<CloudfoundryApplication, String> restartColumn;

   private Column<CloudfoundryApplication, String> deleteColumn;

   /**
    * 
    */
   public ApplicationsListGrid()
   {
      setID("applicationsListGrid");
      
      TextCell textCell = new TextCell();
      nameColumn = new Column<CloudfoundryApplication, String>(textCell)
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            return object.getName();
         }
      };

      instancesColumn = new Column<CloudfoundryApplication, Number>(new NumberCell())
      {

         @Override
         public Integer getValue(CloudfoundryApplication object)
         {
            return object.getInstances();
         }
      };

      stateColumn = new Column<CloudfoundryApplication, String>(new TextCell())
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            return object.getState();
         }
      };

      urlColumn = new Column<CloudfoundryApplication, List<String>>(new ListLink())
      {

         @Override
         public List<String> getValue(CloudfoundryApplication object)
         {
            return object.getUris();
         }
      };

      servicesColumn = new Column<CloudfoundryApplication, String>(new TextCell())
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            StringBuilder b = new StringBuilder();
            for (String s : object.getServices())
            {
               b.append(s).append(";");
            }
            return b.toString();
         }
      };

      startColumn = new Column<CloudfoundryApplication, String>(new ButtonCell())
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            return "Start";
         }
      };

      stopColumn = new Column<CloudfoundryApplication, String>(new ButtonCell())
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            return "Stop";
         }
      };

      restartColumn = new Column<CloudfoundryApplication, String>(new ButtonCell())
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            return "Restart";
         }
      };

      deleteColumn = new Column<CloudfoundryApplication, String>(new ButtonCell())
      {

         @Override
         public String getValue(CloudfoundryApplication object)
         {
            return "Delete";
         }
      };

      getCellTable().addColumn(nameColumn, "Application");
      getCellTable().addColumn(instancesColumn, "#");
      getCellTable().addColumn(stateColumn, "Health");
      getCellTable().addColumn(urlColumn, "URLS");
      getCellTable().addColumn(servicesColumn, "Services");
      getCellTable().addColumn(startColumn, "Start");
      getCellTable().addColumn(stopColumn, "Stop");
      getCellTable().addColumn(restartColumn, "Restart");
      getCellTable().addColumn(deleteColumn, "Delete");
   }

   private class ListLink extends AbstractSafeHtmlCell<List<String>>
   {

      /**
       * 
       */
      public ListLink()
      {
         super(new SafeHtmlListRenderer(), "");
      }

      /**
       * @see com.google.gwt.cell.client.AbstractSafeHtmlCell#render(com.google.gwt.cell.client.Cell.Context, com.google.gwt.safehtml.shared.SafeHtml, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb)
      {
         sb.append(data);
      }

   }

   private class SafeHtmlListRenderer implements SafeHtmlRenderer<List<String>>
   {

      /**
       * @see com.google.gwt.text.shared.SafeHtmlRenderer#render(java.lang.Object)
       */
      @Override
      public SafeHtml render(List<String> object)
      {
         String string = createLinks(object);
         return new SafeHtmlBuilder().appendHtmlConstant(string).toSafeHtml();
      }

      /**
       * @see com.google.gwt.text.shared.SafeHtmlRenderer#render(java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      public void render(List<String> object, SafeHtmlBuilder builder)
      {
         String string = createLinks(object);
         builder.appendHtmlConstant(string);
      }

      /**
       * @param object
       * @return
       */
      private String createLinks(List<String> object)
      {
         StringBuilder b = new StringBuilder();
         for (String s : object)
         {
            b.append("<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s + " target=\"_blank\">" + s + "</a>")
               .append("<br>");
         }
         String string = b.toString();
         if (string.endsWith("<br>"))
            string = string.substring(0, string.lastIndexOf("<br>"));
         return string;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addStartApplicationHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addStartApplicationHandler(final SelectionHandler<CloudfoundryApplication> handler)
   {
      startColumn.setFieldUpdater(new FieldUpdater<CloudfoundryApplication, String>()
      {

         @Override
         public void update(int index, CloudfoundryApplication object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addStopApplicationHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addStopApplicationHandler(final SelectionHandler<CloudfoundryApplication> handler)
   {
      stopColumn.setFieldUpdater(new FieldUpdater<CloudfoundryApplication, String>()
      {

         @Override
         public void update(int index, CloudfoundryApplication object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addRestartApplicationHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addRestartApplicationHandler(final SelectionHandler<CloudfoundryApplication> handler)
   {
      restartColumn.setFieldUpdater(new FieldUpdater<CloudfoundryApplication, String>()
      {

         @Override
         public void update(int index, CloudfoundryApplication object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.HasApplicationsActions#addDeleteApplicationHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addDeleteApplicationHandler(final SelectionHandler<CloudfoundryApplication> handler)
   {
      deleteColumn.setFieldUpdater(new FieldUpdater<CloudfoundryApplication, String>()
      {

         @Override
         public void update(int index, CloudfoundryApplication object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }

   private class SelectionEventImpl extends SelectionEvent<CloudfoundryApplication>
   {
      /**
       * @param selectedItem
       */
      protected SelectionEventImpl(CloudfoundryApplication selectedItem)
      {
         super(selectedItem);
      }

   }

}
