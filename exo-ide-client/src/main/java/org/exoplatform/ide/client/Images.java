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

      public static final String CLEAR_OUTPUT = IMAGE_URL + "panel/output/clearOutput.png";

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

      public static final String JSON = IMAGE_URL + "filetype/json.png";

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

      //public static final String OK = IMAGE_URL + "buttons/ok.png";

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

      public static final String OBJECT_ITEM = IMAGE_URL + "outline/object.png";

      public static final String ARRAY_ITEM = IMAGE_URL + "outline/array.png";

      public static final String DATA_ITEM = IMAGE_URL + "outline/data.png";

      public static final String ERROR_ITEM = IMAGE_URL + "outline/error.png";

      public static final String INTERFACE_ITEM = IMAGE_URL + "outline/interface-item.png";

      public static final String MODULE_ITEM = IMAGE_URL + "outline/module-item.png";

      public static final String LOCAL_VARIABLE_ITEM = IMAGE_URL + "outline/local-variable-item.png";

      public static final String GLOBAL_VARIABLE_ITEM = IMAGE_URL + "outline/global-variable-item.png";

      public static final String CLASS_VARIABLE_ITEM = IMAGE_URL + "outline/class-variable-item.png";

      public static final String INSTANCE_VARIABLE_ITEM = IMAGE_URL + "outline/instance-variable-item.png";

      public static final String CONSTANT_ITEM = IMAGE_URL + "outline/constant-item.png";

      public static final String PHP_TAG_ICON = IMAGE_URL + "outline/php-tag.png";

      public static final String CLASS_CONSTANT_ICON = IMAGE_URL + "outline/class-constant-item.png";

      public static final String NAMESPACE_ICON = IMAGE_URL + "outline/namespace-item.png";
   }

   public interface Versioning
   {

      public static final String RESTORE_VERSION = IMAGE_URL + "versioning/restore_version.png";

      public static final String OPEN_VERSION = IMAGE_URL + "versioning/open_version.png";

   }

   public interface RestService
   {
      public static final String CLASS = IMAGE_URL + "restservice/resource.png";

      public static final String RESOURCE = IMAGE_URL + "restservice/parameter.png";

      public static final String METHOD = IMAGE_URL + "restservice/var.png";
   }

   public interface Editor
   {
      public static final String READONLY_FILE = IMAGE_URL + "panel/editor/file_readonly.png";
      public static final String SOURCE_BUTTON_ICON = IMAGE_URL + "panel/editor/source_button.png";;
      public static final String DESIGN_BUTTON_ICON = IMAGE_URL + "panel/editor/design_button.png";;      
      public static final String EDITOR_SWITCHER_BACKGROUND = IMAGE_URL + "panel/editor/editor_switcher_background.png";
   }

   public static final String BLANK = IMAGE_URL + "blank.png";

}
