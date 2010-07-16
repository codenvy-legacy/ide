/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.module.edit.control;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.module.edit.event.FormatFileEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FormatSourceCommand extends IDEControl implements EditorActiveFileChangedHandler
{

   private static final String ID = "Edit/Format";

   private static final String TITLE = "Format";

   public FormatSourceCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(IDEImageBundle.INSTANCE.format(), IDEImageBundle.INSTANCE.formatDisabled());
      setEvent(new FormatFileEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (event.getEditor().canFormatSource())
      {
         if (MimeType.TEXT_PLAIN.equals(event.getFile().getContentType()))
         {
            setVisible(false);
            setEnabled(false);
         }
         else
         {
            setVisible(true);
            setEnabled(true);
         }
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }
   }

}
