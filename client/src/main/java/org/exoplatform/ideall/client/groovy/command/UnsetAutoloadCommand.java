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
package org.exoplatform.ideall.client.groovy.command;

import org.exoplatform.gwt.commons.rest.MimeType;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.SimpleCommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.groovy.GroovyPropertyUtil;
import org.exoplatform.ideall.client.groovy.event.UnsetAutoloadEvent;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UnsetAutoloadCommand extends SimpleCommand implements EditorActiveFileChangedHandler,
   ItemPropertiesSavedHandler
{

   private static final String ID = "Run/Unset Autoload";

   private static final String TITLE = "Unset Groovy Script Autoload";

   public UnsetAutoloadCommand()
   {
      super(ID, TITLE, Images.MainMenu.UNSET_AUTOLOAD, new UnsetAutoloadEvent());
   }

   /**
    * Initializing handlers
    * 
    * @see org.exoplatform.ideall.client.application.component.SimpleCommand#onRegisterHandlers()
    */
   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(ItemPropertiesSavedEvent.TYPE, this);
   }

   /**
    * Handling event Editor active file changed
    *
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

      if (!MimeType.SCRIPT_GROOVY.equals(event.getFile().getContentType()))
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (event.getFile().isNewFile())
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      checkEnablingFor(event.getFile());

   }

   /**
    * Enabling / dosabling unset autoload
    * 
    * @param file
    */
   private void checkEnablingFor(File file)
   {
      String autoloadPropertyValue = GroovyPropertyUtil.getAutoloadPropertyValue(file);
      System.out.println("autoload [" + autoloadPropertyValue + "]");

      if (autoloadPropertyValue == null)
      {
         disableUnsetAutoload();
      }
      else
      {
         boolean autoload = Boolean.parseBoolean(autoloadPropertyValue);
         if (autoload)
         {
            enableUnsetAutoload();
         }
         else
         {
            disableUnsetAutoload();
         }
      }
   }

   /**
    * Enable unset autoload 
    */
   private void enableUnsetAutoload()
   {
      setVisible(true);
      setEnabled(true);
   }

   /**
    * Disable unset autoload
    */
   private void disableUnsetAutoload()
   {
      setVisible(false);
      setEnabled(false);
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      System.out.println("item properties saved!!!!!!!!!!!");
      if (!(event.getItem() instanceof File))
      {
         return;
      }

      if (context.getActiveFile() != (File)event.getItem())
      {
         return;
      }

      checkEnablingFor((File)event.getItem());
   }

}
