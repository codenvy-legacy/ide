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

import com.google.gwt.i18n.client.Messages;

/**
 * Jenkins extension messages constants.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface JenkinsMessages extends Messages
{

   @Key("control.build.javaApp.id")
   String buildJavaAppId();

   @Key("control.build.javaApp.title")
   String buildJavaAppTitle();

   @Key("control.build.javaApp.prompt")
   String buildJavaAppPrompt();
   
   @Key("view.build.javaApp.title")
   String buildJavaAppViewTitle();
   
   @Key("view.build.javaApp.button.cancel")
   String buildJavaAppButtonCancel();
   
   @Key("view.build.javaApp.button.build")
   String buildJavaAppButtonBuild();
   
   @Key("view.build.javaApp.git.url")
   String buildJavaAppGitRepositoryUrl();
   
   @Key("controller.no.remote.repository")
   String noRemoteRepository();
   
   @Key("controller.no.git.repository")
   String noGitReposytory();
   
   @Key("controller.start.build")
   String buildStarted(String projectName);
   
   @Key("control.status.start")
   String statusControlStart();
   
   @Key("controller.build.result.title")
   String buildResultTitle();
   
   @Key("controller.build.result.message")
   String buildResultMessage(String projectName, String buildResult);
}
