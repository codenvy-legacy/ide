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
package org.exoplatform.ide.extension.aws.client;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 9:51:41 AM anya $
 * 
 */
public interface AWSLocalizationConstant extends com.google.gwt.i18n.client.Messages
{
   @Key("button.create")
   String createButton();

   @Key("button.cancel")
   String cancelButton();

   @Key("button.ok")
   String okButton();

   @Key("button.rename")
   String renameButton();

   @Key("button.deploy")
   String deployButton();

   @Key("button.delete")
   String deleteButton();

   @Key("button.stop")
   String stopButton();

   @Key("button.close")
   String closeButton();

   @Key("button.add")
   String addButton();

   @Key("button.next")
   String nextButton();

   @Key("button.back")
   String backButton();

   @Key("button.finish")
   String finishButton();

   @Key("button.update")
   String updateButton();

   @Key("button.create.version")
   String createVersionButton();

   @Key("button.launch.environment")
   String launchEnvironmentButton();

   // Messages
   @Key("creatingProject")
   String creatingProject();

   // Controls
   @Key("button.properties")
   String propertiesButton();

   @Key("button.upload")
   String uploadButton();

   @Key("button.refresh")
   String refreshButton();

   //Controls
   @Key("control.beanstalk.id")
   String beanstalkControlId();

   @Key("control.beanstalk.title")
   String beanstalkControlTitle();

   @Key("control.beanstalk.prompt")
   String beanstalkControlPrompt();

   @Key("control.createApp.id")
   String createApplicationControlId();

   @Key("control.createApp.title")
   String createApplicationControlTitle();

   @Key("control.createApp.prompt")
   String createApplicationControlPrompt();

   @Key("control.deleteApp.id")
   String deleteApplicationControlId();

   @Key("control.deleteApp.title")
   String deleteApplicationControlTitle();

   @Key("control.deleteApp.prompt")
   String deleteApplicationControlPrompt();

   @Key("control.login.id")
   String loginControlId();

   @Key("control.login.title")
   String loginControlTitle();

   @Key("control.login.prompt")
   String loginControlPrompt();

   @Key("control.manage.application.id")
   String manageApplicationControlId();

   @Key("control.manage.application.title")
   String manageApplicationControlTitle();

   @Key("control.manage.application.prompt")
   String manageApplicationControlPrompt();

   @Key("control.ec2.management.id")
   String ec2ManagementControlId();

   @Key("control.ec2.management.title")
   String ec2ManagementControlTitle();

   @Key("control.ec2.management.promt")
   String ec2ManagementControlPrompt();

   // Login View
   @Key("login.title")
   String loginTitle();

   @Key("login.field.access.key")
   String loginAccessKeyField();

   @Key("login.field.secret.key")
   String loginSecretKeyField();

   @Key("login.error.invalidKeyValue")
   String loginErrorInvalidKeyValue();

   @Key("login.success")
   String loginSuccess();

   // Create Application
   @Key("create.application.view.title")
   String createApplicationViewTitle();

   @Key("create.application.name.field")
   String createApplicationNameField();

   @Key("create.application.description.field")
   String createApplicationDescriptionField();

   @Key("create.application.s3.bucket.field")
   String createApplicationS3BucketField();

   @Key("create.application.s3.key.field")
   String createApplicationS3KeyField();

   @Key("create.application.s3.title")
   String createApplicationS3Title();

   @Key("create.environment.name")
   String createEnvironmentName();

   @Key("create.environment.description")
   String createEnvironmentDescription();

   @Key("create.environment.solution.stack")
   String createEnvironmentSolutionStack();

   @Key("create.environment.launch")
   String createEnvironmentLaunch();

   @Key("create.environment.launching")
   String createEnvironmentLaunching(String environment);

   @Key("create.application.success")
   String createApplicationSuccess(String application);

   @Key("create.application.fail")
   String createApplicationFailed(String application);

   @Key("create.application.started")
   String createApplicationStartedOnUrl(String name, String url);

   @Key("create.application.terminated")
   String createApplicationTerminated();

   @Key("create.environment.success")
   String createEnvironmentSuccess(String environment);

   @Key("create.environment.fail")
   String createEnvironmentFailed(String environment);

   // Manage Application
   @Key("not.aws.application.message")
   String notAWSApplictaionMessage();

   @Key("manage.application.view.title")
   String manageApplicationViewTitle();

   @Key("application.name")
   String applicationName();

   @Key("environment.url")
   String environmentUrl();

   @Key("application.description")
   String applicationDescription();

   @Key("application.creation.date")
   String applicationCreationDate();

   @Key("application.updated.date")
   String applicationUpdatedDate();

   @Key("application.actions")
   String applicationActions();

   @Key("edit.description.prompt")
   String editDescriptionPrompt();

   @Key("delete.application.prompt")
   String deleteApplicationPrompt();

   @Key("general.tab")
   String generalTab();

   @Key("versions.tab")
   String versionsTab();

   @Key("events.tab")
   String eventsTab();

   @Key("environments.tab")
   String environmentsTab();

   // Delete Application
   @Key("delete.application.title")
   String deleteApplicationTitle();

   @Key("delete.application.question")
   String deleteApplicationQuestion(String application);

   @Key("delete.application.success")
   String deleteApplicationSuccess(String application);

   @Key("delete.application.failed")
   String deleteApplicationFailed(String application);

   // Update Application
   @Key("update.application.failed")
   String updateApplicationFailed(String application);

   @Key("update.description.field")
   String updateDescriptionField();

   @Key("update.description.view.title")
   String updateDescriptionViewTitle();

   // Versions
   @Key("versions.grid.label")
   String versionsGridLabel();

   @Key("versions.grid.description")
   String versionsGridDescription();

   @Key("versions.grid.location")
   String versionsGridLocation();

   @Key("versions.grid.created")
   String versionsGridCreated();

   @Key("versions.grid.updated")
   String versionsGridUpdated();

   // Delete Version
   @Key("delete.version.view.title")
   String deleteVersionViewTitle();

   @Key("delete.version.question")
   String deleteVersionQuestion(String version);

   @Key("delete.s3.bundle")
   String deleteS3Bundle();

   @Key("delete.version.failed")
   String deleteVersionFailed(String version);

   // Deploy Version
   @Key("deploy.version.view.title")
   String deployVersionViewTitle();

   @Key("deploy.version.failed")
   String deployVersionFailed(String version);

   @Key("deploy.version.newenvironment")
   String deployVersionToNewEnvironment();

   @Key("deploy.version.existingenvironment")
   String deployVersionToExistingEnvironment();

   // Create Version
   @Key("create.version.failed")
   String createVersionFailed(String version);

   @Key("create.version.view.title")
   String createVersionViewTitle();

   @Key("create.version.name.field")
   String createVersionNameField();

   @Key("create.version.description.field")
   String createVersionDescriptionField();

   @Key("create.environment.view.title")
   String createEnvironmentViewTitle();

   // Terminate Environment
   @Key("stop.environment.question")
   String stopEnvironmentQuestion(String environment);

   @Key("stop.environment.failed")
   String stopEnvironmentFailed(String version);

   // S3 Management
   @Key("s3.managemnt.view.title")
   String s3managemntViewTitle();

   @Key("control.s3.managemnt.id")
   String s3ManagementControlId();

   @Key("control.s3.managemnt.title")
   String s3ManagementControlTitle();

   @Key("control.s3.managemnt.promt")
   String s3ManagementControlPrompt();

   // EC2 Management View
   @Key("management.ec2.id")
   String managementEC2ViewTitle();
}
