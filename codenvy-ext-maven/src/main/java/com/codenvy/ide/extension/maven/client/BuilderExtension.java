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
package com.codenvy.ide.extension.maven.client;

import com.codenvy.ide.extension.Extension;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.codenvy.ide.extension.maven.client.BuilderAutoBeanFactory;
import com.codenvy.ide.extension.maven.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.maven.client.build.BuildProjectPresenter;

/**
 * Maven builder extension entry point.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderExtension.java Feb 21, 2012 1:53:48 PM azatsarynnyy $
 * 
 */
@Singleton
@Extension(title = "Maven Support.", version = "2.0.0")
public class BuilderExtension
{
   public static final BuilderAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(BuilderAutoBeanFactory.class);

   /** Channel for the messages containing status of the Maven build job. */
   public static final String BUILD_STATUS_CHANNEL = "maven:buildStatus:";

   /**
    * Localization constants.
    */
   public static final BuilderLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(BuilderLocalizationConstant.class);

   @Inject
   public BuilderExtension(BuildProjectPresenter buildProjectPresenter)
   {

   }
}