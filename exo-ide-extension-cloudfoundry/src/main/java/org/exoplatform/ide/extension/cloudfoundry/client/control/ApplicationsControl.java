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
package org.exoplatform.ide.extension.cloudfoundry.client.control;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.apps.ShowApplicationsEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 18, 2011 evgen $
 *
 */
public class ApplicationsControl extends AbstractCloudFoundryControl
{

   /**
    * 
    */
   public ApplicationsControl()
   {
      super(CloudFoundryExtension.LOCALIZATION_CONSTANT.appsControlId());
      setTitle(CloudFoundryExtension.LOCALIZATION_CONSTANT.appsControlTitle());
      setPrompt(CloudFoundryExtension.LOCALIZATION_CONSTANT.appsControlPrompt());
      setEvent(new ShowApplicationsEvent());
      setImages(CloudFoundryClientBundle.INSTANCE.appsList(), CloudFoundryClientBundle.INSTANCE.appsListDisabled());
   }

}
