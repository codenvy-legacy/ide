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
package com.codenvy.ide.preferences;

import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;

import com.codenvy.ide.Resources;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;


/**
 * PreferenceViewImpl is the view of preferences.
 * The view shows preference pages to the end user. It has an area at the bottom containing
 * OK, Apply and Close buttons, on the left hand side of page is list of available preferences,
 * on the right hand side of page is current preference page.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class PreferencesViewImpl extends DialogBox implements PreferencesView
{
   private static PreferenceViewImplUiBinder uiBinder = GWT.create(PreferenceViewImplUiBinder.class);

   @UiField
   Button btnClose;

   @UiField
   Button btnOk;

   @UiField
   Button btnApply;

   @UiField
   ScrollPanel preferences;

   @UiField
   SimplePanel contentPanel;

   private ActionDelegate delegate;

   private PreferencesPagePresenter firstPage;

   private SimpleList<PreferencesPagePresenter> list;

   private SimpleList.ListItemRenderer<PreferencesPagePresenter> listItemRenderer =
      new SimpleList.ListItemRenderer<PreferencesPagePresenter>()
      {
         @Override
         public void render(Element itemElement, PreferencesPagePresenter itemData)
         {
            TableCellElement label = Elements.createTDElement();

            SafeHtmlBuilder sb = new SafeHtmlBuilder();
            // Add icon
            sb.appendHtmlConstant("<table><tr><td>");
            ImageResource icon = itemData.getIcon();
            if (icon != null)
            {
               sb.appendHtmlConstant("<img src=\"" + icon.getSafeUri().asString() + "\">");
            }
            sb.appendHtmlConstant("</td>");

            // Add title
            sb.appendHtmlConstant("<td>");
            sb.appendEscaped(itemData.getTitle());
            sb.appendHtmlConstant("</td></tr></table>");

            label.setInnerHTML(sb.toSafeHtml().asString());

            itemElement.appendChild(label);
         }

         @Override
         public Element createElement()
         {
            return Elements.createTRElement();
         }
      };

   private SimpleList.ListEventDelegate<PreferencesPagePresenter> listDelegate =
      new SimpleList.ListEventDelegate<PreferencesPagePresenter>()
      {
         public void onListItemClicked(Element itemElement, PreferencesPagePresenter itemData)
         {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedPreference(itemData);
         }

         public void onListItemDoubleClicked(Element listItemBase, PreferencesPagePresenter itemData)
         {
         }
      };

   interface PreferenceViewImplUiBinder extends UiBinder<Widget, PreferencesViewImpl>
   {
   }

   /**
    * Create view.
    * 
    * @param resources
    * @param preferences
    */
   public PreferencesViewImpl(Resources resources, JsonArray<PreferencesPagePresenter> preferences)
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Preferences");
      //adds widget into DialogBox
      this.setWidget(widget);

      //create list of preferences
      TableElement tableElement = Elements.createTableElement();
      tableElement.setAttribute("style", "width: 100%");
      list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

      this.preferences.setStyleName(resources.coreCss().simpleListContainer());
      this.preferences.add(list);

      list.render(preferences);

      if (preferences.size() > 0)
      {
         firstPage = preferences.get(0);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
      
      //show first page if page is exist
      if (firstPage != null)
      {
         listDelegate.onListItemClicked(null, firstPage);
      }
      else
      {
         btnApply.setEnabled(false);
      }
   }

   @UiHandler("btnApply")
   void onBtnApplyClick(ClickEvent event)
   {
      delegate.onApplyClicked();
   }

   @UiHandler("btnOk")
   void onBtnOkClick(ClickEvent event)
   {
      delegate.onOkClicked();
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
      this.hide();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showPreferences()
   {
      this.center();
      this.show();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AcceptsOneWidget getContentPanel()
   {
      return contentPanel;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplyButtonEnabled(boolean isEnabled)
   {
      btnApply.setEnabled(isEnabled);
   }
}