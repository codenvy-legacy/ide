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
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
   
   public static final String MSG_LABEL_ID = "ideCustomizeHotKeysMessageLabel";
   
   private static final String TITLE = IDE.PREFERENCES_CONSTANT.customizeHotkeysTitle();
   
   /**
    * Initial width of this view
    */
   private static int WIDTH = 600;

   /**
    * Initial height of this view
    */
   private static int HEIGHT = 300;   

   private static CustomizeHotKeysViewUiBinder uiBinder = GWT.create(CustomizeHotKeysViewUiBinder.class);

   interface CustomizeHotKeysViewUiBinder extends UiBinder<Widget, CustomizeHotKeysView>
   {
   }
   
   @UiField
   ImageButton bindButton;
   
   @UiField
   ImageButton unbindButton;   

   @UiField
   ImageButton okButton;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   HotKeyItemListGrid hotKeyItemListGrid;
   
   @UiField
   TextField hotKeyField;
   
   @UiField
   Label messageLabel;

   public CustomizeHotKeysView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.customizeHotKeys()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      messageLabel.getElement().setId(MSG_LABEL_ID);
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public HasClickHandlers getBindButton()
   {
      return bindButton;
   }

   @Override
   public HasClickHandlers getUnbindButton()
   {
      return unbindButton;
   }

   @Override
   public ListGridItem<HotKeyItem> getHotKeyItemListGrid()
   {
      return hotKeyItemListGrid;
   }

   @Override
   public HasValue<String> getHotKeyField()
   {
      return hotKeyField;
   }

   @Override
   public HotKeyItem getSelectedItem()
   {
      return hotKeyItemListGrid.getSelectedItems().get(0);
   }

   @Override
   public void setOkButtonEnabled(boolean enabled)
   {
      okButton.setEnabled(enabled);
   }

   @Override
   public void setBindButtonEnabled(boolean enabled)
   {
      bindButton.setEnabled(enabled);
   }

   @Override
   public void setUnbindButtonEnabled(boolean enabled)
   {
      unbindButton.setEnabled(enabled);
   }

   @Override
   public void setHotKeyFieldEnabled(boolean enabled)
   {
      hotKeyField.setEnabled(enabled);
   }

   @Override
   public void focusOnHotKeyField()
   {
      hotKeyField.focusInItem();
   }

   @Override
   public void showError(String text)
   {
      messageLabel.setText(text);
   }

}
