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
package org.exoplatform.ide.client.module.development.control;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.form.FormClosedEvent;
import org.exoplatform.ide.client.framework.form.FormClosedHandler;
import org.exoplatform.ide.client.framework.form.FormOpenedEvent;
import org.exoplatform.ide.client.framework.form.FormOpenedHandler;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsSavedHandler;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.outline.CodeHelperForm;
import org.exoplatform.ide.client.outline.OutlineTreeGrid;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class ShowOutlineControl extends IDEControl implements EditorActiveFileChangedHandler,
   ApplicationSettingsSavedHandler, FormOpenedHandler, FormClosedHandler, ApplicationSettingsReceivedHandler
{

   public static final String ID = "View/Show \\ Hide Outline";

   public static final String TITLE = "Outline";

   public static final String PROMPT_SHOW = "Show Outline";

   public static final String PROMPT_HIDE = "Hide Outline";

   private static final String COOKIE_OUTLINE = "outline";

   private boolean showOutLine = false;

   private boolean outLineFormOpened = false;

   public ShowOutlineControl(HandlerManager eventBus)
   {
      super(ID, eventBus);
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
      addHandler(ApplicationSettingsSavedEvent.TYPE, this);
      addHandler(FormOpenedEvent.TYPE, this);
      addHandler(FormClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null || !event.getEditor().canCreateTokenList())
      {
         setVisible(false);
         return;
      }

      boolean visible = OutlineTreeGrid.haveOutline(event.getFile());
      setVisible(visible);
      if (visible)
      {
         update();
      }
   }

   private void update()
   {
      setSelected(outLineFormOpened);

      if (outLineFormOpened)
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

   public void onFormOpened(FormOpenedEvent event)
   {
      if (CodeHelperForm.ID.equals(event.getFormId()))
      {
         outLineFormOpened = true;
         update();
      }
   }

   public void onFormClosed(FormClosedEvent event)
   {
      if (CodeHelperForm.ID.equals(event.getFormId()))
      {
         outLineFormOpened = false;
         update();
      }
   }

   public void onApplicationSettingsSaved(ApplicationSettingsSavedEvent event)
   {
      if (event.getApplicationSettings().getValue(COOKIE_OUTLINE) != null)
      {
         showOutLine = (Boolean)event.getApplicationSettings().getValue(COOKIE_OUTLINE);
      }
      else
      {
         showOutLine = true;
      }
      update();
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
   }

}
