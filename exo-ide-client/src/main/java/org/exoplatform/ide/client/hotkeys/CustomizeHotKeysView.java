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
package org.exoplatform.ide.client.hotkeys;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CustomizeHotKeysView extends ViewImpl implements org.exoplatform.ide.client.hotkeys.CustomizeHotKeysPresenter.Display
{
   
   public static final String ID = "ideCustomizeHotKeysView";
   
   /**
    * Initial width of this view
    */
   private static int WIDTH = 550;

   /**
    * Initial height of this view
    */
   private static int HEIGHT = 350;   

   private static CustomizeHotKeysViewUiBinder uiBinder = GWT.create(CustomizeHotKeysViewUiBinder.class);

   interface CustomizeHotKeysViewUiBinder extends UiBinder<Widget, CustomizeHotKeysView>
   {
   }

   public CustomizeHotKeysView()
   {
      super(ID, "popup", "Customize hotkeys", new Image(IDEImageBundle.INSTANCE.customizeHotKeys()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasClickHandlers getSaveButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasClickHandlers getBindButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasClickHandlers getUnbindButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListGridItem<HotKeyItem> getHotKeyItemListGrid()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasValue<String> getHotKeyField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HotKeyItem getSelectedItem()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setSaveButtonEnabled(boolean enabled)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setBindButtonEnabled(boolean enabled)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setUnbindButtonEnabled(boolean enabled)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setHotKeyFieldEnabled(boolean enabled)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void focusOnHotKeyField()
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void showError(String style, String text)
   {
      // TODO Auto-generated method stub
      
   }

}
