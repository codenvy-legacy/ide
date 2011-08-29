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

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.groovy.client.GroovyExtension;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * Form for configuring classpath file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 6, 2011 $
 *
 */
public class ConfigureBuildPathView extends ViewImpl implements ConfigureBuildPathPresenter.Display
{
   private static ConfigureBuildPathViewUiBinder uiBinder = GWT.create(ConfigureBuildPathViewUiBinder.class);
   
   interface ConfigureBuildPathViewUiBinder extends UiBinder<Widget, ConfigureBuildPathView>
   {
   }

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * Save button.
    */
   @UiField
   ImageButton saveButton;

   /**
    * Add source button.
    */
   @UiField
   ImageButton addButton;

   /**
    * Remove source button.
    */
   @UiField
   ImageButton removeButton;

   @UiField
   ClassPathEntryListGrid classPathEntryListGrid;

   
   public static final String CLASS_PATH_ENTRY_LIST_GRID_WIDTH = "100%";

   public static final int WIDTH = 600;

   public static final int HEIGHT = 335;

   
   //IDs for Selenium tests:
   
   public static final String ID = "ideConfigureBuildPathForm";

   private final String ID_CANCEL_BUTTON = "ideConfigureBuildPathFormCancelButton";

   private final String ID_SAVE_BUTTON = "ideConfigureBuildPathFormSaveButton";

   private final String ID_ADD_BUTTON = "ideConfigureBuildPathFormAddButton";

   private final String ID_REMOVE_BUTTON = "ideConfigureBuildPathFormRemoveButton";

   
   /**
    * @param eventBus handler manager
    */
   public ConfigureBuildPathView()
   {
      super(ID, ViewType.MODAL, GroovyExtension.LOCALIZATION_CONSTANT.configureBuildPathTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      // rewrite default width 200px defined in org.exoplatform.gwtframework.ui.client.component.ListGrid
      classPathEntryListGrid.setWidth(CLASS_PATH_ENTRY_LIST_GRID_WIDTH); 
      
      saveButton.setButtonId(ID_SAVE_BUTTON );     
      cancelButton.setButtonId(ID_CANCEL_BUTTON);

      addButton.setButtonId(ID_ADD_BUTTON );     
      removeButton.setButtonId(ID_REMOVE_BUTTON);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#getAddButton()
    */
   public HasClickHandlers getAddButton()
   {
      return addButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#getRemoveButton()
    */
   public HasClickHandlers getRemoveButton()
   {
      return removeButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#getSaveButton()
    */
   public HasClickHandlers getSaveButton()
   {
      return saveButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#getClassPathEntryListGrid()
    */
   public ListGridItem<GroovyClassPathEntry> getClassPathEntryListGrid()
   {
      return classPathEntryListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#enableRemoveButton(boolean)
    */
   public void enableRemoveButton(boolean isEnabled)
   {
      removeButton.setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#getSelectedItems()
    */
   public List<GroovyClassPathEntry> getSelectedItems()
   {
      return classPathEntryListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.classpath.ui.ConfigureBuildPathPresenter.Display#setCurrentRepository(java.lang.String)
    */
   @Override
   public void setCurrentRepository(String repository)
   {
      classPathEntryListGrid.setCurrentRepository(repository);
   }
}
