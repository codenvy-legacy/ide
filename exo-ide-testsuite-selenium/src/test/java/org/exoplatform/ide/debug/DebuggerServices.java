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
package org.exoplatform.ide.debug;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.Build;

/**
 * 
 *
 */
public class DebuggerServices extends BaseTest
{

   /**
    * This method check all buttons on debuger panel
    * after first run (breakpoints is not set) 
    * @throws Exception 
    */
   public void isDebugerButtonsWithoutBreakPoints() throws Exception
   {
      IDE.DEBUGER.waitResumeBtnIsEnabled(false);
      IDE.DEBUGER.waitStepIntoBtnIsEnabled(false);
      IDE.DEBUGER.waitStepOverBtnIsEnabled(false);
      IDE.DEBUGER.waitStepReturnBtnIsEnabled(false);
      IDE.DEBUGER.waitDisconnectBtnIsEnabled(true);
      IDE.DEBUGER.waitRemoveAllBreakPointsBtnIsEnabled(true);
      IDE.DEBUGER.waitChangeValueBtnIsEnabled(false);
      IDE.DEBUGER.waitEvaluateExpressionIsEnabled(false);
   }

   /**
    * This method check all buttons on debuger panel
    * if breakpoint set 
    * @throws Exception 
    */
   public void isDebugerButtonsWithSetBreakPoints() throws Exception
   {
      IDE.DEBUGER.waitResumeBtnIsEnabled(true);
      IDE.DEBUGER.waitStepIntoBtnIsEnabled(true);
      IDE.DEBUGER.waitStepOverBtnIsEnabled(true);
      IDE.DEBUGER.waitStepReturnBtnIsEnabled(true);
      IDE.DEBUGER.waitDisconnectBtnIsEnabled(true);
      IDE.DEBUGER.waitRemoveAllBreakPointsBtnIsEnabled(true);
      IDE.DEBUGER.waitChangeValueBtnIsEnabled(false);
      IDE.DEBUGER.waitEvaluateExpressionIsEnabled(true);
   }
   
   /**
    * This method check all buttons on debuger panel
    * if set breakзoint and select variable for chainging 
    * @throws Exception 
    */
   public void isDebugerButtonsAllBtnActive() throws Exception
   {
      IDE.DEBUGER.waitResumeBtnIsEnabled(true);
      IDE.DEBUGER.waitStepIntoBtnIsEnabled(true);
      IDE.DEBUGER.waitStepOverBtnIsEnabled(true);
      IDE.DEBUGER.waitStepReturnBtnIsEnabled(true);
      IDE.DEBUGER.waitDisconnectBtnIsEnabled(true);
      IDE.DEBUGER.waitRemoveAllBreakPointsBtnIsEnabled(true);
      IDE.DEBUGER.waitChangeValueBtnIsEnabled(true);
      IDE.DEBUGER.waitEvaluateExpressionIsEnabled(true);
   }
   
   
   /**
    * switch on demo application window
    * Important! This method means that will be opened only one window with demo application
    * @param currentWin
    */
   protected void switchDebugAppWin(String currentWin)
   {
      for (String handle : driver.getWindowHandles())
      {
         if (!currentWin.equals(handle))
         {
            driver.switchTo().window(handle);
            break;
         }

      }
   }

   protected void runDebugApp (String project) throws Exception{
      IDE.PROJECT.EXPLORER.waitOpened();
      // step 1 Open project
      IDE.PROJECT.OPEN.openProject(project);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      //step 2 run debug app and wait while build finish
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEBUG_APPLICATION);
      IDE.BUILD.waitOpened();
      String builderMessage = IDE.BUILD.getOutputMessage();
      assertTrue(builderMessage.startsWith(Build.Messages.BUILDING_PROJECT));
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      //step 3 check opening output panel and wait debug control 
      IDE.DEBUGER.waitOpened();

   }
   
   
   
   
   
   
}
