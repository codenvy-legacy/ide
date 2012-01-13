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
package org.exoplatform.ide.extension.cloudbees.client.list.ui;

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
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfo;
import org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 22, 2011 evgen $
 * 
 */
public class ApplicationListGrid extends ListGrid<ApplicationInfo> implements HasApplicationListActions
{

   private Column<ApplicationInfo, String> nameColumn;

   private Column<ApplicationInfo, String> statusColumn;

   private Column<ApplicationInfo, String> urlColumn;

   private Column<ApplicationInfo, String> instanceColumn;

   private Column<ApplicationInfo, String> infoColumn;

   private Column<ApplicationInfo, String> deleteColumn;

   /**
    * 
    */
   public ApplicationListGrid()
   {
      nameColumn = new Column<ApplicationInfo, String>(new TextCell())
      {

         @Override
         public String getValue(ApplicationInfo object)
         {
            return object.getId();
         }
      };

      statusColumn = new Column<ApplicationInfo, String>(new TextCell())
      {

         @Override
         public String getValue(ApplicationInfo object)
         {
            return object.getStatus();
         }
      };

      urlColumn = new Column<ApplicationInfo, String>(new TextCell(new SafeHtmlRenderer<String>()
      {

         @Override
         public void render(String object, SafeHtmlBuilder builder)
         {
            builder.appendHtmlConstant(createLink(object));
         }

         @Override
         public SafeHtml render(String object)
         {
            return new SafeHtmlBuilder().appendHtmlConstant(createLink(object)).toSafeHtml();
         }

         private String createLink(String s)
         {
            return "<a style=\"cursor: pointer; color:#2039f8\" href=" + s + " target=\"_blank\">" + s + "</a>";
         }
      }))
      {

         @Override
         public String getValue(ApplicationInfo object)
         {
            return object.getUrl();
         }
      };

      instanceColumn = new Column<ApplicationInfo, String>(new TextCell())
      {

         @Override
         public String getValue(ApplicationInfo object)
         {
            return object.getInstances();
         }
      };

      deleteColumn = new Column<ApplicationInfo, String>(new ButtonCell())
      {

         @Override
         public String getValue(ApplicationInfo object)
         {
            return CloudBeesExtension.LOCALIZATION_CONSTANT.appListDelete();
         }
      };

      infoColumn = new Column<ApplicationInfo, String>(new ButtonCell())
      {

         @Override
         public String getValue(ApplicationInfo object)
         {
            return CloudBeesExtension.LOCALIZATION_CONSTANT.appListInfo();
         }
      };

      getCellTable().addColumn(nameColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListName());
      getCellTable().addColumn(statusColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListStatus());
      getCellTable().addColumn(urlColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListUrl());
      getCellTable().addColumn(instanceColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListInstance());
      getCellTable().addColumn(infoColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListInfo());
      getCellTable().addColumn(deleteColumn, CloudBeesExtension.LOCALIZATION_CONSTANT.appListDelete());
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions#addDeleteHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addDeleteHandler(final SelectionHandler<ApplicationInfo> handler)
   {
      deleteColumn.setFieldUpdater(new FieldUpdater<ApplicationInfo, String>()
      {

         @Override
         public void update(int index, ApplicationInfo object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions#addInfoHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addInfoHandler(final SelectionHandler<ApplicationInfo> handler)
   {
      infoColumn.setFieldUpdater(new FieldUpdater<ApplicationInfo, String>()
      {

         @Override
         public void update(int index, ApplicationInfo object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }

   private class SelectionEventImpl extends SelectionEvent<ApplicationInfo>
   {
      /**
       * @param selectedItem
       */
      protected SelectionEventImpl(ApplicationInfo selectedItem)
      {
         super(selectedItem);
      }

   }
}
