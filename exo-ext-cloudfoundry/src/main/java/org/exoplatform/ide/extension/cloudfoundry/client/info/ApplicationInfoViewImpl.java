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
package org.exoplatform.ide.extension.cloudfoundry.client.info;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationInfoViewImpl extends DialogBox implements ApplicationInfoView
{
   private static ApplicationInfoViewImplUiBinder uiBinder = GWT.create(ApplicationInfoViewImplUiBinder.class);

   @UiField
   Button btnOk;

   @UiField(provided = true)
   CellTable<String> urisTable = new CellTable<String>();

   @UiField(provided = true)
   CellTable<String> environmentsTable = new CellTable<String>();

   @UiField(provided = true)
   CellTable<String> servicesTable = new CellTable<String>();

   @UiField
   Label name;

   @UiField
   Label state;

   @UiField
   Label instances;

   @UiField
   Label version;

   @UiField
   Label resourceDisk;

   @UiField
   Label memory;

   @UiField
   Label model;

   @UiField
   Label stack;

   interface ApplicationInfoViewImplUiBinder extends UiBinder<Widget, ApplicationInfoViewImpl>
   {
   }

   private ApplicationInfoView.ActionDelegate delegate;

   @Inject
   public ApplicationInfoViewImpl()
   {
      Widget widget = uiBinder.createAndBindUi(this);

      createCellTable(urisTable, "URIs");
      createCellTable(servicesTable, "Services");
      createCellTable(environmentsTable, "Environments");

      this.setText("Application Info");
      this.setWidget(widget);

      //      setApplicationEnvironments(JsonCollections.<String> createArray());
      //      setApplicationServices(JsonCollections.<String> createArray());
      //      setApplicationUris(JsonCollections.<String> createArray());
   }

   private void createCellTable(CellTable<String> table, String header)
   {
      Column<String, String> column = new Column<String, String>(new TextCell())
      {
         @Override
         public String getValue(String object)
         {
            return object;
         }
      };

      table.addColumn(column, header);
      table.setColumnWidth(column, "100%");
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
   public void setName(String text)
   {
      name.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setState(String text)
   {
      state.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setInstances(String text)
   {
      instances.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setVersion(String text)
   {
      version.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDisk(String text)
   {
      resourceDisk.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setMemory(String text)
   {
      memory.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setStack(String text)
   {
      stack.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setModel(String text)
   {
      model.setText(text);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationUris(JsonArray<String> applications)
   {
      setItemsIntoCellTable(applications, urisTable);
   }

   private void setItemsIntoCellTable(JsonArray<String> items, CellTable<String> table)
   {
      List<String> list = new ArrayList<String>();
      for (int i = 0; i < items.size(); i++)
      {
         list.add(items.get(i));
      }

      table.setRowData(list);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationServices(JsonArray<String> services)
   {
      setItemsIntoCellTable(services, servicesTable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationEnvironments(JsonArray<String> environments)
   {
      setItemsIntoCellTable(environments, environmentsTable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showDialog()
   {
      this.center();
      this.show();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.hide();
   }

   @UiHandler("btnOk")
   void onBtnOkClick(ClickEvent event)
   {
      delegate.onOKClicked();
   }
}