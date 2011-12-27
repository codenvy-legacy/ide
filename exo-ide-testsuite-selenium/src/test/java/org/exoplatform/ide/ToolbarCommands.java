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
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public interface ToolbarCommands
{

   public interface Editor
   {
      String FIND_REPLACE = "Find/Replace...";

      String UNDO = "Undo Typing";

      String REDO = "Redo Typing";

      String LOCK_FILE = "Lock File";

      String UNLOCK_FILE = "Unlock File";
   }

   public interface File
   {
      String SAVE = "Save";

      String SAVE_AS = "Save As...";
      
      String DELETE = "Delete Item(s)...";

      String REFRESH = "Refresh Selected Folder";

      String CUT_SELECTED_ITEM = "Cut Selected Item(s)";

      String COPY_SELECTED_ITEM = "Copy Selected Item(s)";
      
      String PASTE = "Paste Selected Item(s)";

      final String SEARCH = "Search...";
   }

   public interface View
   {
      String SHOW_OUTLINE = "Show Outline";

      String HIDE_OUTLINE = "Hide Outline";

      String SHOW_DOCUMENTATION = "Show Documentation";

      String HIDE_DOCUMENTATION = "Hide Documentation";

      String SHOW_PROPERTIES = "Show Properties";

      String VIEW_VERSION_HISTORY = "View Item Version History";

      String HIDE_VERSION_HISTORY = "Hide Item Version History";

      String VIEW_VERSION = "View Item Version...";

      String VIEW_NEWER_VERSION = "View Newer Version";

      String VIEW_OLDER_VERSION = "View Older Version";
   }

   public interface Run
   {
      String SHOW_PREVIEW = "Show Preview";
      
      String SHOW_GADGET_PREVIEW = "Show Gadget Preview";

      String DEPLOY_GADGET = "Deploy Gadget to GateIn";

      String UNDEPLOY_GADGET = "UnDeploy Gadget from GateIn";

      String RUN_GROOVY_SERVICE = "Run in Sandbox";

      String VALIDATE_GROOVY_SERVICE = "Validate REST Service";

      String DEPLOY_GROOVY_SERVICE = "Deploy REST Service";

      String UNDEPLOY_GROOVY_SERVICE = "Undeploy REST Service";

      String DEPLOY_SANDBOX = "Deploy REST Service to Sandbox";

      String DEPLOY_UWA_WIDGET = "Deploy UWA widget to Ecosystem";

      String UNDEPLOY_SANDBOX = "Undeploy REST Service from Sandbox";

      String SET_AUTOLOAD = "Set REST Service Autoload";

      String PREVIEW_NODE_TYPE = "Preview node type";

      String DEPLOY_NODE_TYPE = "Deploy node type";
      
      String LAUNCH_REST_SERVICE = "Launch REST Service...";
      
      String UNSET_AUTOLOAD ="Unset REST Service Autoload";
   }
}
