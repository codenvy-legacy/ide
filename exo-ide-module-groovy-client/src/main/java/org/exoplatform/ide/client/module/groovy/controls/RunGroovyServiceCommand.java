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
package org.exoplatform.ide.client.module.groovy.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.groovy.Images;
import org.exoplatform.ide.client.module.groovy.event.RunGroovyServiceEvent;
import org.exoplatform.ide.client.module.vfs.api.Version;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RunGroovyServiceCommand extends SimpleControl implements EditorActiveFileChangedHandler
{

   private static final String ID = "Run/Run Groovy Service...";

   public RunGroovyServiceCommand(HandlerManager eventBus)
   {
      super(ID);
      setTitle("Run Groovy Service...");
      setPrompt("Run Groovy Service...");
      setIcon(Images.Controls.RUN_GROOVY_SERVICE);
      setEvent(new RunGroovyServiceEvent());
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || !MimeType.GROOVY_SERVICE.equals(event.getFile().getContentType())
         || (event.getFile() instanceof Version))
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

      setVisible(true);
      setEnabled(true);
   }

}
