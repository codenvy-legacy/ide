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

      public static final String DELIMETER = imageUrl + "menu/delimeter.png";

      public static final String TOOLS = imageUrl + "menu/tools2.png";

      public static final String DOWNLOAD_MENU = imageUrl + "menu/file/download_menu.png";

      public static final String UPLOAD_MENU = imageUrl + "menu/file/upload_menu.png";

      public static final String GET_URL = imageUrl + "bundled/view/url.png";

      /*
       * FILE
       */
      
      public interface File
      {

         public static final String OPEN_LOCAL_FILE = imageUrl + "bundled/file/open_local_file.png";

         public static final String UPLOAD = imageUrl + "bundled/file/upload.png";

      }

      /*
       * GROOVY
       */

      public static final String URL = imageUrl + "groovy/url.png";

   }

   public interface Edit
   {

      public static final String REPLACE = imageUrl + "menu/edit/replace.png";

   }

   public interface ControlButtons
   {

      public static final String MAXIMIZE = imageUrl + "buttons/minmax/maximize.png";

      public static final String RESTORE = imageUrl + "buttons/minmax/minimize.png";

   }

   public interface OutputPanel
   {

      public static final String BUTTON_CLEAR = imageUrl + "panel/output/buttonClear.png";

   }

   public interface FileTypes
   {

      public static final String DEFAULT = imageUrl + "filetype/default.png";

      public static final String WORKSPACE = imageUrl + "repository/workspace.png";

      public static final String TXT = imageUrl + "filetype/txt.png";

      public static final String HTML = imageUrl + "filetype/html.png";

      public static final String XML = imageUrl + "filetype/xml.png";

      public static final String GROOVY = imageUrl + "filetype/groovy.png";

      public static final String JAVASCRIPT = imageUrl + "filetype/javascript.gif";

      public static final String CSS = imageUrl + "filetype/css.png";

      public static final String GADGET = imageUrl + "filetype/gadget.png";

      public static final String FOLDER = imageUrl + "filetype/folder_closed.png";

      public static final String UWA_WIDGET = imageUrl + "filetype/uwa-widget.png";

   }

   public interface Logos
   {

      public static final String ABOUT_LOGO = imageUrl + "logo/eXo-IDE-Logo.png";

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

//   public interface RepositoryService
//   {
//
//      public static final String SERVICE = imageUrl + "repository/service.png";
//
//      public static final String REPOSITORY = imageUrl + "repository/repository.png";
//
//      public static final String WORKSPACE = imageUrl + "repository/workspace.png";
//
//   }

   public interface Dialogs
   {

      public static final String ASK = imageUrl + "dialog/ask.png";

   }
   
   public interface Outline
   {
      public static final String FUNCTION_ITEM = imageUrl + "outline/function-item.png";
      
      public static final String VAR_ITEM = imageUrl + "outline/var-item.png";
      
      public static final String METHOD_ITEM = imageUrl + "outline/method-item.png";
      
      public static final String PROPERTY_ITEM = imageUrl + "outline/property-item.png";
   }

   public static final String BLANK = imageUrl + "blank.png";

}
