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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.resources.client.ClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public interface JenkinsResourceBundle extends ClientBundle
{

   @Source("org/exoplatform/ide/extension/jenkins/client/images/build_Disabled.png")
   ImageResource build_Disabled();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/build.png")
   ImageResource build();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/blue_anime.gif")
   ImageResource blue_anime();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/blue.png")
   ImageResource blue();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/grey_anime.gif")
   ImageResource grey_anime();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/grey.png")
   ImageResource grey();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/red_anime.gif")
   ImageResource red_anime();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/red.png")
   ImageResource red();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/yellow.png")
   ImageResource yellow();

   @Source("org/exoplatform/ide/extension/jenkins/client/images/transparent.png")
   ImageResource transparent();

}
