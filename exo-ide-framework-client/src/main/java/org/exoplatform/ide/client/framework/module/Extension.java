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
package org.exoplatform.ide.client.framework.module;

import org.exoplatform.gwtframework.ui.client.command.Control;

import com.google.gwt.core.client.EntryPoint;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public abstract class Extension implements EntryPoint
{

   /**
    * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
    */
   @Override
   public void onModuleLoad()
   {
      IDE.registerExtension(this);
   }

   /**
    * This method called after IDE initialized. In this method you can add controls and views.<br>
    * To add {@link Control} call
    * {@link IDE#addControl(Control, org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget, boolean)}<br>
    * To add Editor call {@link IDE#addEditor(org.exoplatform.ide.editor.api.EditorProducer)}
    */
   public abstract void initialize();

}
