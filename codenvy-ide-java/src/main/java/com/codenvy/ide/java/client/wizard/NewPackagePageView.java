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
package com.codenvy.ide.java.client.wizard;

import com.codenvy.ide.api.mvp.View;

import com.codenvy.ide.json.JsonArray;


/**
 * View for new Java package wizard.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public interface NewPackagePageView extends View<NewPackagePageView.ActionDelegate>
{
   /**
    * Action delegate for new Java package wizard.
    */
   public interface ActionDelegate
   {
      /**
       * Package parent changed.
       * @param index the parent index.
       */
      void parentChanged(int index);

      /**
       * New package name changed, validate it.
       */
      void checkPackageName();
   }

   /**
    * Set all packages or source folders in project
    * @param parents the packages or source folder names
    */
   void setParents(JsonArray<String> parents);

   /**
    * Select parent by index
    * 
    * @param index of the parent in the list
    */
   void selectParent(int indexOf);

   /**
    * Get new package name.
    * @return the new package name
    */
   String getPackageName();

   /**
    * Disable all ui components.
    */
   void disableAllUi();

}
