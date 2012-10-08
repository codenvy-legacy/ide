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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

import java.util.Map.Entry;

/**
 * Environment configuration view.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: ConfigurationView.java Oct 5, 2012 6:16:25 PM azatsarynnyy $
 *
 */
public class ConfigurationView extends ViewImpl implements ConfigurationPresenter.Display
{
   public static final String ID = "ideEnvironmentConfigurationView";

   private static final int HEIGHT = 700;

   private static final int WIDTH = 550;

   private static ApplicationInfoViewUiBinder uiBinder = GWT.create(ApplicationInfoViewUiBinder.class);

   /**
    * Ok button.
    */
   @UiField
   ImageButton okButton;

   /**
    * Application's information grid.
    */
   @UiField
   ConfigurationGrid applicationInfoGrid;

   interface ApplicationInfoViewUiBinder extends UiBinder<Widget, ConfigurationView>
   {
   }

   public ConfigurationView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.environmentConfigurationTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.ConfigurationPresenter.client.info.ApplicationInfoPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ConfigurationPresenter.client.info.ApplicationInfoPresenter.Display#getApplicationInfoGrid()
    */
   @Override
   public ListGridItem<Entry<String, String>> getApplicationInfoGrid()
   {
      return applicationInfoGrid;
   }
}
