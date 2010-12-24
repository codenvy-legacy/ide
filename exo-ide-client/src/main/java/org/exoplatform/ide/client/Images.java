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
package org.exoplatform.ide.client;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Images
{

   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();

   public interface MainMenu
   {

      public static final String DELIMETER = IMAGE_URL + "menu/delimeter.png";

      public static final String TOOLS = IMAGE_URL + "menu/tools2.png";

      public static final String DOWNLOAD_MENU = IMAGE_URL + "menu/file/download_menu.png";

      public static final String UPLOAD_MENU = IMAGE_URL + "menu/file/upload_menu.png";

      public static final String GET_URL = IMAGE_URL + "bundled/view/url.png";

      /*
       * FILE
       */
      
      public interface File
      {

         public static final String OPEN_LOCAL_FILE = IMAGE_URL + "bundled/file/open_local_file.png";
         
         public static final String OPEN_FILE_BY_PATH = IMAGE_URL + "bundled/file/open_file_by_path.png";         

         public static final String UPLOAD = IMAGE_URL + "bundled/file/upload.png";

      }

      /*
       * GROOVY
       */

      public static final String URL = IMAGE_URL + "groovy/url.png";

   }

   public interface Edit
   {

      public static final String REPLACE = IMAGE_URL + "menu/edit/replace.png";

   }

   public interface ControlButtons
   {

      public static final String MAXIMIZE = IMAGE_URL + "buttons/minmax/maximize.png";

      public static final String RESTORE = IMAGE_URL + "buttons/minmax/minimize.png";

   }

   public interface OutputPanel
   {

      public static final String BUTTON_CLEAR = IMAGE_URL + "panel/output/buttonClear.png";

   }

   public interface FileTypes
   {

      public static final String DEFAULT = IMAGE_URL + "filetype/default.png";

      public static final String WORKSPACE = IMAGE_URL + "repository/workspace.png";

      public static final String TXT = IMAGE_URL + "filetype/txt.png";

      public static final String HTML = IMAGE_URL + "filetype/html.png";

      public static final String XML = IMAGE_URL + "filetype/xml.png";

      public static final String GROOVY = IMAGE_URL + "filetype/groovy.png";
      
      public static final String REST_SERVICE = IMAGE_URL + "filetype/rest.png";

      public static final String JAVASCRIPT = IMAGE_URL + "filetype/javascript.gif";

      public static final String CSS = IMAGE_URL + "filetype/css.png";

      public static final String GADGET = IMAGE_URL + "filetype/gadget.png";

      public static final String FOLDER = IMAGE_URL + "filetype/folder_closed.png";

      public static final String UWA_WIDGET = IMAGE_URL + "filetype/uwa-widget.png";
      
      // TODO add appropriate icon 
      public static final String GROOVY_TEMPLATE = IMAGE_URL + "filetype/gtmpl.png";      
      
      public static final String CHROMATTIC = IMAGE_URL + "filetype/chromattic.png";

   }

   public interface Logos
   {

      public static final String ABOUT_LOGO = IMAGE_URL + "logo/eXo-IDE-Logo.png";

   }

   public interface Buttons
   {

      public static final String OK = IMAGE_URL + "buttons/ok.png";

      public static final String NO = IMAGE_URL + "buttons/no.png";

      public static final String CANCEL = IMAGE_URL + "buttons/cancel.png";

      public static final String YES = IMAGE_URL + "buttons/yes.png";

      public static final String SEARCH = IMAGE_URL + "buttons/search.png";

      public static final String ADD = IMAGE_URL + "buttons/add.png";

      public static final String DELETE = IMAGE_URL + "buttons/remove.png";

      public static final String UP = IMAGE_URL + "buttons/up.png";

      public static final String DOWN = IMAGE_URL + "buttons/down.png";

      public static final String DEFAULTS = IMAGE_URL + "buttons/defaults.png";

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

      public static final String ASK = IMAGE_URL + "dialog/ask.png";

   }
   
   public interface Outline
   {
      public static final String FUNCTION_ITEM = IMAGE_URL + "outline/function-item.png";
      
      public static final String VAR_ITEM = IMAGE_URL + "outline/var-item.png";
      
      public static final String METHOD_ITEM = IMAGE_URL + "outline/method-item.png";
      
      public static final String PROPERTY_ITEM = IMAGE_URL + "outline/property-item.png";
      
      public static final String TAG_ITEM = IMAGE_URL + "outline/tag.png";
      
      public static final String CDATA_ITEM = IMAGE_URL + "outline/cdata-item.png";
      
      public static final String GROOVY_TAG_ITEM = IMAGE_URL + "outline/groovy-tag.png";
      
      public static final String CLASS_ITEM = IMAGE_URL + "outline/class-item.png";
      
      public static final String PRIVATE_METHOD = IMAGE_URL + "outline/private-method.png";

      public static final String PUBLIC_METHOD = IMAGE_URL + "outline/public-method.png";

      public static final String PROTECTED_METHOD = IMAGE_URL + "outline/protected-method.png";

      public static final String DEFAULT_METHOD = IMAGE_URL + "outline/default-method.png";
   
      public static final String PRIVATE_FIELD = IMAGE_URL + "outline/private-field.png";

      public static final String PUBLIC_FIELD = IMAGE_URL + "outline/public-field.png";

      public static final String PROTECTED_FIELD = IMAGE_URL + "outline/protected-field.png";

      public static final String DEFAULT_FIELD = IMAGE_URL + "outline/default-field.png";
   }
   
   public interface Versioning
   {

      public static final String RESTORE_VERSION = IMAGE_URL + "versioning/restore_version.png";
      
      public static final String OPEN_VERSION = IMAGE_URL + "versioning/open_version.png";

   }
   
   public interface RestService
   {
      public static final String REST_SERVICE = IMAGE_URL + "restservice/rest-service.png";
      
      public static final String IN = IMAGE_URL + "restservice/in.png";
      
      public static final String OUT   = IMAGE_URL + "restservice/out.png";
      
      public static final String METHOD   = IMAGE_URL + "restservice/method.png";
      
      public static final String PARAMETER   = IMAGE_URL + "restservice/parameter.png";
      
      public static final String VAR   = IMAGE_URL + "restservice/var.png";
   }
   
   public interface Editor
   {

      public static final String READONLY_FILE = IMAGE_URL + "panel/editor/file_readonly.png";
   }

   public static final String BLANK = IMAGE_URL + "blank.png";

}
