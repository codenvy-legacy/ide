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
package org.exoplatform.ide.extension.aws.client.beanstalk.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployApplicationView.java Sep 25, 2012 12:56:56 PM azatsarynnyy $
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
   TextInput envNameField;

   @UiField
   SelectItem solutionStackField;

   public DeployApplicationView()
   {
      super();
      initWidget(uiBinder.createAndBindUi(this));
      setHeight("260px");
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getNameField()
    */
   @Override
   public TextFieldItem getNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getEnvNameField()
    */
   @Override
   public TextFieldItem getEnvNameField()
   {
      return envNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getSolutionStackField()
    */
   @Override
   public HasValue<String> getSolutionStackField()
   {
      return solutionStackField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getView()
    */
   @Override
   public Composite getView()
   {
      return this;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#setSolutionStackValues(java.lang.String[])
    */
   @Override
   public void setSolutionStackValues(String[] values)
   {
      solutionStackField.setValueMap(values);
   }

}
