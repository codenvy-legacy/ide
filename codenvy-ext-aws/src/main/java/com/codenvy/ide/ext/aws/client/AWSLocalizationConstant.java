/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.aws.client;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AWSLocalizationConstant extends com.google.gwt.i18n.client.Messages {
    // Buttons

    @Key("button.create.bucket")
    String createBucketButton();

    @Key("button.cancel")
    String cancelButton();

    @Key("button.ok")
    String okButton();

    @Key("button.deploy")
    String deployButton();

    @Key("button.delete")
    String deleteButton();

    @Key("button.launch")
    String launchButton();

    @Key("button.restart")
    String restartButton();

    @Key("button.rebuild")
    String rebuildButton();

    @Key("button.terminate")
    String terminateButton();

    @Key("button.close")
    String closeButton();

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

    @Key("unableGetEnvironmentInfo")
    String unableToGetEnvironmentInfo(String name);

    @Key("validationErrorSpecifyAppName")
    String validationErrorSpecifyAppName();

    @Key("validationErrorEnvNameLength")
    String validationErrorEnvNameLength();

    @Key("validationErrorEnvNameHyphen")
    String validationErrorEnvNameHyphen();

    @Key("seeOutputForLinkToLog")
    String seeOutputForLinkToLog();

    @Key("viewLogFromInstance")
    String viewLogFromInstance(String instanceId);

    @Key("logsPreparing")
    String logsPreparing();

    // Controls
    @Key("button.upload")
    String uploadButton();

    @Key("button.refresh")
    String refreshButton();

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

    @Key("launch.environment.name")
    String launchEnvironmentName();

    @Key("launch.environment.description")
    String launchEnvironmentDescription();

    @Key("launch.environment.solution.stack")
    String launchEnvironmentSolutionStack();

    @Key("launch.environment.versions")
    String launchEnvironmentVersions();

    @Key("launch.environment.launch")
    String launchEnvironmentLaunch();

    @Key("launch.environment.launching")
    String launchEnvironmentLaunching(String environment);

    @Key("create.application.success")
    String createApplicationSuccess(String application);

    @Key("create.application.fail")
    String createApplicationFailed(String application);

    @Key("create.application.started")
    String createApplicationStartedOnUrl(String name, String url);

    @Key("launch.environment.success")
    String launchEnvironmentSuccess(String environment);

    @Key("launch.environment.fail")
    String launchEnvironmentFailed(String environment);

    // Manage Application
    @Key("manage.application.view.title")
    String manageApplicationViewTitle();

    @Key("application.name")
    String applicationName();

    @Key("application.description")
    String applicationDescription();

    @Key("application.creation.date")
    String applicationCreationDate();

    @Key("application.updated.date")
    String applicationUpdatedDate();

    @Key("environment.configuration.title")
    String environmentConfigurationTitle();

    // Delete Application

    // Update Application
    @Key("update.application.failed")
    String updateApplicationFailed(String application);

    @Key("update.description.view.title")
    String updateDescriptionViewTitle();

    // Versions
    @Key("versions.grid.label")
    String versionsGridLabel();

    @Key("versions.grid.description")
    String versionsGridDescription();

    @Key("versions.grid.created")
    String versionsGridCreated();

    @Key("versions.grid.updated")
    String versionsGridUpdated();

    // Delete Version
    @Key("delete.version.view.title")
    String deleteVersionViewTitle();

    @Key("delete.version.question")
    String deleteVersionQuestion(String version);

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

    // Environments
    @Key("environments.grid.name")
    String environmentsGridName();

    @Key("environments.grid.stack")
    String environmentsGridStack();

    @Key("environments.grid.version")
    String environmentsGridVersion();

    @Key("environments.grid.status")
    String environmentsGridStatus();

    @Key("environments.grid.health")
    String environmentsGridHealth();

    @Key("environments.grid.url")
    String environmentsGridUrl();

    // Launch Environment
    @Key("launch.environment.view.title")
    String launchEnvironmentViewTitle();

    // Terminate Environment
    @Key("terminate.environment.view.title")
    String terminateEnvironmentViewTitle();

    @Key("terminate.environment.question")
    String terminateEnvironmentQuestion(String name);

    @Key("terminate.environment.success")
    String terminateEnvironmentSuccess(String name);

    // Rebuild Environment
    @Key("rebuild.environment.view.title")
    String rebuildEnvironmentViewTitle();

    @Key("rebuild.environment.question")
    String rebuildEnvironmentQuestion(String name);

    @Key("rebuild.environment.failed")
    String rebuildEnvironmentFailed(String name);

    // Get Logs
    @Key("logs.environment.failed")
    String logsEnvironmentFailed(String name);

    // Restart App Server
    @Key("restart.appserver.view.title")
    String restartAppServerViewTitle();

    @Key("restart.appserver.question")
    String restartAppServerQuestion(String name);

    // S3 Management
    @Key("control.s3.managemnt.title")
    String s3ManagementControlTitle();

    @Key("configuration.ec2.failed")
    String getEnvironmentConfigurationFailed();

    @Key("terminate.ec2.question")
    String terminateEC2InstanceQuestion(String instance);

    @Key("terminate.ec2.success")
    String terminateInstanceSuccess(String version);

    @Key("terminate.ec2.failed")
    String terminateInstanceFailed(String version);

    @Key("reboot.ec2.question")
    String rebootEC2InstanceQuestion(String instance);

    @Key("reboot.ec2.success")
    String rebootInstanceSuccess(String version);

    @Key("reboot.ec2.failed")
    String rebootInstanceFailed(String version);

    @Key("stop.ec2.question")
    String stopEC2InstanceQuestion(String instance);

    @Key("stop.ec2.force")
    String stopEC2Force();

    @Key("stop.ec2.failed")
    String stopInstanceFailed(String version);

    @Key("stop.ec2.success")
    String stopInstanceSuccess(String version);

    @Key("start.ec2.question")
    String startEC2InstanceQuestion(String instance);

    @Key("start.ec2.success")
    String startInstanceSuccess(String version);

    @Key("start.ec2.failed")
    String startInstanceFailed(String version);

    // Edit environment configuration
    @Key("update.environment.configuration.failed")
    String updateEnvironmentConfigurationFailed(String name);

    @Key("edit.configuration.ec2.instanceType")
    String editConfigurationEC2InstanceType();

    @Key("edit.configuration.ec2.securityGroups")
    String editConfigurationEC2SecurityGroups();

    @Key("edit.configuration.keyName")
    String editConfigurationKeyName();

    @Key("edit.configuration.monitoringInterval")
    String editConfigurationMonitoringInterval();

    @Key("edit.configuration.imageId")
    String editConfigurationImageId();

    @Key("edit.configuration.appHealthCheckUrl")
    String editConfigurationAppHealthCheckUrl();

    @Key("edit.configuration.healthCheckInterval")
    String editConfigurationHealthCheckInterval();

    @Key("edit.configuration.healthCheckTimeout")
    String editConfigurationHealthCheckTimeout();

    @Key("edit.configuration.healthyThreshold")
    String editConfigurationHealthyThreshold();

    @Key("edit.configuration.unhealthyThreshold")
    String editConfigurationUnhealthyThreshold();

    @Key("edit.configuration.initialJVMHeapSize")
    String editConfigurationInitialJVMHeapSize();

    @Key("edit.configuration.maximumJVMHeapSize")
    String editConfigurationMaximumJVMHeapSize();

    @Key("edit.configuration.maxPermSize")
    String editConfigurationMaxPermSize();

    @Key("edit.configuration.jvmOptions")
    String editConfigurationJVMOptions();

}
