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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdatePropertiesPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryProjectPresenter implements CloudFoundryProjectView.ActionDelegate
{
   private CloudFoundryProjectView view;

   private ApplicationInfoPresenter applicationInfoPresenter;

   private UnmapUrlPresenter unmapUrlPresenter;

   private UpdatePropertiesPresenter updateProperyPresenter;

   @Inject
   public CloudFoundryProjectPresenter(ApplicationInfoPresenter applicationInfoPresenter,
      UnmapUrlPresenter unmapUrlPresenter, UpdatePropertiesPresenter updateProperyPresenter)
   {
      this(new CloudFoundryProjectViewImpl(), applicationInfoPresenter, unmapUrlPresenter, updateProperyPresenter);
   }

   protected CloudFoundryProjectPresenter(CloudFoundryProjectView view,
      ApplicationInfoPresenter applicationInfoPresenter, UnmapUrlPresenter unmapUrlPresenter,
      UpdatePropertiesPresenter updateProperyPresenter)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.applicationInfoPresenter = applicationInfoPresenter;
      this.unmapUrlPresenter = unmapUrlPresenter;
      this.updateProperyPresenter = updateProperyPresenter;
   }

   /**
    * Show dialog.
    */
   public void showDialog()
   {
      view.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCloseClicked()
   {
      view.close();
   }

   @Override
   public void onUpdateClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onLogsClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onServicesClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onDeleteClicked()
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onInfoClicked()
   {
      applicationInfoPresenter.showDialog();
   }

   @Override
   public void onStartClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onStopClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onRestartClicked()
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditMemoryClicked()
   {
      // TODO Auto-generated method stub
      updateProperyPresenter.showDialog("Update memory", "", view.getApplicationMemory());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditUrlClicked()
   {
      unmapUrlPresenter.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditInstancesClicked()
   {
      // TODO Auto-generated method stub
      updateProperyPresenter.showDialog("Update instances", "", view.getApplicationInstances());
   }
}