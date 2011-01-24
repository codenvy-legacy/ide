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
package org.exoplatform.ide.client.module.development;

import org.exoplatform.ide.client.documentation.DocumentationPresenter;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.module.development.control.ShowOutlineControl;
import org.exoplatform.ide.client.module.development.control.ShowPreviewCommand;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class DevelopmentModule implements IDEModule
{

   public DevelopmentModule(HandlerManager eventBus)
   {
      eventBus.fireEvent(new RegisterControlEvent(new ShowOutlineControl(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new ShowPreviewCommand(), DockTarget.TOOLBAR, true));

      new DevelopmentModuleEventHandler(eventBus);
      
      new DocumentationPresenter(eventBus);
   }

}
