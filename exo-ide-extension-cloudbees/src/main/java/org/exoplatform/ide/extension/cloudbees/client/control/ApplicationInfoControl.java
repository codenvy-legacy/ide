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

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoEvent;

/**
 * Control for showing information about application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationControl.java Jun 23, 2011 12:00:53 PM vereshchaka $
 */
public class ApplicationInfoControl extends SimpleControl implements IDEControl
{
   
   private static final String ID = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoControlId();
   
   private static final String TITLE = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoControlTitle();
   
   private static final String PROMPT = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoControlPrompt();
   
   public ApplicationInfoControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(CloudBeesClientBundle.INSTANCE.applicationInfo(), CloudBeesClientBundle.INSTANCE.applicationInfoDisabled());
      setEvent(new ApplicationInfoEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      setEnabled(true);
   }

}
