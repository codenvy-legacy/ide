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
package org.exoplatform.ide.extension.cloudfoundry.client.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsView;
import org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationView;
import org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationView;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationView;
import org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoView;
import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginView;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectView;
import org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServiceView;
import org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServiceViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesView;
import org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdatePropertiesView;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdatePropertiesViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlView;
import org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlViewImpl;

/**
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CloudFoundryGinModule extends AbstractGinModule
{
   /**
    * {@inheritDoc}
    */
   @Override
   protected void configure()
   {
      bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
      bind(ApplicationsView.class).to(ApplicationsViewImpl.class).in(Singleton.class);
      bind(CreateApplicationView.class).to(CreateApplicationViewImpl.class).in(Singleton.class);
      bind(CloudFoundryProjectView.class).to(CloudFoundryProjectViewImpl.class).in(Singleton.class);
      bind(DeleteApplicationView.class).to(DeleteApplicationViewImpl.class).in(Singleton.class);
      bind(ManageServicesView.class).to(ManageServicesViewImpl.class).in(Singleton.class);
      bind(CreateServiceView.class).to(CreateServiceViewImpl.class).in(Singleton.class);
      bind(DeployApplicationView.class).to(DeployApplicationViewImpl.class).in(Singleton.class);
      bind(ApplicationInfoView.class).to(ApplicationInfoViewImpl.class).in(Singleton.class);
      bind(UnmapUrlView.class).to(UnmapUrlViewImpl.class).in(Singleton.class);
      bind(UpdatePropertiesView.class).to(UpdatePropertiesViewImpl.class).in(Singleton.class);
   }
}