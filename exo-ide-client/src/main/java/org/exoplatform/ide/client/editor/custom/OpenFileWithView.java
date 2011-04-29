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
package org.exoplatform.ide.client.editor.custom;

import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenFileWithView extends ViewImpl implements
   org.exoplatform.ide.client.editor.custom.OpenFileWithPresenter.Display
{

   private static OpenFileWithViewUiBinder uiBinder = GWT.create(OpenFileWithViewUiBinder.class);

   interface OpenFileWithViewUiBinder extends UiBinder<Widget, OpenFileWithView>
   {
   }

   /**
    * Initial width of this view
    */
   private static int INITIAL_WIDTH = 500;

   /**
    * Initial height of this view
    */
   private static int INITIAL_HEIGHT = 280;

   @UiField
   EditorsListGrid editorsListGrid;

   @UiField
   CheckboxItem isDefaultCheckItem;

   @UiField
   IButton openButton;

   @UiField
   IButton cancelButton;

   public OpenFileWithView()
   {
      super(ID, "popup", "Open File With", new Image(IDEImageBundle.INSTANCE.restServicesDiscovery()), INITIAL_WIDTH,
         INITIAL_HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public EditorsListGrid getEditorsListGrid()
   {
      return editorsListGrid;
   }

   @Override
   public HasValue<Boolean> getIsDefaultCheckItem()
   {
      return isDefaultCheckItem;
   }

   @Override
   public HasClickHandlers getOpenButton()
   {
      return openButton;
   }

   @Override
   public void setOpenButtonEnabled(boolean enabled)
   {
      openButton.setEnabled(enabled);
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setSelectedItem(EditorInfo item)
   {
      editorsListGrid.getCellTable().getSelectionModel().setSelected(item, true);
   }

}
