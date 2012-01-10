/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.editor.java.client.create;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.editor.java.client.CreateJavaClassEvent;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.vfs.client.model.ItemContext;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Jan 10, 2012 2:05:22 PM anya $
 *
 */
public class NewJavaClassControl extends SimpleControl implements IDEControl, ItemsSelectedHandler
{
   public NewJavaClassControl()
   {
      super("File/New/New Java Class");
      setTitle("Java Class");
      setPrompt("Create Java Class");
      setNormalImage(JavaClientBundle.INSTANCE.newClassWizz());
      setDisabledImage(JavaClientBundle.INSTANCE.newClassWizzDisabled());
      setEvent(new CreateJavaClassEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1 && event.getSelectedItems().get(0) instanceof ItemContext)
      {
         boolean enabled = ((ItemContext)event.getSelectedItems().get(0)).getProject() != null;
         setEnabled(enabled);
      }
      else
      {
         setEnabled(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }
}
