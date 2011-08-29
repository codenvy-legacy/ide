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
package org.exoplatform.ide.extension.groovy.client.classpath.ui;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.groovy.client.GroovyExtension;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * Form for choosing sources for class path.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class ChooseSourcePathView extends ViewImpl implements ChooseSourcePathPresenter.Display
{
   
   private static ChooseSourcePathViewUiBinder uiBinder = GWT.create(ChooseSourcePathViewUiBinder.class);
   
   interface ChooseSourcePathViewUiBinder extends UiBinder<Widget, ChooseSourcePathView>
   {
   }

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;
   
   /**
    * Confirm button.
    */
   @UiField
   ImageButton okButton;

   /**
    * Tree for displaying items.
    */
   @UiField
   ItemTreeGrid<Item> pathTreeGrid;
   
   
   public static final int WIDTH = 450;

   public static final int HEIGHT = 400;

   //IDs for Selenium tests:

   public static final String ID = "ideChooseSourcePathView";
   
   private final String ID_CANCEL_BUTTON = "ideChooseSourcePathViewCancelButton";

   private final String ID_OK_BUTTON = "ideChooseSourcePathViewOkButton";

   private final String ID_TREE_GRID = "ideChooseSourcePathViewTreeGrid";

   public ChooseSourcePathView()
   {
      super(ID, ViewType.MODAL, GroovyExtension.LOCALIZATION_CONSTANT.chooseSourcePathViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      pathTreeGrid.getElement().setId(ID_TREE_GRID);
      okButton.setButtonId(ID_OK_BUTTON );     
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getOkButton()
    */
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getItemsTree()
    */
   public TreeGridItem<Item> getItemsTree()
   {
      return pathTreeGrid;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#getSelectedItems()
    */
   public List<Item> getSelectedItems()
   {
      return pathTreeGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ChooseSourcePathPresenter.Display#enableOkButtonState(boolean)
    */
   public void enableOkButtonState(boolean isEnabled)
   {
      okButton.setEnabled(isEnabled);
   }

}
