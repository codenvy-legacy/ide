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
package org.exoplatform.ide.extension.googleappengine.client;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public interface GAELocalization extends com.google.gwt.i18n.client.Messages
{
   @Key("google.app.engine.control")
   String googleAppEngineControl();

   /* Buttons */
   @Key("ok.button")
   String okButton();

   @Key("cancel.button")
   String cancelButton();

   @Key("login.button")
   String loginButton();

   @Key("ready.button")
   String readyButton();

   /* Create application */
   @Key("create.application.control.title")
   String createApplicationControlTitle();

   @Key("create.application.control.prompt")
   String createApplicationControlPrompt();

   @Key("create.application.view.title")
   String createApplicationViewTitle();

   @Key("create.application.instruction")
   String createApplicationInstruction();

   @Key("create.application.logged.note")
   String createApplicationLoggedNote();

   @Key("create.application.deploy.field")
   String createApplicationDeployField();

   @Key("create.application.cannot.deploy")
   String createApplicationCannotDeploy();

   /* Deploy application */
   @Key("deploy.application.control.title")
   String deployApplicationControlTitle();

   @Key("deploy.application.control.prompt")
   String deployApplicationControlPrompt();

   @Key("deploy.application.success")
   String deployApplicationSuccess(String project);

   @Key("deploy.application.started")
   String deployApplicationStarted(String projectName);

   @Key("deploy.application.finished")
   String deployApplicationFinished(String projectName);

   /* Rollback update */
   @Key("rollback.update.control.title")
   String rollbackUpdateControlTitle();

   @Key("rollback.update.control.prompt")
   String rollbackUpdateControlPrompt();

   @Key("rollback.update.success")
   String rollbackUpdateSuccess();

   /* Log in */
   @Key("login.view.title")
   String loginViewTitle();

   @Key("email.field.title")
   String emailFieldTitle();

   @Key("password.field.title")
   String passwordFieldTitle();

   @Key("login.failed.message")
   String loginFailedMessage();
}