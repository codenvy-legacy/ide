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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 3:47:59 PM anya $
 * 
 */
public class BackendsTabPane extends Composite
{
   private static BackendsTabPaneUiBinder uiBinder = GWT.create(BackendsTabPaneUiBinder.class);

   interface BackendsTabPaneUiBinder extends UiBinder<Widget, BackendsTabPane>
   {
   }

   public BackendsTabPane()
   {
      initWidget(uiBinder.createAndBindUi(this));
   }
   
   @UiField
   Button configureBackendButton;
   
   @UiField
   Button deleteBackendButton;

   @UiField
   Button getListBackendsButton;

   @UiField
   Button rollbackBackendButton;

   @UiField
   Button rollbackAllBackendsButton;

   @UiField
   Button setBackendStateButton;

   @UiField
   Button updateAllBackendsButton;

   @UiField
   Button updateBackendButton;
   
   @UiField
   Button updateBackendsButton;

   /**
    * @return the uiBinder
    */
   public static BackendsTabPaneUiBinder getUiBinder()
   {
      return uiBinder;
   }

   /**
    * @return the configureBackendButton
    */
   public Button getConfigureBackendButton()
   {
      return configureBackendButton;
   }

   /**
    * @return the deleteBackendButton
    */
   public Button getDeleteBackendButton()
   {
      return deleteBackendButton;
   }

   /**
    * @return the getListBackendsButton
    */
   public Button getGetListBackendsButton()
   {
      return getListBackendsButton;
   }

   /**
    * @return the rollbackBackendButton
    */
   public Button getRollbackBackendButton()
   {
      return rollbackBackendButton;
   }

   /**
    * @return the rollbackAllBackendsButton
    */
   public Button getRollbackAllBackendsButton()
   {
      return rollbackAllBackendsButton;
   }

   /**
    * @return the setBackendStateButton
    */
   public Button getSetBackendStateButton()
   {
      return setBackendStateButton;
   }

   /**
    * @return the updateAllBackendsButton
    */
   public Button getUpdateAllBackendsButton()
   {
      return updateAllBackendsButton;
   }

   /**
    * @return the updateBackendButton
    */
   public Button getUpdateBackendButton()
   {
      return updateBackendButton;
   }

   /**
    * @return the updateBackends
    */
   public Button getUpdateBackends()
   {
      return updateBackendsButton;
   }
   
   
   
}
