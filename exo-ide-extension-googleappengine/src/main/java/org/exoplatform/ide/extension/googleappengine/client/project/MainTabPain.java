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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.ui.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 * 
 */
public class MainTabPain extends Composite
{

   private static MainTabPainUiBinder uiBinder = GWT.create(MainTabPainUiBinder.class);

   interface MainTabPainUiBinder extends UiBinder<Widget, MainTabPain>
   {
   }

   @UiField
   Button updateApplicationButton;
   
   @UiField
   Button rollbackApplicationButton;

   @UiField
   Button getLogsButton;

   @UiField
   Button updateDosButton;

   @UiField
   Button updateIndexesButton;

   @UiField
   Button vacuumIndexesButton;

   @UiField
   Button updatePageSpeedButton;

   @UiField
   Button updateQueuesButton;

   public MainTabPain()
   {
      initWidget(uiBinder.createAndBindUi(this));
   }

   /**
    * @return the updateApplicationButton
    */
   public Button getUpdateApplicationButton()
   {
      return updateApplicationButton;
   }

   /**
    * @return the rollbackApplicationButton
    */
   public Button getRollbackApplicationButton()
   {
      return rollbackApplicationButton;
   }

   /**
    * @return the getLogsButton
    */
   public Button getGetLogsButton()
   {
      return getLogsButton;
   }

   /**
    * @return the updateDosButton
    */
   public Button getUpdateDosButton()
   {
      return updateDosButton;
   }

   /**
    * @return the updateIndexesButton
    */
   public Button getUpdateIndexesButton()
   {
      return updateIndexesButton;
   }

   /**
    * @return the vacuumIndexesButton
    */
   public Button getVacuumIndexesButton()
   {
      return vacuumIndexesButton;
   }

   /**
    * @return the updatePageSpeedButton
    */
   public Button getUpdatePageSpeedButton()
   {
      return updatePageSpeedButton;
   }

   /**
    * @return the updateQueuesButton
    */
   public Button getUpdateQueuesButton()
   {
      return updateQueuesButton;
   }
}
