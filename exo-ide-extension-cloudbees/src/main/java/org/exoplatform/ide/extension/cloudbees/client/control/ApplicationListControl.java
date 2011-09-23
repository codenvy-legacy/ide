/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudbees.client.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.list.ShowApplicationListEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 21, 2011 evgen $
 *
 */
public class ApplicationListControl extends SimpleControl implements IDEControl
{

   /**
    * 
    */
   public ApplicationListControl()
   {
      super(CloudBeesExtension.LOCALIZATION_CONSTANT.controlAppListId());
      setTitle(CloudBeesExtension.LOCALIZATION_CONSTANT.controlAppListTitle());
      setPrompt(CloudBeesExtension.LOCALIZATION_CONSTANT.controlAppListPrompt());
      setImages(CloudBeesClientBundle.INSTANCE.appList(), CloudBeesClientBundle.INSTANCE.appListDisabled());
      setEnabled(true);
      setVisible(true);
      setEvent(new ShowApplicationListEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
   }

}
