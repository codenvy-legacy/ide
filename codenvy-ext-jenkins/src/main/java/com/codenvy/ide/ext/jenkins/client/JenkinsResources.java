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
package com.codenvy.ide.ext.jenkins.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/** @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a> */
public interface JenkinsResources extends ClientBundle {
    @Source("com/codenvy/ide/ext/jenkins/images/build_Disabled.png")
    ImageResource build_Disabled();

    @Source("com/codenvy/ide/ext/jenkins/images/build.png")
    ImageResource build();

    @Source("com/codenvy/ide/ext/jenkins/images/blue_anime.gif")
    ImageResource blue_anime();

    @Source("com/codenvy/ide/ext/jenkins/images/blue.png")
    ImageResource blue();

    @Source("com/codenvy/ide/ext/jenkins/images/grey_anime.gif")
    ImageResource grey_anime();

    @Source("com/codenvy/ide/ext/jenkins/images/grey.png")
    ImageResource grey();

    @Source("com/codenvy/ide/ext/jenkins/images/red_anime.gif")
    ImageResource red_anime();

    @Source("com/codenvy/ide/ext/jenkins/images/red.png")
    ImageResource red();

    @Source("com/codenvy/ide/ext/jenkins/images/yellow.png")
    ImageResource yellow();

    @Source("com/codenvy/ide/ext/jenkins/images/transparent.png")
    ImageResource transparent();
}