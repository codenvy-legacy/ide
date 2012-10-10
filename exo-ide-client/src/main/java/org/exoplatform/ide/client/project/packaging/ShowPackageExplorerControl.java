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
package org.exoplatform.ide.client.project.packaging;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
@RolesAllowed({"administrators", "developers"})
public class ShowPackageExplorerControl extends SimpleControl implements IDEControl, VfsChangedHandler
{
   
   public static final String ID = "Window/Show View/Package Explorer";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.packageExplorerControlTitle();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.packageExplorerControlPrompt();

   public ShowPackageExplorerControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.packageExplorer(), IDEImageBundle.INSTANCE.packageExplorerDisabled());
      setEvent(new ShowPackageExplorerEvent());      
   }

   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);      
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      if (event.getVfsInfo() != null)
      {
         setEnabled(true);
         setVisible(true);
      }
      else
      {
         setEnabled(false);
         setVisible(false);
      }
   }

}
