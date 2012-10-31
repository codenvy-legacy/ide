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
package org.exoplatform.ide.extension.appfog.client.control;

import org.exoplatform.ide.extension.appfog.client.AppfogClientBundle;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.update.UpdateInstancesEvent;

/**
 * Control to update number of instances of application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UpdateInstancesControl extends AbstractAppfogControl
{

   private static final String ID = AppfogExtension.LOCALIZATION_CONSTANT.updateInstancesControlId();

   private static final String TITLE = AppfogExtension.LOCALIZATION_CONSTANT.updateInstancesControlTitle();

   private static final String PROMPT = AppfogExtension.LOCALIZATION_CONSTANT.updateInstancesControlPrompt();

   public UpdateInstancesControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(AppfogClientBundle.INSTANCE.appInstances(),
         AppfogClientBundle.INSTANCE.appInstancesDisabled());
      setEvent(new UpdateInstancesEvent());
   }
}
