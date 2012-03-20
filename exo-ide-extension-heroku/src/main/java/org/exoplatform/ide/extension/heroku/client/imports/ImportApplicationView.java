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
package org.exoplatform.ide.extension.heroku.client.imports;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.user.client.ui.CheckBox;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for importing Heroku application. Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 19, 2012 12:10:23 PM anya $
 * 
 */
public class ImportApplicationView extends ViewImpl implements ImportApplicationPresenter.Display
{
   private static final String VIEW_ID = "exoImportApplicationView";

   private static final int WIDTH = 410;

   private static final int HEIGHT = 170;

   private static final String APPLICATION_FIELD_NAME = "exoImportApplicationViewApplicationFieldName";

   private static final String PROJECT_FIELD_NAME = "exoImportApplicationViewProjectFieldName";

   private static final String DEPLOY_FIELD_NAME = "exoImportApplicationViewProjectFieldName";

   private static final String IMPORT_BUTTON_ID = "exoImportApplicationViewImportButton";

   private static final String CANCEL_BUTTON_ID = "exoImportApplicationViewCancelButton";

   private static ImportApplicationViewUiBinder uiBinder = GWT.create(ImportApplicationViewUiBinder.class);

   interface ImportApplicationViewUiBinder extends UiBinder<Widget, ImportApplicationView>
   {
   }

   /**
    * Project name field.
    */
   @UiField
   TextInput projectField;

   /**
    * Application's name field.
    */
   @UiField
   TextInput applicationField;

   /**
    * Deploy public key field.
    */
   @UiField
   CheckBox deployPublicKeyField;

   /**
    * Import button.
    */
   @UiField
   ImageButton importButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public ImportApplicationView()
   {
      super(VIEW_ID, ViewType.MODAL, HerokuExtension.LOCALIZATION_CONSTANT.importApplicationViewTitle(), null, WIDTH,
         HEIGHT, false);

      add(uiBinder.createAndBindUi(this));

      projectField.setName(PROJECT_FIELD_NAME);
      applicationField.setName(APPLICATION_FIELD_NAME);
      deployPublicKeyField.setName(DEPLOY_FIELD_NAME);

      importButton.setButtonId(IMPORT_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter.Display#getProjectName()
    */
   @Override
   public HasValue<String> getProjectName()
   {
      return projectField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter.Display#getApplicationName()
    */
   @Override
   public HasValue<String> getApplicationName()
   {
      return applicationField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter.Display#getDeployPublicKey()
    */
   @Override
   public HasValue<Boolean> getDeployPublicKey()
   {
      return deployPublicKeyField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter.Display#getImportButton()
    */
   @Override
   public HasClickHandlers getImportButton()
   {
      return importButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter.Display#setImportButtonEnabled(boolean)
    */
   @Override
   public void setImportButtonEnabled(boolean enabled)
   {
      importButton.setEnabled(enabled);
   }
}
