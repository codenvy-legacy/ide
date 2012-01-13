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
package org.exoplatform.ide.extension.openshift.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationView.java Dec 5, 2011 1:58:14 PM vereshchaka $
 * 
 */
public class DeployApplicationView extends Composite implements DeployApplicationPresenter.Display
{
   interface DeployApplicationViewUiBinder extends UiBinder<Widget, DeployApplicationView>
   {
   }

   private static DeployApplicationViewUiBinder uiBinder = GWT.create(DeployApplicationViewUiBinder.class);

   @UiField
   TextInput nameField;

   @UiField
   SelectItem typeField;

   public DeployApplicationView()
   {
      super();
      initWidget(uiBinder.createAndBindUi(this));
      setHeight("150px");
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getApplicationNameField()
    */
   @Override
   public HasValue<String> getApplicationNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getView()
    */
   @Override
   public Composite getView()
   {
      return this;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.deploy.DeployApplicationPresenter.Display#getTypeField()
    */
   @Override
   public HasValue<String> getTypeField()
   {
      return typeField;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.deploy.DeployApplicationPresenter.Display#setTypeValues(java.lang.String[])
    */
   @Override
   public void setTypeValues(String[] types)
   {
      typeField.setValueMap(types);
   }

}
