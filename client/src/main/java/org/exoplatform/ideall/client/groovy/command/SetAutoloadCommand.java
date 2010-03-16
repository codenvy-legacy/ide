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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.groovy.GroovyPropertyUtil;
import org.exoplatform.ideall.client.groovy.event.SetAutoloadEvent;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SetAutoloadCommand extends IDECommand implements EditorActiveFileChangedHandler,
   ItemPropertiesSavedHandler
{

   private static final String ID = "Run/Set Autoload";

   private static final String TITLE_SET = "Set Autoload";
   
   private static final String PROMPT_SET = "Set Groovy Script Autoload";

   private static final String TITLE_UNSET = "Unset Autoload";
   
   private static final String PROMPT_UNSET = "Unset Groovy Script Autoload";


   public SetAutoloadCommand()
   {
      super(ID);
      setTitle(TITLE_SET);
      setPrompt(TITLE_SET);
      setIcon(Images.MainMenu.SET_AUTOLOAD);
      //setEvent(new AutoloadEvent());
   }

   /**
    * Initializing handlers
    * 
    * @see org.exoplatform.ideall.client.application.component.IDECommand#onRegisterHandlers()
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
         hideAutoload();
         return;
      }

      if (!MimeType.SCRIPT_GROOVY.equals(event.getFile().getContentType()))
      {
         hideAutoload();
         return;
      }

      if (event.getFile().isNewFile())
      {
         setVisible(true);
         setEnabled(false);
         return;
      }

      checkEnablingFor(event.getFile());
   }

   /**
    * Enabling / disabling Autoloading
    * 
    * @param file
    */
   private void checkEnablingFor(File file)
   {
      enableAutoload();

      String autoloadPropertyValue = GroovyPropertyUtil.getAutoloadPropertyValue(file);
      if (autoloadPropertyValue == null || Boolean.parseBoolean(autoloadPropertyValue) != true)
      {
         // is set autoload
         setTitle(TITLE_SET);
         setPrompt(PROMPT_SET);
         setIcon(Images.MainMenu.SET_AUTOLOAD);
         setEvent(new SetAutoloadEvent(true));
      }
      else
      {
         // is unset autoload
         setTitle(TITLE_UNSET);
         setPrompt(PROMPT_UNSET+" - <br />"+file.getName());
         setIcon(Images.MainMenu.UNSET_AUTOLOAD);
         setEvent(new SetAutoloadEvent(false));
      }
   }

   /**
    * Enabling autoload
    */
   private void enableAutoload()
   {
      setVisible(true);
      setEnabled(true);
   }

   /**
    * Disabling autoload
    */
   private void hideAutoload()
   {
      setVisible(false);
      setEnabled(false);
   }

   /**
    * Handling item properties saved event
    * 
    * @see org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedHandler#onItemPropertiesSaved(org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedEvent)
    */
   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
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
