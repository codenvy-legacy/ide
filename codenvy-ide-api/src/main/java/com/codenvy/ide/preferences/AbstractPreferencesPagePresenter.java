/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.preferences;

import com.google.gwt.resources.client.ImageResource;

/**
 * Abstract base implementation for all preference page implementations.
 * It's simpler to get started using Preferences.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public abstract class AbstractPreferencesPagePresenter implements PreferencesPagePresenter
{
   protected DirtyStateListener delegate;

   private String title;

   private ImageResource icon;

   /**
    * Create preference page.
    * 
    * @param title
    * @param icon
    */
   public AbstractPreferencesPagePresenter(String title, ImageResource icon)
   {
      this.title = title;
      this.icon = icon;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setUpdateDelegate(DirtyStateListener delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle()
   {
      return title;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ImageResource getIcon()
   {
      return icon;
   }
}