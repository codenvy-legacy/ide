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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubView.java Dec 7, 2011 3:37:28 PM vereshchaka $
 */
public class ImportFromGithubView extends ViewImpl implements ImportFromGithubPresenter.Display
{
   private static final String ID = "ImportFromGithubView";

   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.importLoadDialogTitle();

   private static final int HEIGHT = 245;

   private static final int WIDTH = 450;

   interface ImportFromGithubViewUiBinder extends UiBinder<Widget, ImportFromGithubView>
   {
   }

   private static ImportFromGithubViewUiBinder uiBinder = GWT.create(ImportFromGithubViewUiBinder.class);
   
   @UiField
   TextInput projectUrlField;
   
   @UiField
   SelectItem selectProjectTypeField;
   
   @UiField
   ImageButton importButton;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   Label notifyLabel;

   public ImportFromGithubView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getImportButton()
    */
   @Override
   public HasClickHandlers getImportButton()
   {
      return importButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getUrlField()
    */
   @Override
   public HasValue<String> getUrlField()
   {
      return projectUrlField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getProjectTypeField()
    */
   @Override
   public HasValue<String> getProjectTypeField()
   {
      return selectProjectTypeField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#setProjectTypeValues(java.lang.String[])
    */
   @Override
   public void setProjectTypeValues(String[] values)
   {
      selectProjectTypeField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#enableImportButton(boolean)
    */
   @Override
   public void enableImportButton(boolean enabled)
   {
      importButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getNotifyLabel()
    */
   @Override
   public HasValue<String> getNotifyLabel()
   {
      return notifyLabel;
   }

}
