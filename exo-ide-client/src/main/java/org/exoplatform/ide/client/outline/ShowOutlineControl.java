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
package org.exoplatform.ide.client.outline;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.outline.OutlineDisplay;
import org.exoplatform.ide.client.framework.outline.ShowOutlineEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.editor.client.api.EditorCapability;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
@RolesAllowed({"developer"})
public class ShowOutlineControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   ViewClosedHandler, ViewOpenedHandler
{

   public static final String ID = "View/Show \\ Hide Outline";

   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outlineTitleControl();

   public static final String PROMPT_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.outlinePromptShowControl();

   public static final String PROMPT_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.outlinePromptHideControl();

   private boolean outlineViewOpened = false;

   private List<String> ignoredMimeTypes = new ArrayList<String>();

   /**
    * 
    */
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

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ViewOpenedEvent.TYPE, this);

      ignoredMimeTypes.add(MimeType.TEXT_HTML);
      ignoredMimeTypes.add(MimeType.TEXT_CSS);
      ignoredMimeTypes.add(MimeType.TEXT_JAVASCRIPT);
      ignoredMimeTypes.add(MimeType.APPLICATION_JAVASCRIPT);
      ignoredMimeTypes.add(MimeType.APPLICATION_X_JAVASCRIPT);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         return;
      }

      boolean visible = event.getEditor().isCapable(EditorCapability.OUTLINE);

      // TODO add possibility to configure editor's capabilities 
      if (ignoredMimeTypes.contains(event.getFile().getMimeType()))
      {
         visible = false;
      }

      setVisible(visible);
      if (visible)
      {
         update();
      }
   }

   /**
    * 
    */
   private void update()
   {
      setSelected(outlineViewOpened);
      if (outlineViewOpened)
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
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof OutlineDisplay)
      {
         outlineViewOpened = true;
         update();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof OutlineDisplay)
      {
         outlineViewOpened = false;
         update();
      }
   }

}
