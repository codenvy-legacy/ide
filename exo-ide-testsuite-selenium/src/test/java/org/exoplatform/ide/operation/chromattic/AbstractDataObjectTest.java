/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.operation.chromattic;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 20, 2010 $
 *
 */
public abstract class AbstractDataObjectTest extends BaseTest
{
   
   //---Elements from Deploy Node Type dialog-------
   
   public static final String DEPLOY_NODE_TYPE_DIALOG_ID = "ideDeployNodeTypeForm";
   
   public static final String DEPLOY_NODE_TYPE_FORMAT_FIELD_NAME = "ideDeployNodeTypeFormFormatField";
   
   public static final String DEPLOY_NODE_TYPE_ALREADY_EXIST_FIELD_NAME = "ideDeployNodeTypeFormAlreadyExistBehaviorField";
   
   public static final String DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID = "ideDeployNodeTypeFormDeployButton";
   
   public static final String DEPLOY_NODE_TYPE_CANCEL_BUTTON_ID = "ideDeployNodeTypeFormCancelButton";
   
   //---Elements from Generate Node Type dialog-------
   
   public static final String IDE_GENERATED_TYPE_PREVIEW_VIEW_LOCATOR = "//div[@view-id='ideGeneratedTypePreviewView']";
   
   public static final String GENERATE_NODE_TYPE_DIALOG_ID = "ideGenerateNodeTypeForm";
   
   public static final String GENERATE_NODE_TYPE_FORMAT_FIELD = "ideGenerateNodeTypeFormFormatField";
   
   public static final String GENERATE_NODE_TYPE_GENERATE_BUTTON_ID = "ideGenerateNodeTypeFormGenerateButton";
   
   public static final String GENERATE_NODE_TYPE_CANCEL_BUTTON_ID = "ideGenerateNodeTypeFormCancelButton";
   
   /**
    * Wait while "Deploy node type" dialog will be present.
    * @throws Exception
    */
   public void waitForDeployNodeTypeDialog() throws Exception
   {
      waitForElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID);
   }
   
   /**
    * Wait while "Deploy node type" dialog will not be present.
    * @throws Exception
    */
   public void waitForDeployNodeTypeDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(DEPLOY_NODE_TYPE_DIALOG_ID);
   }
   
   /**
    * Wait while "Generate node type" dialog will be present.
    * @throws Exception
    */
   public void waitForGenerateNodeTypeDialog() throws Exception
   {
      waitForElementPresent(GENERATE_NODE_TYPE_DIALOG_ID);
   }
   
   /**
    * Wait while "Generate node type" dialog will not be present.
    * @throws Exception
    */
   public void waitForGenerateNodeTypeDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(GENERATE_NODE_TYPE_DIALOG_ID);
   }

   /**
    * Checks the state of preview node type button.
    * 
    * @param isPresent is present on toolbar and in menu
    * @param isEnabled is enabled or not
    * @throws Exception exception
    */
   protected void checkPreviewNodeTypeButton(boolean isPresent, boolean isEnabled) throws Exception
   {
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.Run.PREVIEW_NODE_TYPE, isPresent);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.PREVIEW_NODE_TYPE, isPresent);
      if (isEnabled)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, isEnabled);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.PREVIEW_NODE_TYPE, isEnabled);
      }
   }

   /**
    * Checks the state of deploy node type button.
    * 
    * @param isPresent is present on toolbar and in menu
    * @param isEnabled is enabled or not
    * @throws Exception exception
    */
   protected void checkDeployNodeTypeButton(boolean isPresent, boolean isEnabled) throws Exception
   {
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.Run.DEPLOY_NODE_TYPE, isPresent);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE, isPresent);
      if (isEnabled)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, isEnabled);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE, isEnabled);
      }
   }

}
