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
package org.exoplatform.ideall.client;

import org.exoplatform.gwtframework.ui.util.UIHelper;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Images
{

   public static final String imageUrl = UIHelper.getGadgetImagesURL();

   public interface MainMenu
   {

      public static final String SAVE = imageUrl + "menu/next_edition/save.png";

      public static final String SAVE_AS = imageUrl + "menu/next_edition/saveAs.png";

      public static final String SAVE_ALL = imageUrl + "menu/next_edition/saveAll.png";

      public static final String SAVE_AS_TEMPLATE = imageUrl + "menu/next_edition/saveTemplate.png";

      public static final String DELETE = imageUrl + "menu/delete.png";

      public static final String NEW = imageUrl + "menu/newDocument.png";

      public static final String NEW_FOLDER = imageUrl + "menu/newFolder.png";

      public static final String SEARCH = imageUrl + "menu/next_edition/search.png";

      public static final String PROPERTIES = imageUrl + "menu/next_edition/properties.png";

      public static final String PREVIEW = imageUrl + "menu/next_edition/preview.png";

      public static final String REFRESH = imageUrl + "menu/refresh.png";

      public static final String MOVE = imageUrl + "menu/move.png";

      public static final String DELIMETER = imageUrl + "menu/delimeter.png";

      public static final String TOOLS = imageUrl + "menu/tools2.png";

      public static final String ABOUT = imageUrl + "menu/about.png";

      public static final String CUSTOMIZE_TOOLBAR = imageUrl + "menu/customizeToolBar.png";

      public static final String TEMPLATES = imageUrl + "menu/next_edition/templates.png";
      
      public static final String GOTOFOLDER = imageUrl + "menu/goToFile3.png";
      
      public static final String GET_URL = imageUrl + "menu/url1.png";
      

      public static final String UPLOAD = imageUrl + "menu/next_edition/upload1.png";
      
      public static final String DOWNLOAD = imageUrl + "menu/next_edition/download1.png";
      
      public static final String DOWNLOAD_FOLDER = imageUrl + "menu/next_edition/downloadZipped1.png";
      
      public static final String WADL = imageUrl + "groovy/wadl1.png";
      
      

      /*
       * GROOVY
       */

      public static final String VALIDATE = imageUrl + "menu/groovy/validate.png";

      public static final String DEPLOY = imageUrl + "menu/groovy/deploy.png";

      public static final String UNDEPLOY = imageUrl + "menu/groovy/undeploy.png";

      public static final String GROOVY_OUTPUT = imageUrl + "menu/groovy/output.png";

      public static final String SET_AUTOLOAD = imageUrl + "groovy/setAutoload1.png";

      public static final String UNSET_AUTOLOAD = imageUrl + "groovy/unsetAutoload1.png";

   }

   public interface Edit
   {

      public static final String UNDO = imageUrl + "menu/editing/undo.png";

      public static final String REDO = imageUrl + "menu/editing/redo.png";

      public static final String FORMAT = imageUrl + "menu/next_edition/format.png";

      public static final String SHOW_LINE_NUMBERS = imageUrl + "edit/showLineNumbers2.png";

      public static final String HIDE_LINE_NUMBERS = imageUrl + "edit/hideLineNumbers2.png";
      
   }
   
   public interface ControlButtons
   {
      
      public static final String MAXIMIZE = imageUrl + "buttons/minmax/maximize2.png";
      
      public static final String RESTORE = imageUrl + "buttons/minmax/minimize2.png";
      
   }

   public interface ViewPanel
   {

      public static final String BUTTON_CLEAR = imageUrl + "view/buttonClear1.png";

   }

   public interface PropertiesPanel
   {

      public static final String ICON = imageUrl + "properties/tabIcon.png";

   }

   public interface OutputPanel
   {

      public static final String ICON = imageUrl + "output/output2.png";

   }

   public interface BrowserPanel
   {

      public static final String ICON = imageUrl + "browser/workspace.png";

   }

   public interface SearchPanel
   {

      static final String ICON = imageUrl + "search/search.png";

   }

   public interface StatusBar
   {

      public static final String VISIBILITY = imageUrl + "properties.png";

      public static final String FODLER_OPENED = imageUrl + "folderOpened.png";

      public static final String GRADIENT = imageUrl + "statusbar/gradient.png";

   }

   public interface FileTypes
   {

      public static final String WORKSPACE = imageUrl + "filetypes/workspace.png";

      public static final String DEFAULT = imageUrl + "filetypes/default.png";

      public static final String TXT = imageUrl + "filetypes/txt.png";

      public static final String HTML = imageUrl + "filetypes/html.png";

      public static final String XML = imageUrl + "filetypes/xml.png";

      public static final String GROOVY = imageUrl + "filetypes/groovy.png";

      public static final String JAVASCRIPT = imageUrl + "filetypes/javascript.gif";

      public static final String CSS = imageUrl + "filetypes/css.png";

      public static final String GADGET = imageUrl + "filetypes/gadget1.png";

   }

   public interface Logos
   {

      public static final String ABOUT_LOGO = imageUrl + "logo/exoplatform.gif";
   }

   public interface Buttons
   {

      public static final String OK = imageUrl + "buttons/yes1.png";

      public static final String NO = imageUrl + "buttons/no1.png";

      public static final String CANCEL = imageUrl + "buttons/no1.png";

      public static final String YES = imageUrl + "buttons/yes1.png";

      public static final String SEARCH = imageUrl + "buttons/search2.png";

      public static final String ADD = imageUrl + "buttons/add2.png";

      public static final String DELETE = imageUrl + "buttons/remove2.png";

   }

   public interface Toolbar
   {

      public static final String UP = imageUrl + "toolbar/move_up1.png";

      public static final String DOWN = imageUrl + "toolbar/move_down1.png";

      public static final String DEFAULTS = imageUrl + "toolbar/refresh1.png";

   }

   public interface RepositoryService
   {

      public static final String SERVICE = imageUrl + "repository/service.png";

      public static final String REPOSITORY = imageUrl + "repository/repository.png";

      public static final String WORKSPACE = imageUrl + "repository/workspace.png";

   }

   public interface Dialogs
   {

      public static final String ASK = imageUrl + "dialog/ask.png";

   }

}
