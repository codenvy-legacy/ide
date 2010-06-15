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
package org.exoplatform.ideall.client.common.command.view;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.outline.event.ShowOutlineEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class ShowOutlineControl extends IDECommand implements EditorActiveFileChangedHandler
{

   public static final String ID = "View/Show Outline";

   public ShowOutlineControl()
   {
      super(ID);
      setTitle("Show Outline");
      setPrompt("Show Outline");
      setIcon(Images.Outline.OUTLINE);
      setEvent(new ShowOutlineEvent(true));
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);

   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (event.getFile().getContentType().equals(MimeType.APPLICATION_JAVASCRIPT)
               ||event.getFile().getContentType().equals(MimeType.GOOGLE_GADGET))
      {
         setVisible(true);
         setEnabled(true);
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }

   }

}
