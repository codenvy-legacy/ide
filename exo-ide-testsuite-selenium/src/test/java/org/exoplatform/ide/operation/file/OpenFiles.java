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
package org.exoplatform.ide.operation.file;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OpenFiles extends BaseTest
{
   //IDE-109 Open file with non-default editor. 
   @Test
   public void openFileWithNonDefaultEditor() throws Exception
   {
      final String htmlFile = "newHtmlFile.html";
      
      final String groovyFile = "newGroovyFile.groovy";
      
      final String txtFile = "newTextFile.txt";
      
      final String xmlFile = "newXmlFile.xml";
      
      final String jsFile = "newJavascriptFile.js";
      
      final String cssFile = "newCssFile.css";
      
      final String gadgetFile = "newGoogleGadget.xml";
      
      createSaveAndCloseFile(MenuCommands.New.HTML_FILE, htmlFile);
      createSaveAndCloseFile(MenuCommands.New.GROOVY_SCRIPT_FILE, groovyFile);
      createSaveAndCloseFile(MenuCommands.New.TEXT_FILE, txtFile);
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, xmlFile);
      createSaveAndCloseFile(MenuCommands.New.JAVASCRIPT_FILE, jsFile);
      createSaveAndCloseFile(MenuCommands.New.CSS_FILE, cssFile);
      createSaveAndCloseFile(MenuCommands.New.GOOGLE_GADGET_FILE, gadgetFile);
      
   }
   
   private void createSaveAndCloseFile(String menuCommand, String fileName) throws Exception
   {
      openNewFileFromToolbar(menuCommand);
      Thread.sleep(1000);
      
      saveAsUsingToolbarButton(fileName);
      Thread.sleep(1000);
      
      closeTab("0");
      Thread.sleep(1000);
   }

}
