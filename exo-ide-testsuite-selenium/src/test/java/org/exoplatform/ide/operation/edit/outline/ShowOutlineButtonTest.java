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
package org.exoplatform.ide.operation.edit.outline;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.Test;

/**
 * Check, that toolbut button Show outline is present only
 * JavaScript, XML, HTML, Google Gadget or Groovy template files are opened.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Aug 11, 2010
 *
 */

public class ShowOutlineButtonTest extends BaseTest
{
   //check, that show outline button is shown only for
   //files, which have outline
   @Test
   public void testShowOutlineButton() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //---- 1 ------
      //open JavaScript file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 2 ------
      //open xml file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 3 ------
      //open html file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 4 ------
      //open google gadget file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 5 ------
      //open text file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, false);
      
      //---- 6 ------
      //open css file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, false);
      
      //---- 7 ------
      //open rest service file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 8 ------
      //open groovy script file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 9 ------
      //open groovy template file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
      //---- 10 ------
      //open select tab with xml file
      IDE.editor().selectTab(1);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, true);
      
   }
   
}