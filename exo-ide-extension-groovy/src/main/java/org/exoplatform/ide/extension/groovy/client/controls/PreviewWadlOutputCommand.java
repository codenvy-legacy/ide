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
package org.exoplatform.ide.extension.groovy.client.controls;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.extension.groovy.client.Images;
import org.exoplatform.ide.extension.groovy.client.event.PreviewWadlOutputEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators"})
public class PreviewWadlOutputCommand extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{
   private static final String ID = "Run/Launch REST Service";

   public PreviewWadlOutputCommand()
   {
      super(ID);
      setTitle("Launch REST Service...");
      setPrompt("Launch REST Service...");
      setIcon(Images.Controls.OUTPUT);
      //setImages(GroovyPluginImageBundle.INSTANCE.groovyOutput(), GroovyPluginImageBundle.INSTANCE.groovyOutputDisabled());
      setEvent(new PreviewWadlOutputEvent());
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
      if (event.getFile() == null || (event.getFile().isVersion()))
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

      if (MimeType.GROOVY_SERVICE.equals(event.getFile().getMimeType()))
      {
         if (!event.getFile().isPersisted())
         {
            setEnabled(false);
            setVisible(true);
            return;
         }
         else
         {
            setVisible(true);
            setEnabled(true);
            return;
         }
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }

   }

}
