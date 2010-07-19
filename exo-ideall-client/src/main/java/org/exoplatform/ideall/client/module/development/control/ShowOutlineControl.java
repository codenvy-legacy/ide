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
package org.exoplatform.ideall.client.module.development.control;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.cookie.event.BrowserCookiesUpdatedEvent;
import org.exoplatform.ideall.client.cookie.event.BrowserCookiesUpdatedHandler;
import org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedEvent;
import org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedHandler;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ideall.client.outline.CodeHelperForm;
import org.exoplatform.ideall.client.outline.OutlineTreeGrid;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class ShowOutlineControl extends IDEControl implements EditorActiveFileChangedHandler,
   OpenedFormsStateChangedHandler, BrowserCookiesUpdatedHandler
{

   public static final String ID = "View/Show \\ Hide Outline";

   public static final String TITLE = "Outline";

   public static final String PROMPT_SHOW = "Show Outline";

   public static final String PROMPT_HIDE = "Hide Outline";

   public ShowOutlineControl()
   {
      super(ID);
      setTitle(TITLE);
      setImages(IDEImageBundle.INSTANCE.outline(), IDEImageBundle.INSTANCE.outlineDisabled());
      setEvent(new ShowOutlineEvent(true));
      setEnabled(true);
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(OpenedFormsStateChangedEvent.TYPE, this);
      addHandler(BrowserCookiesUpdatedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         return;
      }

      boolean visible = OutlineTreeGrid.haveOutline(event.getFile());
      setVisible(visible);
   }

   /**
    * @see org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedHandler#onOpenedFormsStateChanged(org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedEvent)
    */
   public void onOpenedFormsStateChanged(OpenedFormsStateChangedEvent event)
   {
      boolean isOpened = context.getOpenedForms().contains(CodeHelperForm.ID);

      setSelected(isOpened);

      if (isOpened)
      {
         setEvent(new ShowOutlineEvent(false));
      }
      else
      {
         setEvent(new ShowOutlineEvent(true));
      }

   }

   /**
    * Update the control state - change prompt and event parameter.
    */
   private void update()
   {
      if (context.isShowOutline())
      {
         setPrompt(PROMPT_HIDE);
         setEvent(new ShowOutlineEvent(false));
      }
      else
      {
         setPrompt(PROMPT_SHOW);
         setEvent(new ShowOutlineEvent(true));
      }
   }

   /**
    * @see org.exoplatform.ideall.client.cookie.event.BrowserCookiesUpdatedHandler#onBrowserCookiesUpdated(org.exoplatform.ideall.client.cookie.event.BrowserCookiesUpdatedEvent)
    */
   public void onBrowserCookiesUpdated(BrowserCookiesUpdatedEvent event)
   {
      update();
   }

}
