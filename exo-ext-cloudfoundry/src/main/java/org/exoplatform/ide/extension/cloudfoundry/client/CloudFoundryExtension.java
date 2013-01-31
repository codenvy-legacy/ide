/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.api.ui.paas.PaaSAgent;
import org.exoplatform.ide.extension.Extension;

/**
 * Extension add Cloud Foundry support to the IDE Application.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Cloud Foundry Support.", id = "ide.ext.cloudfoundry", version = "2.0.0")
public class CloudFoundryExtension
{
   /**
    * The generator of an {@link AutoBean}.
    */
   public static final CloudFoundryAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(CloudFoundryAutoBeanFactory.class);

   public static final CloudFoundryLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(CloudFoundryLocalizationConstant.class);

   /**
    * Default CloudFoundry server.
    */
   public static final String DEFAULT_SERVER = "http://api.cloudfoundry.com";

   private static final String ID = "CloudFoundry";

   @Inject
   public CloudFoundryExtension(PaaSAgent paasAgent)
   {
      // TODO Auto-generated constructor stub
      //      paasAgent.registerPaaS(id, title, image, providesTemplate, supportedProjectTypes, wizardPage, preferencePage);
   }
}