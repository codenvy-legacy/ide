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

import com.gargoylesoftware.htmlunit.Page;

import com.thoughtworks.selenium.Selenium;

import org.exoplatform.ide.core.AskDialog;
import org.exoplatform.ide.core.AskForValueDialog;
import org.exoplatform.ide.core.Button;
import org.exoplatform.ide.core.CodeAssistant;
import org.exoplatform.ide.core.Delete;
import org.exoplatform.ide.core.Editor;
import org.exoplatform.ide.core.FindReplace;
import org.exoplatform.ide.core.Folder;
import org.exoplatform.ide.core.GoToLine;
import org.exoplatform.ide.core.InformationDialog;
import org.exoplatform.ide.core.Input;
import org.exoplatform.ide.core.Menu;
import org.exoplatform.ide.core.Navigation;
import org.exoplatform.ide.core.Outline;
import org.exoplatform.ide.core.Output;
import org.exoplatform.ide.core.Perspective;
import org.exoplatform.ide.core.Preview;
import org.exoplatform.ide.core.Project;
import org.exoplatform.ide.core.Properties;
import org.exoplatform.ide.core.RESTService;
import org.exoplatform.ide.core.Rename;
import org.exoplatform.ide.core.SaveAs;
import org.exoplatform.ide.core.SaveAsTemplate;
import org.exoplatform.ide.core.Search;
import org.exoplatform.ide.core.SelectWorkspace;
import org.exoplatform.ide.core.Statusbar;
import org.exoplatform.ide.core.Templates;
import org.exoplatform.ide.core.Toolbar;
import org.exoplatform.ide.core.Upload;
import org.exoplatform.ide.core.Versions;
import org.exoplatform.ide.core.WarningDialog;
import org.exoplatform.ide.core.Workspace;
import org.exoplatform.ide.paas.heroku.core.Heroku;
import org.exoplatform.ide.paas.openshift.core.OpenShift;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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

   private WebDriver driver;

   private static IDE instance;

   public static IDE getInstance()
   {
      return instance;
   }

   public Menu MENU;

   public Toolbar TOOLBAR;

   public Editor EDITOR;

   public Outline OUTLINE = new Outline();

   public Navigation NAVIGATION = new Navigation();

   public Perspective PERSPECTIVE = new Perspective();

   public CodeAssistant CODEASSISTANT;

   public Preview PREVIEW = new Preview();

   public SaveAs SAVE_AS = new SaveAs();

   public WarningDialog WARNING_DIALOG;

   public AskDialog ASK_DIALOG;

   public AskForValueDialog ASK_FOR_VALUE_DIALOG;

   public InformationDialog INFORMATION_DIALOG = new InformationDialog();

   public Statusbar STATUSBAR;

   public SelectWorkspace SELECT_WORKSPACE = new SelectWorkspace();

   public Workspace WORKSPACE = new Workspace();

   public GoToLine GOTOLINE;

   public Upload UPLOAD = new Upload();

   public FindReplace FINDREPLACE;

   public Templates TEMPLATES = new Templates();

   public SaveAsTemplate SAVE_AS_TEMPLATE = new SaveAsTemplate();

   public RESTService REST_SERVICE = new RESTService();

   public Search SEARCH = new Search();

   public Output OUTPUT = new Output();

   public Properties PROPERTIES;

   public Versions VERSIONS = new Versions();

   public Folder FOLDER;

   public Rename RENAME;

   public org.exoplatform.ide.git.core.GIT GIT = new org.exoplatform.ide.git.core.GIT(selenium);

   public Heroku HEROKU = new Heroku();

   public OpenShift OPENSHIFT = new OpenShift();

   public Project PROJECT;

   public Input INPUT;

   public Button BUTTON;

   public Delete DELETE;

   public IDE(Selenium selenium, String workspaceURL, WebDriver driver)
   {
      System.out.println("\r\n\r\n\r\n\r\n" + "Initializing IDE ( Selenium )\r\n" + "Workspace URL > " + workspaceURL
         + "\r\n\r\n\r\n");

      this.selenium = selenium;
      this.workspaceURL = workspaceURL;
      this.driver = driver;
      instance = this;

      ASK_DIALOG = PageFactory.initElements(driver, AskDialog.class);
      ASK_FOR_VALUE_DIALOG = PageFactory.initElements(driver, AskForValueDialog.class);
      BUTTON = PageFactory.initElements(driver, Button.class);
      CODEASSISTANT = PageFactory.initElements(driver, CodeAssistant.class);
      DELETE = PageFactory.initElements(driver, Delete.class);
      EDITOR = PageFactory.initElements(driver, Editor.class);
      FOLDER = PageFactory.initElements(driver, Folder.class);
      FINDREPLACE = PageFactory.initElements(driver, FindReplace.class);
      GOTOLINE = PageFactory.initElements(driver, GoToLine.class);
      INPUT = PageFactory.initElements(driver, Input.class);
      MENU = PageFactory.initElements(driver, Menu.class);
      PROPERTIES = PageFactory.initElements(driver, Properties.class);
      PROJECT = PageFactory.initElements(driver, Project.class);
      RENAME = PageFactory.initElements(driver, Rename.class);
      STATUSBAR = PageFactory.initElements(driver, Statusbar.class);
      TOOLBAR = PageFactory.initElements(driver, Toolbar.class);
      WARNING_DIALOG = PageFactory.initElements(driver, WarningDialog.class);

   }

   public Selenium getSelenium()
   {
      return selenium;
   }

   public WebDriver driver()
   {
      return driver;
   }

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
      driver().switchTo().defaultContent();
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
