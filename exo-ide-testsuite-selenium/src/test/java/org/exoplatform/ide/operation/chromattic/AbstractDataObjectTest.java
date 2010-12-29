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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 20, 2010 $
 *
 */
public class AbstractDataObjectTest extends BaseTest
{
   /**
    * Check form for generating node type definition is present.
    */
   protected void checkGenerateNodeTypeFormPresent()
   {
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideGenerateNodeTypeFormDynamicForm\"]/item[name=ideGenerateNodeTypeFormFormatField||value=EXO]/textbox"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideGenerateNodeTypeFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideGenerateNodeTypeFormGenerateButton\"]"));
   }

   /**
    * Check form for deploying node type definition is present.
    */
   protected void checkDeployNodeTypeFormPresent()
   {
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployNodeTypeFormDynamicForm\"]/item[name=ideDeployNodeTypeFormFormatField]/textbox"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployNodeTypeFormDynamicForm\"]/item[name=ideDeployNodeTypeFormAlreadyExistBehaviorField]/textbox"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeployNodeTypeFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeployNodeTypeFormDeployButton\"]"));
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
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.Run.PREVIEW_NODE_TYPE, isPresent);
      IDE.menu().checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.PREVIEW_NODE_TYPE, isPresent);
      if (isEnabled)
      {
         IDE.toolbar().assertButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, isEnabled);
         IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.PREVIEW_NODE_TYPE, isEnabled);
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
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.Run.DEPLOY_NODE_TYPE, isPresent);
      IDE.menu().checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE, isPresent);
      if (isEnabled)
      {
         IDE.toolbar().assertButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, isEnabled);
         IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE, isEnabled);
      }
   }

   /**
    * Checks whether editor with generated node type definition is present.
    * 
    * @param isPresent is present or not
    */
   protected void checkViewWithGeneratedCodePresent(boolean isPresent)
   {
      assertEquals(isPresent, selenium.isElementPresent("//div[@eventproxy='ideGeneratedTypePreviewPanel']"));
   }

   /**
    * Get content of message in dialog window.
    * 
    * @return {@link String} message
    */
   protected String getMessageFromGloabalWarning()
   {
      return selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]");
   }
}
