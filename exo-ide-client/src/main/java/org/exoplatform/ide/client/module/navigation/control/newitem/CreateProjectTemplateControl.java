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
package org.exoplatform.ide.client.module.navigation.control.newitem;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class CreateProjectTemplateControl extends SimpleControl implements IDEControl, EntryPointChangedHandler
{

   public final static String ID = "File/New/Project Template...";

   public CreateProjectTemplateControl()
   {
      super(ID);
      setTitle("Project Template...");
      setPrompt("Create Project Template...");
      setDelimiterBefore(true);
      setEnabled(true);
      setImages(IDEImageBundle.INSTANCE.createProjectTemplate(), IDEImageBundle.INSTANCE.createProjectTemplateDisabled());
      setEvent(new CreateProjectTemplateEvent());      
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler#onEntryPointChanged(org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent)
    */
   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      if (event.getEntryPoint() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }
   }

}
