/**
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
 *
 */

package org.exoplatform.ide.client.module.chromattic.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.module.chromattic.Images;
import org.exoplatform.ide.client.module.chromattic.event.CompileGroovyEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@RolesAllowed({"administrators", "developers"})
public class CompileGroovyControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{
   
   /**
    * Control ID
    */
   public static final String ID = "Run/Compile";
   
   /**
    * 
    */
   public CompileGroovyControl() {
      super(ID);
      setTitle("Compile");
      setPrompt("Compile");
      setIcon(Images.Controls.COMPILE_GROOVY);
   }

   /**
    * Initialise control
    * 
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * Handling of changing currently edited file
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || (event.getFile() instanceof Version))
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

      String fileMimeType = event.getFile().getContentType();
      
      if (MimeType.APPLICATION_GROOVY.equals(fileMimeType))
      {
         setVisible(true);
         
         if (event.getFile().isNewFile())
         {
            setEnabled(false);
         }
         else
         {
            setEnabled(true);
         }
         
         setEvent(new CompileGroovyEvent(event.getFile()));
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }
   }

}
