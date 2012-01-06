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

import com.thoughtworks.selenium.Selenium;

import org.exoplatform.ide.core.AboutDialog;
import org.exoplatform.ide.core.AskDialog;
import org.exoplatform.ide.core.AskForValueDialog;
import org.exoplatform.ide.core.AvailableDependencies;
import org.exoplatform.ide.core.Button;
import org.exoplatform.ide.core.CodeAssistant;
import org.exoplatform.ide.core.CustomizeToolbar;
import org.exoplatform.ide.core.Delete;
import org.exoplatform.ide.core.DeployNodeType;
import org.exoplatform.ide.core.Editor;
import org.exoplatform.ide.core.FindReplace;
import org.exoplatform.ide.core.Folder;
import org.exoplatform.ide.core.GoToLine;
import org.exoplatform.ide.core.InformationDialog;
import org.exoplatform.ide.core.Input;
import org.exoplatform.ide.core.Loader;
import org.exoplatform.ide.core.LogReader;
import org.exoplatform.ide.core.Login;
import org.exoplatform.ide.core.Menu;
import org.exoplatform.ide.core.Navigation;
import org.exoplatform.ide.core.Outline;
import org.exoplatform.ide.core.Output;
import org.exoplatform.ide.core.Perspective;
import org.exoplatform.ide.core.Preview;
import org.exoplatform.ide.core.PreviewNodeType;
import org.exoplatform.ide.core.Project;
import org.exoplatform.ide.core.Properties;
import org.exoplatform.ide.core.RESTService;
import org.exoplatform.ide.core.RESTServiceDiscovery;
import org.exoplatform.ide.core.Rename;
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

   public AboutDialog ABOUT;

   public DeployNodeType DEPLOY_NODE_TYPE;

   public AvailableDependencies AVAILABLE_DEPENDENCIES;

   public Loader LOADER;

   public Outline OUTLINE;

   public Navigation NAVIGATION = new Navigation();

   public Perspective PERSPECTIVE;

   public CodeAssistant CODEASSISTANT;

   public Preview PREVIEW;

   public WarningDialog WARNING_DIALOG;

   public AskDialog ASK_DIALOG;

   public AskForValueDialog ASK_FOR_VALUE_DIALOG;

   public InformationDialog INFORMATION_DIALOG;

   public Statusbar STATUSBAR;

   public SelectWorkspace SELECT_WORKSPACE;

   @Deprecated
   public Workspace WORKSPACE = new Workspace();

   public GoToLine GOTOLINE;

   public Upload UPLOAD = new Upload();

   public FindReplace FINDREPLACE;

   public Templates TEMPLATES;

   public SaveAsTemplate SAVE_AS_TEMPLATE;

   public RESTService REST_SERVICE;

   public Search SEARCH = new Search();

   public Output OUTPUT;

   public Properties PROPERTIES;

   public PreviewNodeType PREVIEW_NODE_TYPE;

   public Versions VERSIONS = new Versions();

   public Folder FOLDER;

   public Rename RENAME;

   public org.exoplatform.ide.git.core.GIT GIT;

   public Heroku HEROKU = new Heroku();

   public OpenShift OPENSHIFT = new OpenShift();

   public Project PROJECT;

   public Input INPUT;

   public Button BUTTON;

   public Delete DELETE;
   
   public Login LOGIN;
   
   public LogReader LOG_READER;
   
   public CustomizeToolbar CUSTOMIZE_TOOLBAR = new CustomizeToolbar();
   
   public RESTServiceDiscovery REST_SERVICE_DISCOVERY;

   public IDE(Selenium selenium, String workspaceURL, WebDriver driver)
   {
      System.out.println("\r\n\r\n\r\n\r\n" + "Initializing IDE ( Selenium )\r\n" + "Workspace URL > " + workspaceURL
         + "\r\n\r\n\r\n");

      this.selenium = selenium;
      this.workspaceURL = workspaceURL;
      this.driver = driver;
      instance = this;
      GIT = new org.exoplatform.ide.git.core.GIT(selenium, driver);
      ABOUT = PageFactory.initElements(driver, AboutDialog.class);
      ASK_DIALOG = PageFactory.initElements(driver, AskDialog.class);
      ASK_FOR_VALUE_DIALOG = PageFactory.initElements(driver, AskForValueDialog.class);
      AVAILABLE_DEPENDENCIES = PageFactory.initElements(driver, AvailableDependencies.class);
      BUTTON = PageFactory.initElements(driver, Button.class);
      CODEASSISTANT = PageFactory.initElements(driver, CodeAssistant.class);
      DELETE = PageFactory.initElements(driver, Delete.class);
      DEPLOY_NODE_TYPE = PageFactory.initElements(driver, DeployNodeType.class);
      EDITOR = PageFactory.initElements(driver, Editor.class);
      FOLDER = PageFactory.initElements(driver, Folder.class);
      FINDREPLACE = PageFactory.initElements(driver, FindReplace.class);
      GOTOLINE = PageFactory.initElements(driver, GoToLine.class);
      INPUT = PageFactory.initElements(driver, Input.class);
      LOADER = PageFactory.initElements(driver, Loader.class);
      MENU = PageFactory.initElements(driver, Menu.class);
      OUTLINE = PageFactory.initElements(driver, Outline.class);
      OUTPUT = PageFactory.initElements(driver, Output.class);
      PREVIEW = PageFactory.initElements(driver, Preview.class);
      PREVIEW_NODE_TYPE = PageFactory.initElements(driver, PreviewNodeType.class);
      PERSPECTIVE = PageFactory.initElements(driver, Perspective.class);
      PROPERTIES = PageFactory.initElements(driver, Properties.class);
      PROJECT = PageFactory.initElements(driver, Project.class);
      RENAME = PageFactory.initElements(driver, Rename.class);
      SAVE_AS_TEMPLATE = PageFactory.initElements(driver, SaveAsTemplate.class);
      SELECT_WORKSPACE = PageFactory.initElements(driver, SelectWorkspace.class);
      STATUSBAR = PageFactory.initElements(driver, Statusbar.class);
      TOOLBAR = PageFactory.initElements(driver, Toolbar.class);
      TEMPLATES = PageFactory.initElements(driver, Templates.class);
      WARNING_DIALOG = PageFactory.initElements(driver, WarningDialog.class);
      GOTOLINE = PageFactory.initElements(driver, GoToLine.class);
      INFORMATION_DIALOG = PageFactory.initElements(driver, InformationDialog.class);
      REST_SERVICE = PageFactory.initElements(driver, RESTService.class);
      LOGIN = PageFactory.initElements(driver, Login.class);
      LOG_READER = PageFactory.initElements(driver, LogReader.class);
      CUSTOMIZE_TOOLBAR= PageFactory.initElements(driver, CustomizeToolbar.class);
      REST_SERVICE_DISCOVERY = PageFactory.initElements(driver, RESTServiceDiscovery.class);
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
