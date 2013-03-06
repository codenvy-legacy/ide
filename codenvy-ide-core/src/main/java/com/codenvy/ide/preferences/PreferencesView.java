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

import com.codenvy.ide.view.View;

import com.google.gwt.user.client.ui.AcceptsOneWidget;


/**
 * Interface of Preferences view.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface PreferencesView extends View<PreferencesView.ActionDelegate>
{
   /**
    * Needs for delegate some function into preferences view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Close button
       */
      void onCloseClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Apply button
       */
      void onApplyClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the OK button
       */
      void onOkClicked();

      /**
       * Performs any actions appropriate in response to select some preference.
       * 
       * @param preference selected preference
       */
      void selectedPreference(PreferencesPagePresenter preference);
   }

   /**
    * Close view.
    */
   void close();

   /**
    * Show preferences.
    */
   void showPreferences();

   /**
    * Returns content panel.
    * 
    * @return
    */
   AcceptsOneWidget getContentPanel();

   /**
    * Sets whether Apply button is enabled.
    * 
    * @param isEnabled <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   void setApplyButtonEnabled(boolean isEnabled);
}