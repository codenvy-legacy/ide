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
package org.exoplatform.ide.extension.netvibes.client.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.extension.netvibes.client.NetvibesClientBundle;
import org.exoplatform.ide.extension.netvibes.client.event.PreviewNetvibesEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class ShowNetvibesPreviewControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{

   public static final String ID = "Run/Show Netwibes Preview";

   public static final String TITLE = "Show Netwibes Preview";

   public ShowNetvibesPreviewControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(NetvibesClientBundle.INSTANCE.preview(), NetvibesClientBundle.INSTANCE.previewDisabled());
      setEvent(new PreviewNetvibesEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (MimeType.UWA_WIDGET.equals(event.getFile().getMimeType()))
      {
         setVisible(true);
         if (!event.getFile().isPersisted())
         {
            setEnabled(false);
         }
         else
         {
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
