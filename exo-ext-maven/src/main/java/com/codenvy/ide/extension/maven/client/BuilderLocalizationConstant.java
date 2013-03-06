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

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants.
 * Interface to represent the constants contained in resource bundle: 'BuilderLocalizationConstant.properties'.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderLocalizationConstant.java Feb 21, 2012 3:06:29 PM azatsarynnyy $
 *
 */
public interface BuilderLocalizationConstant extends Messages
{
   /*
    * Controls
    */
   @Key("control.buildProject.id")
   String buildProjectControlId();

   @Key("control.buildProject.title")
   String buildProjectControlTitle();

   @Key("control.buildProject.prompt")
   String buildProjectControlPrompt();

   @Key("control.buildAndPublishProject.id")
   String buildAndPublishProjectControlId();
   
   @Key("control.buildAndPublishProject.title")
   String buildAndPublishProjectControlTitle();
   
   @Key("control.buildAndPublishProject.prompt")
   String buildAndPublishProjectControlPrompt();

   /*
    * Messages
    */
   @Key("messages.buildInProgress")
   String buildInProgress(String project);

   @Key("messages.buildSuccess")
   String buildSuccess();

   @Key("messages.buildFailed")
   String buildFailed();

   @Key("messages.selectedItemsFail")
   String selectedItemsFail();

   /*
    * BuildProjectView
    */
   @Key("buildProject.id")
   String buildProjectId();

   @Key("buildProject.title")
   String buildProjectTitle();

   @Key("buildProject.outputClear")
   String outputClear();

   /*
    * InitRequestHandler
    */
   @Key("build.started")
   String buildStarted(String project);

   @Key("build.finished")
   String buildFinished(String project);
}
