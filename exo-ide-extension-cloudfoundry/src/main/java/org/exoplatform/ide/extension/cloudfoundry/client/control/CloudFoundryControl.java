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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Control for submenu for CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryControl.java Jul 8, 2011 3:25:33 PM vereshchaka $
 *
 */
public class CloudFoundryControl extends SimpleControl implements IDEControl
{
   
   private static final String ID = CloudFoundryExtension.LOCALIZATION_CONSTANT.cloudFoundryControlId();
   
   private static final String TITLE = CloudFoundryExtension.LOCALIZATION_CONSTANT.cloudFoundryControlTitle();
   
   private static final String PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.cloudFoundryControlPrompt();

   /**
    * @param id
    */
   public CloudFoundryControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(CloudFoundryClientBundle.INSTANCE.cloudFoundry(), CloudFoundryClientBundle.INSTANCE.cloudFoundryDisabled());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      setVisible(true);
      setEnabled(true);
   }

}
