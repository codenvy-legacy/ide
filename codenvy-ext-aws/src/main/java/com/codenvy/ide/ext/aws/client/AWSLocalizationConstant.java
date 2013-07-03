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
    @Key("button.create")
    String createButton();

    @Key("button.create.bucket")
    String createBucketButton();

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

    @Key("button.launch")
    String launchButton();

    @Key("button.environment.configuration.view")
    String viewConfigurationButton();

    @Key("button.restart")
    String restartButton();

    @Key("button.rebuild")
    String rebuildButton();

    @Key("button.terminate")
    String terminateButton();

    @Key("button.logs")
    String logsButton();

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

    @Key("button.instance.terminate")
    String terminateInstanceButton();

    @Key("button.instance.reboot")
    String rebootInstanceButton();

    @Key("button.instance.stop")
    String stopInstanceButton();

    @Key("button.instance.start")
    String startInstanceButton();

    // Messages
    @Key("creatingProject")
    String creatingProject();

    @Key("unableGetEnvironmentInfo")
    String unableToGetEnvironmentInfo(String name);

    @Key("validationErrorTitle")
    String validationErrorTitile();

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
    @Key("button.properties")
    String propertiesButton();

    @Key("button.upload")
    String uploadButton();

    @Key("button.refresh")
    String refreshButton();

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

    @Key("create.application.terminated")
    String createApplicationTerminated();

    @Key("launch.environment.success")
    String launchEnvironmentSuccess(String environment);

    @Key("launch.environment.fail")
    String launchEnvironmentFailed(String environment);

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

    @Key("environments.tab")
    String environmentsTab();

    @Key("events.tab")
    String eventsTab();

    @Key("environment.configuration.title")
    String environmentConfigurationTitle();

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

    @Key("terminate.environment.launching")
    String terminateEnvironmentLaunching(String environment);

    @Key("terminate.environment.success")
    String terminateEnvironmentSuccess(String name);

    @Key("terminate.environment.failed")
    String terminateEnvironmentFailed(String name);

    // Rebuild Environment
    @Key("rebuild.environment.view.title")
    String rebuildEnvironmentViewTitle();

    @Key("rebuild.environment.question")
    String rebuildEnvironmentQuestion(String name);

    @Key("rebuild.environment.launching")
    String rebuildEnvironmentLaunching(String environment);

    @Key("rebuild.environment.failed")
    String rebuildEnvironmentFailed(String name);

    @Key("rebuild.environment.success")
    String rebuildEnvironmentSuccess(String name);

    // Get Logs
    @Key("logs.environment.failed")
    String logsEnvironmentFailed(String name);

    // Update Environment
    @Key("update.environment.launching")
    String updateEnvironmentLaunching(String environment);

    @Key("update.environment.success")
    String updateEnvironmentSuccess(String name);

    // Restart App Server
    @Key("restart.appserver.view.title")
    String restartAppServerViewTitle();

    @Key("restart.appserver.question")
    String restartAppServerQuestion(String name);

    @Key("restart.appserver.failed")
    String restartAppServerFailed(String name);

    // S3 Management
    @Key("s3.management.view.title")
    String s3managementViewTitle();

    @Key("s3.management.empty.bucket")
    String s3managementEmptyBucket(String bucket);

    @Key("control.s3.managemnt.id")
    String s3ManagementControlId();

    @Key("control.s3.managemnt.title")
    String s3ManagementControlTitle();

    @Key("control.s3.managemnt.promt")
    String s3ManagementControlPrompt();

    @Key("s3.management.delete.question")
    String s3ManagementDeleteQuestion(String bucket);

    @Key("s3.management.delete.title")
    String s3ManagementDeleteTitle();

    // EC2 Management View
    @Key("management.ec2.id")
    String managementEC2ViewTitle();

    @Key("configuration.ec2.failed")
    String getEnvironmentConfigurationFailed();

    @Key("terminate.ec2.view.title")
    String terminateEC2InstanceViewTitle();

    @Key("terminate.ec2.question")
    String terminateEC2InstanceQuestion(String instance);

    @Key("terminate.ec2.success")
    String terminateInstanceSuccess(String version);

    @Key("terminate.ec2.failed")
    String terminateInstanceFailed(String version);

    @Key("reboot.ec2.view.title")
    String rebootEC2InstanceViewTitle();

    @Key("reboot.ec2.question")
    String rebootEC2InstanceQuestion(String instance);

    @Key("reboot.ec2.success")
    String rebootInstanceSuccess(String version);

    @Key("reboot.ec2.failed")
    String rebootInstanceFailed(String version);

    @Key("stop.ec2.view.title")
    String stopEC2InstanceViewTitle();

    @Key("stop.ec2.question")
    String stopEC2InstanceQuestion(String instance);

    @Key("stop.ec2.force")
    String stopEC2Force();

    @Key("stop.ec2.failed")
    String stopInstanceFailed(String version);

    @Key("stop.ec2.success")
    String stopInstanceSuccess(String version);

    @Key("start.ec2.view.title")
    String startEC2InstanceViewTitle();

    @Key("start.ec2.question")
    String startEC2InstanceQuestion(String instance);

    @Key("start.ec2.success")
    String startInstanceSuccess(String version);

    @Key("start.ec2.failed")
    String startInstanceFailed(String version);

    @Key("management.ec2.tags")
    String managementEC2Tags();

    @Key("edit.environment.prompt")
    String editEnvironmentPrompt();

    @Key("restart.environment.prompt")
    String restartEnvironmentPrompt();

    @Key("rebuild.environment.prompt")
    String rebuildEnvironmentPrompt();

    @Key("terminate.environment.prompt")
    String terminateEnvironmentPrompt();

    @Key("log.environment.prompt")
    String logEnvironmentPrompt();

    // Edit environment configuration
    @Key("update.environment.configuration.failed")
    String updateEnvironmentConfigurationFailed(String name);

    @Key("server.tab")
    String serverTab();

    @Key("loadBalancer.tab")
    String loadBalancerTab();

    @Key("container.tab")
    String containerTab();

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
