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

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

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

      public static final String SAVE = imageUrl + "menu/file/save.png";

      public static final String SAVE_AS = imageUrl + "menu/file/saveAs.png";

      public static final String SAVE_ALL = imageUrl + "menu/file/saveAll.png";

      public static final String SAVE_AS_TEMPLATE = imageUrl + "menu/file/saveTemplate.png";

      public static final String DELETE = imageUrl + "menu/file/delete.png";

      public static final String NEW = imageUrl + "menu/file/new.png";

      public static final String NEW_FOLDER = imageUrl + "menu/file/newFolder.png";

      public static final String SEARCH = imageUrl + "menu/file/search.png";

      public static final String PROPERTIES = imageUrl + "menu/view/properties.png";

      public static final String REFRESH = imageUrl + "menu/file/refresh.png";

      public static final String MOVE = imageUrl + "menu/file/move.png";

      public static final String DELIMETER = imageUrl + "menu/delimeter.png";

      public static final String TOOLS = imageUrl + "menu/tools2.png";

      public static final String ABOUT = imageUrl + "menu/help/about.png";

      public static final String CUSTOMIZE_TOOLBAR = imageUrl + "menu/window/customizeToolBar.png";

      public static final String TEMPLATES = imageUrl + "menu/file/createFromTemplate.png";
      
      public static final String GOTOFOLDER = imageUrl + "menu/file/goToFolder.png";
      
      public static final String GET_URL = imageUrl + "menu/file/url.png";
      
      public static final String OPENWITH = imageUrl + "menu/file/openWith.png";
      
      public static final String UPLOAD = imageUrl + "menu/file/upload.png";
      
      public static final String DOWNLOAD = imageUrl + "menu/file/download.png";
      
      public static final String DOWNLOAD_FOLDER = imageUrl + "menu/file/downloadZipped.png";
      
      public static final String WORKSPACE = imageUrl + "menu/window/workspace.png";
      
      /*
       * HTML
       */
      
      public static final String PREVIEW_HTML = imageUrl + "html/preview.png";

      /*
       * GROOVY
       */

      public static final String VALIDATE = imageUrl + "groovy/validate.png";

      public static final String DEPLOY_GROOVY = imageUrl + "groovy/deploy.png";

      public static final String UNDEPLOY_GROOVY = imageUrl + "groovy/undeploy.png";

      public static final String GROOVY_OUTPUT = imageUrl + "groovy/output.png";

      public static final String SET_AUTOLOAD = imageUrl + "groovy/setAutoload.png";

      public static final String UNSET_AUTOLOAD = imageUrl + "groovy/unsetAutoload.png";
      
      public static final String URL = imageUrl + "groovy/url.png";
      
      /*
       * GADGET
       */
      public static final String PREVIEW_GADGET = imageUrl + "gadget/preview.png";
      
      public static final String DEPLOY_GADGET = imageUrl + "gadget/deployGadget.png";
      
      public static final String UNDEPLOY_GADGET = imageUrl + "gadget/undeployGadget.png";
      
   }

   public interface Edit
   {

      public static final String UNDO = imageUrl + "menu/edit/undo.png";

      public static final String REDO = imageUrl + "menu/edit/redo.png";

      public static final String FORMAT = imageUrl + "menu/edit/format.png";

      public static final String SHOW_LINE_NUMBERS = imageUrl + "menu/edit/showLineNumbers.png";

      public static final String HIDE_LINE_NUMBERS = imageUrl + "menu/edit/hideLineNumbers.png";
      
      public static final String COPY_FILE = imageUrl + "menu/edit/copy.png";
      
      public static final String CUT_FILE = imageUrl + "menu/edit/cut.png";
      
      public static final String PASTE_FILE = imageUrl + "menu/edit/paste.png";
      
   }
   
   public interface ControlButtons
   {
      
      public static final String MAXIMIZE = imageUrl + "buttons/minmax/maximize.png";
      
      public static final String RESTORE = imageUrl + "buttons/minmax/minimize.png";
      
   }

   public interface ViewPanel
   {


   }

   public interface PropertiesPanel
   {

      public static final String ICON = imageUrl + "panel/properties/properties.png";

   }

   public interface OutputPanel
   {
      
      public static final String BUTTON_CLEAR = imageUrl + "panel/output/buttonClear.png";

      public static final String ICON = imageUrl + "panel/output/output.png";

   }
   
   public interface BrowserPanel
   {

      public static final String ICON = imageUrl + "panel/workspace/workspace.png";

   }

   
   public interface SearchPanel
   {

      static final String ICON = imageUrl + "panel/search/search.png";

   }


   public interface FileTypes
   {

      public static final String WORKSPACE = imageUrl + "repository/workspace.png";

      public static final String DEFAULT = imageUrl + "filetype/default.png";

      public static final String TXT = imageUrl + "filetype/txt.png";

      public static final String HTML = imageUrl + "filetype/html.png";

      public static final String XML = imageUrl + "filetype/xml.png";

      public static final String GROOVY = imageUrl + "filetype/groovy.png";

      public static final String JAVASCRIPT = imageUrl + "filetype/javascript.gif";

      public static final String CSS = imageUrl + "filetype/css.png";

      public static final String GADGET = imageUrl + "filetype/gadget.png";

   }

   public interface Logos
   {

      public static final String ABOUT_LOGO = imageUrl + "logo/exoplatform.gif";
   
   }

   public interface Buttons
   {

      public static final String OK = imageUrl + "buttons/ok.png";

      public static final String NO = imageUrl + "buttons/no.png";

      public static final String CANCEL = imageUrl + "buttons/cancel.png";

      public static final String YES = imageUrl + "buttons/yes.png";

      public static final String SEARCH = imageUrl + "buttons/search.png";

      public static final String ADD = imageUrl + "buttons/add.png";

      public static final String DELETE = imageUrl + "buttons/remove.png";
      
      public static final String UP = imageUrl + "buttons/up.png";

      public static final String DOWN = imageUrl + "buttons/down.png";

      public static final String DEFAULTS = imageUrl + "buttons/defaults.png";

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
   
   public static final String BLANK = imageUrl + "blank.png";

}
