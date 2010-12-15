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

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;

/**
 * A widget that wraps the HTML &lt;input type='file'&gt; element.
 * 
 * When file selected, calls onFileSelected method from {@link FileSelectedHandler}
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FileUploadInput extends FileUpload
{

   private FileSelectedHandler fileSelectedHandler;

   public FileUploadInput()
   {
      setName(FormFields.FILE);
      addStyleName("UploadFile-FileSelect");

      ((InputElement)getElement().cast()).setSize(1);
      sinkEvents(Event.ONCHANGE);
   }

   public FileUploadInput(FileSelectedHandler fileSelectedHandler)
   {
      this();
      this.fileSelectedHandler = fileSelectedHandler;
   }

   public void setFileSelectedHandler(FileSelectedHandler fileSelectedHandler)
   {
      this.fileSelectedHandler = fileSelectedHandler;
   }

   public void onBrowserEvent(Event event)
   {
      String fileName = getFilename();

      if (fileName == null || fileName.trim().length() == 0)
      {
         return;
      }

      if (fileSelectedHandler != null)
      {
         fileSelectedHandler.onFileSelected(fileName);
      }
   }

}
