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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedEvent;
import org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedHandler;
import org.exoplatform.ideall.client.outline.CodeHelperForm;
import org.exoplatform.ideall.client.outline.event.ShowOutlineEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class ShowOutlineControl extends IDECommand implements EditorActiveFileChangedHandler, OpenedFormsStateChangedHandler
{

   public static final String ID = "View/Show Outline";

   public ShowOutlineControl()
   {
      super(ID);
      setTitle("Show Outline");
      setPrompt("Show Outline");
      setImages(IDEImageBundle.INSTANCE.outline(), IDEImageBundle.INSTANCE.outlineDisabled());
      setEvent(new ShowOutlineEvent(true));
      setEnabled(true);
      
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(OpenedFormsStateChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         //setEnabled(false);
         return;
      }

      boolean visible = (event.getFile().getContentType().equals(MimeType.APPLICATION_JAVASCRIPT)
               || event.getFile().getContentType().equals(MimeType.GOOGLE_GADGET));
      setVisible(visible);
      boolean isOpened = context.getOpenedForms().contains(CodeHelperForm.ID);
      boolean isEnabled = !isOpened && visible;
      //setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedHandler#onOpenedFormsStateChanged(org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedEvent)
    */
   public void onOpenedFormsStateChanged(OpenedFormsStateChangedEvent event)
   {  
      boolean isOpened = context.getOpenedForms().contains(CodeHelperForm.ID);
      //setEnabled(!isOpened);
   }

}
