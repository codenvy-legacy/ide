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
package org.exoplatform.ide.client.module.groovy.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.groovy.Images;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.client.framework.vfs.Version;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developers"})
public class UndeployGroovySandboxCommand extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{

   public static final String ID = "Run/Undeploy from Sandbox";

   public UndeployGroovySandboxCommand(HandlerManager eventBus)
   {
      super(ID);
      setTitle("Undeploy from Sandbox");
      setPrompt("Undeploy REST Service from Sandbox");
      setIcon(Images.Controls.UNDEPLOY_SANDBOX);
      //setImages(GroovyPluginImageBundle.INSTANCE.undeployGroovy(), GroovyPluginImageBundle.INSTANCE.undeployGroovyDisabled());
      setEvent(new UndeployGroovyScriptSandboxEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }
   
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || (event.getFile() instanceof Version))
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

      if (MimeType.GROOVY_SERVICE.equals(event.getFile().getContentType()))
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
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }
   }
}
