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
package org.exoplatform.ide;

import org.exoplatform.ide.core.CodeAssistant;
import org.exoplatform.ide.core.Dialogs;
import org.exoplatform.ide.core.Editor;
import org.exoplatform.ide.core.Menu;
import org.exoplatform.ide.core.Navigator;
import org.exoplatform.ide.core.Outline;
import org.exoplatform.ide.core.Perspective;
import org.exoplatform.ide.core.Toolbar;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDE
{

   private Menu menu;

   private Toolbar toolbar;

   private Editor editor;

   private Outline outline;

   private Dialogs dialogs;

   private Navigator navigator;

   private Perspective perspective;
   
   private CodeAssistant codeAssistant;

   public IDE(Selenium selenium)
   {
      menu = new Menu(selenium);
      toolbar = new Toolbar(selenium);
      editor = new Editor(selenium, this);
      outline = new Outline(selenium);
      dialogs = new Dialogs(selenium);
      navigator = new Navigator(selenium, this);
      perspective = new Perspective(selenium);
      codeAssistant = new CodeAssistant(selenium);
   }

   public Menu menu()
   {
      return menu;
   }

   public Toolbar toolbar()
   {
      return toolbar;
   }

   public Editor editor()
   {
      return editor;
   }

   /**
    * Get the code outline.
    * 
    * @return {@link Outline}
    */
   public Outline outline()
   {
      return outline;
   }

   public Dialogs dialogs()
   {
      return dialogs;
   }

   /**
    * Get the navigator element.
    * 
    * @return {@link Navigator}
    */
   public Navigator navigator()
   {
      return navigator;
   }

   public Perspective perspective()
   {
      return perspective;
   }

   /**
    * Get code assistant 
    * @return {@link CodeAssistant}
    */
   public CodeAssistant codeAssistant()
   {
      return codeAssistant;
   }
}
