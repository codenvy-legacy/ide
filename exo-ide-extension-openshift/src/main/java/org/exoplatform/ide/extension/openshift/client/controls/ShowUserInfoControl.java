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
package org.exoplatform.ide.extension.openshift.client.controls;

import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.user.ShowUserInfoEvent;

/**
 * Control is used for showing OpenShift user's information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 14, 2011 10:47:04 AM anya $
 *
 */
public class ShowUserInfoControl extends AbstractOpenShiftControl
{

   public ShowUserInfoControl()
   {
      super(OpenShiftExtension.LOCALIZATION_CONSTANT.showUserInfoControlId());
      setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.showUserInfoControlTitle());
      setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.showUserInfoControlPrompt());
      setImages(OpenShiftClientBundle.INSTANCE.userInfoControl(),
         OpenShiftClientBundle.INSTANCE.userInfoControlDisabled());
      setEvent(new ShowUserInfoEvent());
      setDelimiterBefore(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);

      setVisible(true);
   }

   /**
    * 
    */
   protected void refresh()
   {
      setEnabled(vfsInfo != null);
   }
}
