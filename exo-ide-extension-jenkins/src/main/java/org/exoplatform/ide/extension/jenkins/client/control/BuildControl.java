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
package org.exoplatform.ide.extension.jenkins.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BuildControl extends SimpleControl implements IDEControl
{

   /**
    * @param id
    */
   public BuildControl()
   {
      super(JenkinsExtension.MESSAGES.buildJavaAppId());
      setTitle(JenkinsExtension.MESSAGES.buildJavaAppTitle());
      setPrompt(JenkinsExtension.MESSAGES.buildJavaAppPrompt());
      setImages(JenkinsExtension.RESOURCES.build(), JenkinsExtension.RESOURCES.build_Disabled());
      setEvent(new BuildApplicationEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      //TODO enable only if Java project selected
      setEnabled(true);
   }

}
