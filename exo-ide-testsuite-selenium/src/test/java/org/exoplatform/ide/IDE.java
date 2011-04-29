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
package org.exoplatform.ide;

import org.exoplatform.ide.core.AskDialog;
import org.exoplatform.ide.core.CodeAssistant;
import org.exoplatform.ide.core.Editor;
import org.exoplatform.ide.core.ErrorDialog;
import org.exoplatform.ide.core.InformationDialog;
import org.exoplatform.ide.core.Menu;
import org.exoplatform.ide.core.Navigation;
import org.exoplatform.ide.core.OpenWith;
import org.exoplatform.ide.core.Outline;
import org.exoplatform.ide.core.Perspective;
import org.exoplatform.ide.core.Preview;
import org.exoplatform.ide.core.SaveAs;
import org.exoplatform.ide.core.SelectWorkspace;
import org.exoplatform.ide.core.Statusbar;
import org.exoplatform.ide.core.Toolbar;
import org.exoplatform.ide.core.WarningDialog;
import org.exoplatform.ide.core.Workspace;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDE
{

   private Selenium selenium;

   private static IDE instance;

   public static IDE getInstance()
   {
      return instance;
   }

   public IDE(Selenium selenium)
   {
      this.selenium = selenium;
      instance = this;
   }

   public Selenium getSelenium()
   {
      return selenium;
   }

   public Menu MENU = new Menu();
   
   public Toolbar TOOLBAR = new Toolbar();

   public Editor EDITOR = new Editor();
   
   public Outline OUTLINE = new Outline();
   
   public Navigation NAVIGATION = new Navigation();
   
   public Perspective PERSPECTIVE = new Perspective();
   
   public CodeAssistant CODEASSISTANT = new CodeAssistant();
   
   public Preview PREVIEW = new Preview();
   
   public SaveAs SAVE_AS = new SaveAs();
   
   public WarningDialog WARNING_DIALOG = new WarningDialog();

   public ErrorDialog ERROR_DIALOG = new ErrorDialog();
   
   public AskDialog ASK_DIALOG = new AskDialog();
   
   public InformationDialog INFORMATION_DIALOG = new InformationDialog();
   
   public Statusbar STATUSBAR = new Statusbar();
   
   public SelectWorkspace SELECTWORKSPACE = new SelectWorkspace();
   
   public Workspace WORKSPACE = new Workspace();
   
   public OpenWith OPENWITH = new OpenWith();

   /**
    * Select main frame of IDE.
    * 
    * This method is used, after typing text in editor.
    * To type text you must select editor iframe. After typing,
    * to return to them main frame, use selectMainFrame()
    * 
    */
   public void selectMainFrame()
   {
      if (selenium.isElementPresent("//div[@id='eXo-IDE-container']"))
      {
         selenium.selectFrame("//div[@id='eXo-IDE-container']//iframe");
      }
      else
      {
         selenium.selectFrame("relative=top");
      }
   }

   private String workspaceURL;

   public void setWorkspaceURL(String workspaceURL)
   {
      this.workspaceURL = workspaceURL;
   }

   public String getWorkspaceURL()
   {
      return workspaceURL;
   }

}
