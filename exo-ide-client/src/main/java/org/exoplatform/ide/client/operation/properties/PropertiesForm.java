/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.properties;

import java.util.ArrayList;
import java.util.Collection;

import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.gwtframework.ui.client.component.Align;
import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.PropertyTitle;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesForm extends ViewImpl implements PropertiesPresenter.Display
{
   public final static String ID = "idePropertiesView";

   public final static String FORM_ID = "ideDynamicPropertiesForm";

   private Widget content;

   private PropertiesPresenter presenter;

   private Image image;

   public PropertiesForm()
   {
      super(ID, ViewType.OPERATION, "Properties");
      image = new Image(IDEImageBundle.INSTANCE.properties());
      setWidth("100%");
      setHeight("100%");
      presenter = new PropertiesPresenter();
      presenter.bindDisplay(this);

   }

   public void refreshProperties(File file)
   {
      //       clear(); //bug with many refresh, content not shown! 

      if (content != null)
      {
//         if (hasMember(content))
//            removeMember(content);
//         //         content.hide();
//         //         content.removeFromParent();
//         content.destroy();
         remove(content);
      }

      if (file.getProperties().size() == 0)
      {
         content = new com.google.gwt.user.client.ui.Label("There are no properties for this file.");
         content.setWidth("100%");
         content.setHeight("100$");
//         content.setAlign(Alignment.CENTER);
      }
      else
      {
         content = getPropertiesForm(file.getProperties());
//         content.setID(FORM_ID);
//         content.setPadding(10);
         //         content.setTitleWidth(200);
//         content.setLayoutAlign(VerticalAlignment.TOP);
//         content.setLayoutAlign(Alignment.LEFT);

      }
      add(content);
   }

   public VerticalPanel getPropertiesForm(Collection<Property> properties)
   {
      VerticalPanel propertiesForm = new VerticalPanel();
      propertiesForm.setWidth("100%");
      propertiesForm.setHeight("100%");
      //      propertiesForm.setCanFocus(true);
      if (properties == null)
      {
         return propertiesForm;
      }

      ArrayList<Widget> formItems = new ArrayList<Widget>();

      for (Property property : properties)
      {
         QName propertyName = property.getName();

         if (ItemProperty.JCR_CONTENT.equals(propertyName))
         {
            for (Property p : property.getChildProperties())
            {
               if (!PropertyTitle.containsTitleFor(p.getName()))
               {
                  continue;
               }

               String propertyTitle = PropertyTitle.getPropertyTitle(p.getName());
               formItems.add(getStaticTextItem(propertyTitle, p.getValue()));
            }

         }
         else
         {
            if (!PropertyTitle.containsTitleFor(propertyName))
            {
               continue;
            }

            String propertyTitle = PropertyTitle.getPropertyTitle(propertyName);
            formItems.add(getStaticTextItem(propertyTitle, property.getValue()));
         }
      }

      //      Collections.sort(formItems, itemsComparator);
      for (Widget w : formItems)
         propertiesForm.add(w);

      return propertiesForm;
   }

   protected CheckboxItem getBooleanItem(String name, boolean value, boolean isReadOnly)
   {
      CheckboxItem booleanItem = new CheckboxItem();
      booleanItem.setTitle("<b>" + name + "</b>");
      booleanItem.setValue(value);
      booleanItem.setEnabled(!isReadOnly);
      booleanItem.setLabelAsTitle(true);
      return booleanItem;
   }

   protected TextField getStaticTextItem(String name, String value)
   {
      TextField staticTextItem = new TextField();
      staticTextItem.setName("idePropertiesText" + name.replaceAll(" ", ""));
      //      staticTextItem.setWrapTitle(false);
      staticTextItem.setTitle("<b>" + name + "</b>");
      staticTextItem.setValue(value);
      staticTextItem.setTitleAlign(Align.RIGHT);

      //      staticTextItem.setWrap(false);

      return staticTextItem;
   }

   //   protected static Comparator<FormItem> itemsComparator = new Comparator<FormItem>()
   //   {
   //      public int compare(FormItem item1, FormItem item2)
   //      {
   //         return item1.getTitle().compareToIgnoreCase(item2.getTitle());
   //      }
   //   };

   @Override
   public String getTitle()
   {
      return "Properties";
   }

   public String getId()
   {
      return "Properties";
   }

   /**
    * @return the image
    */
   public Image getImage()
   {
      return image;
   }

   //   @Override
   //   public void destroy()
   //   {
   //      presenter.destroy();
   //      super.destroy();
   //   }

}
