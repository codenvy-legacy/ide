/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.module.groovy.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.module.groovy.Images;
import org.exoplatform.ideall.client.module.groovy.event.PreviewWadlOutputEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PreviewWadlOutputCommand extends IDEControl implements EditorActiveFileChangedHandler
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

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

      if (MimeType.SCRIPT_GROOVY.equals(event.getFile().getContentType())
         || MimeType.APPLICATION_GROOVY.equals(event.getFile().getContentType()))
      {
         if (event.getFile().isNewFile())
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
