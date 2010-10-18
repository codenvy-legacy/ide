/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.model;

import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ApplicationContext
{

   private String selectedNavigationPanel;

   /**
    * Current active text editor.
    */
   private LinkedHashMap<String, File> preloadFiles = new LinkedHashMap<String, File>();

   /*
    * Last entered value in Groovy script output form
    */
   private String testGroovyScriptURL;

   private String searchContent;

   private String searchFileName;

   private String searchContentType;

   private List<IDEModule> modules = new ArrayList<IDEModule>();

   /*
    * Store status bar control id's here
    */

   private List<String> statusBarItems = new ArrayList<String>();

   public List<String> getStatusBarItems()
   {
      return statusBarItems;
   }

   public ApplicationContext()
   {
      //toolBarItems.add("");
   }

   public List<IDEModule> getModules()
   {
      return modules;
   }

   /**
    * Uses for storing items to need copy
    */
   private List<Item> itemsToCopy = new ArrayList<Item>();

   /**
    * Uses to storing items to need cut
    */
   private List<Item> itemsToCut = new ArrayList<Item>();

   public List<Item> getItemsToCopy()
   {
      return itemsToCopy;
   }

   public List<Item> getItemsToCut()
   {
      return itemsToCut;
   }

   private String selectedEditorDescription;

   public LinkedHashMap<String, File> getPreloadFiles()
   {
      return preloadFiles;
   }

   public String getSelectedNavigationPanel()
   {
      return selectedNavigationPanel;
   }

   public void setSelectedNavigationPanel(String selectedNavigationPanel)
   {
      this.selectedNavigationPanel = selectedNavigationPanel;
   }

   /**
    * @return
    */
   public String getTestGroovyScriptURL()
   {
      return testGroovyScriptURL;
   }

   /**
    * @param testGroovyScriptURL
    */
   public void setTestGroovyScriptURL(String testGroovyScriptURL)
   {
      this.testGroovyScriptURL = testGroovyScriptURL;
   }

   public String getSearchContent()
   {
      return searchContent;
   }

   public void setSearchContent(String searchContent)
   {
      this.searchContent = searchContent;
   }

   public String getSearchFileName()
   {
      return searchFileName;
   }

   public void setSearchFileName(String searchFileName)
   {
      this.searchFileName = searchFileName;
   }

   public String getSearchContentType()
   {
      return searchContentType;
   }

   public void setSearchContentType(String searchContentType)
   {
      this.searchContentType = searchContentType;
   }

   public String getSelectedEditorDescription()
   {
      return selectedEditorDescription;
   }

   public void setSelectedEditorDescription(String selectedEditor)
   {
      this.selectedEditorDescription = selectedEditor;
   }

}
