/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.apps;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link ApplicationsView}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationsViewImpl extends DialogBox implements ApplicationsView
{
   private class ListLink extends AbstractSafeHtmlCell<List<String>>
   {
      /**
       * Create Link list.
       */
      public ListLink()
      {
         super(new SafeHtmlListRenderer());
      }

      /**
       * {@inheritDoc}
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
       * {@inheritDoc}
       */
      @Override
      public SafeHtml render(List<String> object)
      {
         String string = createLinks(object);
         return new SafeHtmlBuilder().appendHtmlConstant(string).toSafeHtml();
      }

      /**
       * {@inheritDoc}
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
            b.append(
               "<a style=\"cursor: pointer; color:#2039f8\" href=http://" + s + " target=\"_blank\">" + s + "</a>")
               .append("<br>");
         }

         String string = b.toString();
         if (string.endsWith("<br>"))
         {
            string = string.substring(0, string.lastIndexOf("<br>"));
         }

         return string;
      }
   }

   private static ApplicationsViewImplUiBinder uiBinder = GWT.create(ApplicationsViewImplUiBinder.class);

   @UiField
   Button btnClose;

   @UiField
   Button btnShow;

   @UiField(provided = true)
   CellTable<CloudFoundryApplication> appsTable = new CellTable<CloudFoundryApplication>();

   @UiField
   ListBox server;

   interface ApplicationsViewImplUiBinder extends UiBinder<Widget, ApplicationsViewImpl>
   {
   }

   private ActionDelegate delegate;

   private boolean isDisplayed;

   /**
    * Create view.
    * 
    * @param resources
    * @param constant
    */
   @Inject
   protected ApplicationsViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant)
   {
      createAppsTable();

      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Applications");
      this.setWidget(widget);
      
      btnShow.setHTML(new Image(resources.okButton()) + " " + constant.showButton());
      btnClose.setHTML(new Image(resources.cancelButton()) + " " + constant.closeButton());
   }

   /**
    * Creates table what contains list of available applications.
    */
   private void createAppsTable()
   {
      Column<CloudFoundryApplication, String> nameColumn = new Column<CloudFoundryApplication, String>(new TextCell())
      {
         @Override
         public String getValue(CloudFoundryApplication object)
         {
            return object.getName();
         }
      };

      Column<CloudFoundryApplication, Number> instancesColumn =
         new Column<CloudFoundryApplication, Number>(new NumberCell())
         {
            @Override
            public Integer getValue(CloudFoundryApplication object)
            {
               return object.getInstances();
            }
         };

      Column<CloudFoundryApplication, String> stateColumn = new Column<CloudFoundryApplication, String>(new TextCell())
      {
         @Override
         public String getValue(CloudFoundryApplication object)
         {
            return object.getState();
         }
      };

      Column<CloudFoundryApplication, List<String>> urlColumn =
         new Column<CloudFoundryApplication, List<String>>(new ListLink())
         {
            @Override
            public List<String> getValue(CloudFoundryApplication object)
            {
               return object.getUris();
            }
         };

      Column<CloudFoundryApplication, String> servicesColumn =
         new Column<CloudFoundryApplication, String>(new TextCell())
         {
            @Override
            public String getValue(CloudFoundryApplication object)
            {
               StringBuilder b = new StringBuilder();
               for (String s : object.getServices())
               {
                  b.append(s).append(";");
               }
               return b.toString();
            }
         };

      Column<CloudFoundryApplication, String> startColumn =
         new Column<CloudFoundryApplication, String>(new ButtonCell())
         {

            @Override
            public String getValue(CloudFoundryApplication object)
            {
               return "Start";
            }
         };

      startColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>()
      {
         @Override
         public void update(int index, CloudFoundryApplication object, String value)
         {
            delegate.onStartClicked(object);
         }
      });


      Column<CloudFoundryApplication, String> stopColumn =
         new Column<CloudFoundryApplication, String>(new ButtonCell())
         {

            @Override
            public String getValue(CloudFoundryApplication object)
            {
               return "Stop";
            }
         };

      stopColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>()
      {
         @Override
         public void update(int index, CloudFoundryApplication object, String value)
         {
            delegate.onStopClicked(object);
         }
      });

      Column<CloudFoundryApplication, String> restartColumn =
         new Column<CloudFoundryApplication, String>(new ButtonCell())
         {

            @Override
            public String getValue(CloudFoundryApplication object)
            {
               return "Restart";
            }
         };

      restartColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>()
      {
         @Override
         public void update(int index, CloudFoundryApplication object, String value)
         {
            delegate.onRestartClicked(object);
         }
      });

      Column<CloudFoundryApplication, String> deleteColumn =
         new Column<CloudFoundryApplication, String>(new ButtonCell())
         {
            @Override
            public String getValue(CloudFoundryApplication object)
            {
               return "Delete";
            }
         };

      deleteColumn.setFieldUpdater(new FieldUpdater<CloudFoundryApplication, String>()
      {
         @Override
         public void update(int index, CloudFoundryApplication object, String value)
         {
            delegate.onDeleteClicked(object);
         }
      });

      appsTable.addColumn(nameColumn, "Application");
      appsTable.addColumn(instancesColumn, "#");
      appsTable.setColumnWidth(instancesColumn, "8px");
      appsTable.addColumn(stateColumn, "Health");
      appsTable.setColumnWidth(stateColumn, "50px");
      appsTable.addColumn(urlColumn, "URLS");
      appsTable.addColumn(servicesColumn, "Services");
      appsTable.setColumnWidth(servicesColumn, "60px");

      appsTable.addColumn(startColumn, "Start");
      appsTable.setColumnWidth(startColumn, "60px");
      appsTable.addColumn(stopColumn, "Stop");
      appsTable.setColumnWidth(stopColumn, "60px");
      appsTable.addColumn(restartColumn, "Restart");
      appsTable.setColumnWidth(restartColumn, "60px");
      appsTable.addColumn(deleteColumn, "Delete");
      appsTable.setColumnWidth(deleteColumn, "60px");

      // don't show loading indicator
      appsTable.setLoadingIndicator(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplications(JsonArray<CloudFoundryApplication> apps)
   {
      List<CloudFoundryApplication> appList = new ArrayList<CloudFoundryApplication>();
      for (int i = 0; i < apps.size(); i++)
      {
         appList.add(apps.get(i));
      }

      appsTable.setRowData(appList);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getServer()
   {
      int serverIndex = server.getSelectedIndex();
      return serverIndex != -1 ? server.getItemText(serverIndex) : "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServer(String server)
   {
      int count = this.server.getItemCount();
      boolean isItemFound = false;

      int i = 0;
      while (i < count && !isItemFound)
      {
         String item = this.server.getItemText(i);
         isItemFound = item.equals(server);

         i++;
      }

      if (isItemFound)
      {
         this.server.setSelectedIndex(i - 1);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServers(JsonArray<String> servers)
   {
      server.clear();
      for (int i = 0; i < servers.size(); i++)
      {
         server.addItem(servers.get(i));
      }
   }

   @UiHandler("btnShow")
   void onBtnShowClick(ClickEvent event)
   {
      delegate.onShowClicked();
   }

   @UiHandler("btnClose")
   void onBtnCloseClick(ClickEvent event)
   {
      delegate.onCloseClicked();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.isDisplayed = false;
      this.hide();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showDialog()
   {
      this.isDisplayed = true;
      this.center();
      this.show();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDisplayed()
   {
      return isDisplayed;
   }
}