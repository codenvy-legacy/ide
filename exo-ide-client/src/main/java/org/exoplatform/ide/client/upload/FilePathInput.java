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
package org.exoplatform.ide.client.upload;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.upload.event.FilePathSelectedEvent;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmitry Nochevnov</a>
 * @version $
 */

public class FilePathInput extends TextField
{
   public FilePathInput(final HandlerManager eventBus)
   {
      addKeyPressHandler(new KeyPressHandler() {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               eventBus.fireEvent(new FilePathSelectedEvent());
            }
         }
         
      });
   }
   
}
