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

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public interface MenuCommands
{
   public interface New
   {

      String NEW = "New";

      String XML_FILE = "XML";

      String HTML_FILE = "HTML";

      String TEXT_FILE = "Text";

      String JAVASCRIPT_FILE = "JavaScript";

      String CSS_FILE = "CSS";

      String GOOGLE_GADGET_FILE = "Google Gadget";

      String REST_SERVICE_FILE = "REST Service";

      String GROOVY_SCRIPT_FILE = "POGO";

      String NETVIBES_WIDGET = "Netvibes Widget";

      String GROOVY_TEMPLATE_FILE = "Template";

      String FILE_FROM_TEMPLATE = "File From Template...";

      String PROJECT_FROM_TEMPLATE = "Project From Template...";

      String FOLDER = "Folder...";

      String JAVA_PROJECT = "Java Project...";

      String JAVA_SPRING_PROJECT = "Java Spring Project...";

      String CHROMATTIC = "Data Object";

      String PROJECT_TEMPLATE = "Project Template...";

      String JAVA_CLASS = "Java Class";

      String JSP = "JSP";

      String RUBY = "Ruby File";

      String PHP = "PHP File";
   }

   public interface Run
   {

      public static final String RUN = "Run";

      public static final String UNDEPLOY_REST_SERVICE = "Undeploy";

      public static final String DEPLOY_REST_SERVICE = "Deploy";

      public static final String DEPLOY_SANDBOX = "Deploy to Sandbox";

      public static final String DEPLOY_UWA_WIDGET = "Deploy UWA widget";

      public static final String UNDEPLOY_SANDBOX = "Undeploy from Sandbox";

      public static final String SET_AUTOLOAD = "Set Autoload";

      public static final String UNSET_AUTOLOAD = "Unset Autoload";

      public static final String LAUNCH_REST_SERVICE = "Launch REST Service...";

      public static final String RUN_GROOVY_SERVICE = "Run in Sandbox";

      public static final String SHOW_PREVIEW = "Show Preview";

      public static final String SHOW_GADGET_PREVIEW = "Show Gadget Preview";

      public static final String SHOW_GROOVY_TEMPLATE_PREVIEW = "Show Template Preview";

      public static final String VALIDATE = "Validate";

      public static final String PREVIEW_NODE_TYPE = "Preview node type";

      public static final String DEPLOY_NODE_TYPE = "Deploy node type";

      public static final String DEPLOY_GADGET = "Deploy Gadget to GateIn";

      public static final String UNDEPLOY_GADGET = "UnDeploy Gadget from GateIn";
   }

   public interface View
   {
      String VIEW = "View";

      String GO_TO_FOLDER = "Go to Folder";

      String GET_URL = "Get URL...";

      String SHOW_PROPERTIES = "Properties";

      String VERSION_HISTORY = "Version History...";

      String VERSION_LIST = "Version...";

      String NEWER_VERSION = "Newer Version";

      String OLDER_VERSION = "Older Version";

      String LOG_READER = "Log";

   }

   public interface File
   {
      public static final String FILE = "File";

      public static final String UPLOAD_FILE = "Upload File...";

      public static final String UPLOAD_FOLDER = "Upload Zipped Folder...";

      public static final String OPEN_LOCAL_FILE = "Open Local File...";

      public static final String OPEN_FILE_BY_PATH = "Open File By Path...";

      public static final String DOWNLOAD = "Download...";

      public static final String DOWNLOAD_ZIPPED_FOLDER = "Download Zipped Folder...";

      public static final String SAVE = "Save";

      public static final String SAVE_AS = "Save As...";

      public static final String SAVE_ALL = "Save All";

      public static final String SAVE_AS_TEMPLATE = "Save As Template...";

      public static final String DELETE = "Delete...";

      public static final String RENAME = "Rename...";

      public static final String SEARCH = "Search...";

      public static final String REFRESH = "Refresh";

      public static final String RESTORE_VERSION = "Restore to Version";

      public static final String REFRESH_TOOLBAR = "Refresh Selected Folder";

      public static final String CONFIGURE_CLASS_PATH = "Configure Classpath...";
   }

   public interface Edit
   {
      public static final String EDIT_MENU = "Edit";

      public static final String UNDO_TYPING = "Undo Typing";

      public static final String REDO_TYPING = "Redo Typing";

      public static final String CUT_TOOLBAR = "Cut Selected Item(s)";

      public static final String COPY_TOOLBAR = "Copy Selected Item(s)";

      public static final String PASTE_TOOLBAR = "Paste Selected Item(s)";

      public static final String CUT_MENU = "Cut Item(s)";

      public static final String COPY_MENU = "Copy Item(s)";

      public static final String PASTE_MENU = "Paste Item(s)";

      public static final String HIDE_LINE_NUMBERS = "Hide Line Numbers";

      public static final String SHOW_LINE_NUMBERS = "Show Line Numbers";

      public static final String FORMAT = "Format";

      public static final String GO_TO_LINE = "Go to Line...";

      public static final String DELETE_CURRENT_LINE = "Delete Current Line";

      public static final String FIND_REPLACE = "Find/Replace...";

      public static final String LOCK_FILE = "Lock File";

      public static final String UNLOCK_FILE = "Unlock File";

      public static final String LUCK_UNLOCK_FILE = "Lock " + "\\" + " Unlock File";

   }

   public interface CodeEditors
   {
      public static final String CODE_MIRROR = "Code Editor";

      public static final String CK_EDITOR = "WYSWYG Editor";
   }

   public interface Help
   {
      public static final String HELP = "Help";

      public static final String ABOUT = "About...";

      public static final String REST_SERVICES = "REST Services Discovery";

      String AVAILABLE_DEPENDENCIES = "Show Available Dependencies...";
   }

   public interface Window
   {
      public static final String WINDOW = "Window";

      public static final String SELECT_WORKSPACE = "Select Workspace...";

      public static final String CUSTOMIZE_HOTKEYS = "Customize Hotkeys...";

      public static final String CUSTOMIZE_TOOLBAR = "Customize Toolbar...";
   }

   public interface Git
   {
      String GIT = "Git";

      String ADD = "Add...";

      String BRANCHES = "Branches...";

      String CLONE = "Clone Repository...";

      String DELETE = "Delete Repository...";

      String COMMIT = "Commit...";

      String FETCH = "Fetch...";

      String INIT = "Initialize Repository";

      String MERGE = "Merge...";

      String PULL = "Pull...";

      String PUSH = "Push...";

      String REMOTE = "Remote";

      String REMOTES = "Remotes...";

      String REMOVE = "Remove...";

      String RESET_FILES = "Reset Files...";

      String RESET = "Reset...";

      String SHOW_HISTORY = "Show History...";

      String STATUS = "Status";
   }

   public interface Project
   {
      String PROJECT = "Project";

      String NEW = "New";

      String OPEN_PROJECT = "Open...";

      String CLOSE_PROJECT = "Close";

      String EMPTY_PROJECT = "Empty Project...";

      String FROM_TEMPLATE = "From Template...";
   }

   public interface PaaS
   {
      String PAAS = "PaaS";

      public interface Heroku
      {
         String HEROKU = "Heroku";

         String SWITCH_ACCOUNT = "Switch account...";

         String CREATE_APPLICATION = "Create application...";

         String DELETE_APPLICATION = "Delete application...";

         String RENAME_APPLICATION = "Rename application...";

         String APPLICATION_INFO = "Application info...";

         String DEPLOY_PUBLIC_KEY = "Deploy public key...";

         String RAKE = "Rake...";
      }

      public interface OpenShift
      {
         String OPENSHIFT = "OpenShift";

         String CREATE_DOMAIN = "Create domain...";

         String CREATE_APPLICATION = "Create application...";

         String DELETE_APPLICATION = "Delete application...";

         String APPLICATION_INFO = "Application info...";

         String USER_INFO = "User info...";
      }

      public interface CloudFoundry
      {

         String CLOUDFOUNDRY = "CloudFoundry";

         String CREATE_APPLICATION = "Create Application";

         String UPDATE_APPLICATION = "Update Application";

         String DELETE_APPLICATION = "Delete Application...";

         String APPLICATION_INFO = "Application Info...";

         String START_APPLICATION = "Start Application";

         String STOP_APPLICATION = "Stop Application";

         String RESTART_APPLICATION = "Restart Application";

         String APPLICATION_URLS = "Application URLs";

         String UPDATE_MEMORY = "Update Memory...";

         String UPDATE_INSTANCES = "Update Instances...";

         String SWITCH_ACCOUNT = "Switch Account...";

         String APPPLICATIONS = "Applications";

      }

   }

}
