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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.component.TextInput;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.ui.CheckBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.ui.Composite;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 20, 2012 11:56:01 AM anya $
 * 
 */
public class DeployApplicationView extends Composite implements DeployApplicationPresenter.Display
{

   private static DeployApplicationViewUiBinder uiBinder = GWT.create(DeployApplicationViewUiBinder.class);

   interface DeployApplicationViewUiBinder extends UiBinder<Widget, DeployApplicationView>
   {
   }

   @UiField
   CheckBox useExistedField;

   @UiField
   TextInput applicationIdField;

   public DeployApplicationView()
   {
      super();
      initWidget(uiBinder.createAndBindUi(this));
      setHeight("160px");
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#getApplicationIdField()
    */
   @Override
   public HasValue<String> getApplicationIdField()
   {
      return applicationIdField;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#getUseExisting()
    */
   @Override
   public HasValue<Boolean> getUseExisting()
   {
      return useExistedField;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#enableApplicationIdField(boolean)
    */
   @Override
   public void enableApplicationIdField(boolean enable)
   {
      applicationIdField.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#getView()
    */
   @Override
   public Composite getView()
   {
      return this;
   }
}
