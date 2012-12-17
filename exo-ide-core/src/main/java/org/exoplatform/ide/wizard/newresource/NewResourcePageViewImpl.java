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
package org.exoplatform.ide.wizard.newresource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.ui.list.SimpleList;
import org.exoplatform.ide.ui.list.SimpleList.View;
import org.exoplatform.ide.util.dom.Elements;

/**
 * NewResourcePageViewImpl is the view of NewResource wizard.
 * Provides selecting type of resource for creating new resource.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewResourcePageViewImpl implements NewResourcePageView
{
   private static NewFileViewUiBinder uiBinder = GWT.create(NewFileViewUiBinder.class);

   @UiField
   ScrollPanel resources;

   private ActionDelegate delegate;

   private final Widget widget;

   private SimpleList<NewResourceWizardData> list;

   private SimpleList.ListItemRenderer<NewResourceWizardData> listItemRenderer =
      new SimpleList.ListItemRenderer<NewResourceWizardData>()
      {
         @Override
         public void render(Element itemElement, NewResourceWizardData itemData)
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

   private SimpleList.ListEventDelegate<NewResourceWizardData> listDelegate =
      new SimpleList.ListEventDelegate<NewResourceWizardData>()
      {
         public void onListItemClicked(Element itemElement, NewResourceWizardData itemData)
         {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedFileType(itemData);
         }

         public void onListItemDoubleClicked(Element listItemBase, NewResourceWizardData itemData)
         {
         }
      };


   interface NewFileViewUiBinder extends UiBinder<Widget, NewResourcePageViewImpl>
   {
   }

   /**
    * Create view with given list of new resource wizards.
    * 
    * @param wizards available wizards
    */
   public NewResourcePageViewImpl(Resources resources, JsonArray<NewResourceWizardData> wizards)
   {
      widget = uiBinder.createAndBindUi(this);

      TableElement tableElement = Elements.createTableElement();
      tableElement.setAttribute("style", "width: 100%");
      list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

      this.resources.getElement().appendChild((Node)list.getView().getElement());

      list.render(wizards);
   }

   /**
    * {@inheritDoc}
    */
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   public Widget asWidget()
   {
      return widget;
   }

   /**
    * {@inheritDoc}
    */
   public ActionDelegate getDelegate()
   {
      return delegate;
   }

   /**
    * {@inheritDoc}
    */
   public Element getElement()
   {
      // TODO Auto-generated method stub
      return null;
   }
}