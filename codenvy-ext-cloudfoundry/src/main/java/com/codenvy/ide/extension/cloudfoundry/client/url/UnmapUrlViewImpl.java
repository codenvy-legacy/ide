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
package com.codenvy.ide.extension.cloudfoundry.client.url;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link UnmapUrlView}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UnmapUrlViewImpl extends DialogBox implements UnmapUrlView
{
   private static UnmapUrlViewImplUiBinder uiBinder = GWT.create(UnmapUrlViewImplUiBinder.class);

   @UiField(provided = true)
   CellTable<String> urlsTable = new CellTable<String>();

   @UiField
   Button btnClose;

   @UiField
   Button btnMap;

   @UiField
   TextBox mapUrl;

   interface UnmapUrlViewImplUiBinder extends UiBinder<Widget, UnmapUrlViewImpl>
   {
   }

   private UnmapUrlView.ActionDelegate delegate;

   private CloudFoundryLocalizationConstant constant;

   /**
    * Create view.
    * 
    * @param constatns
    * @param resources
    * @param constant
    */
   @Inject
   protected UnmapUrlViewImpl(CloudFoundryLocalizationConstant constatns, CloudFoundryResources resources,
      CloudFoundryLocalizationConstant constant)
   {
      this.constant = constant;

      createUrlsTable();

      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Application URLs");
      this.setWidget(widget);

      btnMap.setHTML(new Image(resources.addButton()) + " " + constatns.mapButton());
      btnClose.setHTML(new Image(resources.cancelButton()) + " " + constatns.closeButton());
   }

   /**
    * Creates urls table.
    */
   private void createUrlsTable()
   {
      Column<String, String> buttonColumn = new Column<String, String>(new ButtonCell())
      {
         @Override
         public String getValue(String object)
         {
            return constant.unmapButton();
         }
      };

      buttonColumn.setFieldUpdater(new FieldUpdater<String, String>()
      {
         @Override
         public void update(int index, String object, String value)
         {
            delegate.onUnMapUrlClicked(object);
         }
      });

      Column<String, SafeHtml> valueColumn = new Column<String, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final String url)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return "<a target=\"_blank\" href=\"http://" + url + "\">" + url + "</a>";
               }
            };
            return html;
         }
      };

      urlsTable.addColumn(valueColumn, constant.applicationUnmapUrlGridUrlField());
      urlsTable.setColumnWidth(valueColumn, "75%");
      urlsTable.addColumn(buttonColumn, constant.unmapUrlListGridColumnTitle());
      urlsTable.setColumnWidth(buttonColumn, "25%");

      // don't show loading indicator
      urlsTable.setLoadingIndicator(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getMapUrl()
   {
      return mapUrl.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setMapUrl(String url)
   {
      mapUrl.setText(url);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setRegisteredUrls(JsonArray<String> urls)
   {
      List<String> urlsList = new ArrayList<String>();
      for (int i = 0; i < urls.size(); i++)
      {
         urlsList.add(urls.get(i));
      }
      urlsTable.setRowData(urlsList);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEnableMapUrlButton(boolean enable)
   {
      btnMap.setEnabled(enable);
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   @UiHandler("btnClose")
   void onBtnCloseClick(ClickEvent event)
   {
      delegate.onCloseClicked();
   }

   @UiHandler("btnMap")
   void onBtnMapClick(ClickEvent event)
   {
      delegate.onMapUrlClicked();
   }

   @UiHandler("mapUrl")
   void onMapUrlKeyUp(KeyUpEvent event)
   {
      delegate.onMapUrlChanged();
   }
}