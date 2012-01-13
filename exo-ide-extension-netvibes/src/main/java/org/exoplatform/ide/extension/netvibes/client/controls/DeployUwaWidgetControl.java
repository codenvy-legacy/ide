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
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.netvibes.client.Images;
import org.exoplatform.ide.extension.netvibes.client.event.DeployUwaWidgetEvent;

/**
 * Control for deploying UWA widgets to Netvibes Ecosystem.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 29, 2010 $
 * 
 */
public class DeployUwaWidgetControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{

   /**
    * Control id.
    */
   public static final String ID = "Run/Deploy widget";

   /**
    * Control's prompt, when user hovers the mouse on it.
    */
   public static final String PROMPT = "Deploy UWA widget to Ecosystem";

   /**
    * Control's title.
    */
   public static final String TITLE = "Deploy UWA widget";

   /**
    * Default constructor.
    */
   public DeployUwaWidgetControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setIcon(Images.Controls.DEPLOY_WIDGET);
      setEvent(new DeployUwaWidgetEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setEnabled(false);
         setVisible(false);
         return;
      }
      // Visible if file MIME type is "application/x-uwa-widget":
      boolean isVisible = MimeType.UWA_WIDGET.equals(event.getFile().getMimeType());
      // Enabled if file is saved:
      boolean isEnabled = (isVisible && event.getFile().isPersisted());
      setVisible(isVisible);
      setEnabled(isEnabled);
   }

}
