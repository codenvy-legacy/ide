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
package org.exoplatform.ide.extension.cloudfoundry.client.update;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UpdatePropertiesPresenter implements UpdatePropertiesView.ActionDelegate
{
   private UpdatePropertiesView view;

   @Inject
   public UpdatePropertiesPresenter()
   {
      this(new UpdatePropertiesViewImpl());
   }

   protected UpdatePropertiesPresenter(UpdatePropertiesView view)
   {
      this.view = view;
      this.view.setDelegate(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onOkClicked()
   {
      // TODO Auto-generated method stub

      view.close();

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCancelClicked()
   {
      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onPropertyChanged()
   {
      // TODO Auto-generated method stub

   }

   /**
    * Show dialog.
    */
   public void showDialog(String header, String title, String propertyValue)
   {
      view.showDialog(header, title, propertyValue);
   }
}