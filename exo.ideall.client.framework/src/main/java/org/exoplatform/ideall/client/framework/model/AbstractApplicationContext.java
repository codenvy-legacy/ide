/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.framework.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.initializer.ApplicationConfiguration;
import org.exoplatform.ideall.vfs.api.File;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public abstract class AbstractApplicationContext
{

   private ApplicationConfiguration applicationConfiguration;

   /**
    * Current active file in editor.
    */
   private File activeFile;
   
   private List<String> openedForms = new ArrayList<String>();
   
   private boolean showLineNumbers = true;
   
   private String entryPoint;
   
   private boolean showOutline = true;
   
   /**
    * Opened files in editor
    */
   private LinkedHashMap<String, File> openedFiles = new LinkedHashMap<String, File>();   

   public ApplicationConfiguration getApplicationConfiguration()
   {
      return applicationConfiguration;
   }

   public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration)
   {
      this.applicationConfiguration = applicationConfiguration;
   }

   /**
    * @return the activeFile
    */
   public File getActiveFile()
   {
      return activeFile;
   }

   /**
    * @param activeFile
    */
   public void setActiveFile(File activeFile)
   {
      this.activeFile = activeFile;
   }
   
   /**
    * @return the openedForms
    */
   public List<String> getOpenedForms()
   {
      return openedForms;
   }
   
   public boolean isShowLineNumbers()
   {
      return showLineNumbers;
   }

   public void setShowLineNumbers(boolean showLineNumbers)
   {
      this.showLineNumbers = showLineNumbers;
   }
   
   public String getEntryPoint()
   {
      return entryPoint;
   }

   public void setEntryPoint(String entryPoint)
   {
      this.entryPoint = entryPoint;
   }
   
   /**
    * @return the showOutline
    */
   public boolean isShowOutline()
   {
      return showOutline;
   }

   /**
    * @param showOutline the showOutline to set
    */
   public void setShowOutline(boolean showOutline)
   {
      this.showOutline = showOutline;
   }
   
   /**
    * @return the openedFiles
    */
   public HashMap<String, File> getOpenedFiles()
   {
      return openedFiles;
   }   

}
