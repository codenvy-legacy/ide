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
package org.exoplatform.ide.wizard;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;


/**
 * Interface of Wizard view.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface WizardView
{
   /**
    * Sets whether Next button is visible.
    * 
    * @param isVisible <code>true</code> to visible the button, <code>false</code>
    * to disable it
    */
   void setNextButtonVisible(boolean isVisible);

   /**
    * Sets whether Next button is enabled.
    * 
    * @param isEnabled <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   void setNextButtonEnabled(boolean isEnabled);

   /**
    * Sets whether Back button is visible.
    * 
    * @param isVisible <code>true</code> to visible the button, <code>false</code>
    * to disable it
    */
   void setBackButtonVisible(boolean isVisible);

   /**
    * Sets whether Finish button is enabled.
    * 
    * @param isEnabled <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   void setFinishButtonEnabled(boolean isEnabled);

   /**
    * Sets new delegate
    * 
    * @param delegate
    */
   void setDelegate(ActionDelegate delegate);

   /**
    * Sets new caption of wizard's page
    * 
    * @param caption
    */
   void setCaption(String caption);

   /**
    * Sets new notice of wizard's page
    * 
    * @param notice
    */
   void setNotice(String notice);

   /**
    * Sets new image of wizard's page
    * 
    * @param image
    */
   void setImage(IsWidget image);

   /**
    * Close wizard
    */
   void close();

   /**
    * Show wizard
    */
   void showWizard();

   /**
    * Returns place of main form where will be shown current wizard page.
    * 
    * @return place of main form
    */
   AcceptsOneWidget getContentPanel();

   /**
    * Needs for delegate some function into Wizard view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Next button
       */
      void onNextClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Back button
       */
      void onBackClicked();
      
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Finish button
       */
      void onFinishClicked();
      
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Cancel button
       */
      void onCancelClicked();
   }
}