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

import com.codenvy.ide.presenter.Presenter;

import com.google.gwt.resources.client.ImageResource;


/**
 * Interface of preference page.
 * Describes main methods for all preference pages.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface PreferencesPagePresenter extends Presenter
{
   /**
    * Needs for delegate updateControls function into PagePresenter.
    */
   interface DirtyStateListener
   {
      /**
       * Updates preference view components without content panel.
       */
      void onDirtyChanged();
   }

   /**
    * Sets new delegate
    * 
    * @param delegate
    */
   void setUpdateDelegate(DirtyStateListener delegate);

   /**
    * Performs any actions appropriate in response to the user
    * having pressed the Apply button.
    */
   void doApply();

   /**
    * Returns whether this page is changed or not.
    * This information is typically used by the preferences presenter to decide
    * when the information is changed.
    * 
    * @return <code>true</code> if this page is changed, and
    * <code>false</code> otherwise
    */
   boolean isDirty();

   /**
    * Return preference page's title. This title will be shown into list of preferences.
    * 
    * @return
    */
   String getTitle();

   /**
    * Returns this preference page's icon. This icon will be shown into list of preferences.
    * 
    * @return
    */
   ImageResource getIcon();
}