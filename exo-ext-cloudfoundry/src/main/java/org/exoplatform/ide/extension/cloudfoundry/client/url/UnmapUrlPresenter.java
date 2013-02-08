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
package org.exoplatform.ide.extension.cloudfoundry.client.url;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UnmapUrlPresenter implements UnmapUrlView.ActionDelegate
{
   private UnmapUrlView view;

   @Inject
   public UnmapUrlPresenter()
   {
      this(new UnmapUrlViewImpl());
   }

   protected UnmapUrlPresenter(UnmapUrlView view)
   {
      this.view = view;
      this.view.setDelegate(this);
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
   public void onMapUrlClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onUnMapUrlClicked()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void onMapUrlChanged()
   {
      // TODO Auto-generated method stub

   }

   /**
    * Show dialog
    */
   public void showDialog()
   {
      view.showDialog();
   }
}