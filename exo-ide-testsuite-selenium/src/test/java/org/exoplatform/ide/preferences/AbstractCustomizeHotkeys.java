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
package org.exoplatform.ide.preferences;

import org.exoplatform.ide.BaseTest;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public abstract class AbstractCustomizeHotkeys extends BaseTest
{
   static final String MESSAGE_LABEL = "ideCustomizeHotKeysMessageLabel";

   static final int NUMBER_OF_COMMANDS = 500;

   static final String GOOGLE_GADGET_FILE = "GoogleGadget.xml";

   static final String DEFAULT_TEXT_IN_GADGET = "Hello, world!";

   static String FOLDER_NAME;

   static final String INFO_MESSAGE_STYLE = "exo-cutomizeHotKey-label-info";

   static final String ERROR_MESSAGE_STYLE = "exo-cutomizeHotKey-label-error";

   static final String BIND_BUTTON_LOCATOR = "ideCustomizeHotKeysViewBindButton";

   static final String SAVE_BUTTON_LOCATOR = "ideCustomizeHotKeysViewOkButton";

   static final String CUSTOMIZE_HOTKEYS_FORM_LOCATOR = "ideCustomizeHotKeysView-window";

   static final String UNBIND_BUTTON_LOCATOR = "ideCustomizeHotKeysViewUnbindButton";

   static final String CANCEL_BUTTON_LOCATOR = "ideCustomizeHotKeysViewCancelButton";

   static final String TEXT_FIELD_LOCATOR = "ideCustomizeHotKeysViewHotKeyField";

   static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   String locator;

   interface Commands
   {
      public static final String CREATE_FILE_FROM_TEMPLATE = "Create File From Template...";

      public static final String NEW_CSS_FILE = "New CSS";

      public static final String NEW_TEXT_FILE = "New TEXT";

      public static final String NEW_HTML_FILE = "New HTML";
   }

}
