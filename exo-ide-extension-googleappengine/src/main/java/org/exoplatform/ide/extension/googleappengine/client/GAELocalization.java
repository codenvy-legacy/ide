/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.googleappengine.client;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface GAELocalization extends com.google.gwt.i18n.client.Messages {
    @Key("google.app.engine.control")
    String googleAppEngineControl();

    /* Buttons */
    @Key("authenticate.button")
    String authenticateButton();

    @Key("ok.button")
    String okButton();

    @Key("cancel.button")
    String cancelButton();

    @Key("login.button")
    String loginButton();

    @Key("deploy.button")
    String deployButton();

    @Key("create.button")
    @DefaultMessage("Create")
    String createButton();

    @Key("close.button")
    String closeButton();

    @Key("configure.button")
    String configureButton();

    @Key("delete.button")
    String deleteButton();

    @Key("update.button")
    String updateButton();

    @Key("update.all.button")
    String updateAllButton();

    @Key("rollback.button")
    String rollbackButton();

    @Key("rollback.all.button")
    String rollbackAllButton();

    @Key("logs.button")
    String logsButton();

    @Key("vacuum.button")
    String vacuumButton();

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
    @Key("deploy.application.use.existed.id.field")
    String deployApplicationUseExistedIdField();

    @Key("deploy.application.empty.id.message")
    String deployApplicationEmptyIdMessage();

    @Key("deploy.application.control.title")
    String deployApplicationControlTitle();

    @Key("deploy.application.control.prompt")
    String deployApplicationControlPrompt();

    @Key("deploy.application.success")
    String deployApplicationSuccess(String project, String link);

    @Key("deploy.application.message")
    String deployApplicationMessage(String projectName);

    @Key("deploy.application.started")
    String deployApplicationStarted(String projectName);

    @Key("deploy.application.finished")
    String deployApplicationFinished(String projectName);

    @Key("deploy.application.instruction")
    String deployApplicationInstruction();

    /* Manage Application */
    @Key("manage.application.view.title")
    String manageApplicationViewTitle();

    @Key("manage.application.backends.tab")
    String manageApplicationBackendsTab();

    @Key("manage.application.crons.tab")
    String manageApplicationCronsTab();

    @Key("manage.application.general.tab")
    String manageApplicationGeneralTab();

    @Key("manage.application.indexes")
    String manageApplicationIndexes();

    @Key("manage.application.application")
    String manageApplicationApplication();

    @Key("manage.application.pagespeed")
    String manageApplicationPagespeed();

    @Key("manage.application.queues")
    String manageApplicationQueues();

    @Key("manage.application.dos")
    String manageApplicationDos();

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

    @Key("login.message")
    String loginMessage();

    @Key("login.control.title")
    String loginControlTitle();

    @Key("login.control.prompt")
    String loginControlPrompt();

    @Key("logout.control.title")
    String logoutControlTitle();

    @Key("logout.control.prompt")
    String logoutControlPrompt();

    @Key("logout.not.logged")
    String logoutNotLogged();

    @Key("logout.success")
    String logoutSuccess();

    /* Logs */
    @Key("logs.view.title")
    String logsViewTitle();

    @Key("num.days.field.title")
    String numDaysFieldTitle();

    @Key("logs.severity.field.title")
    String logsSeverityFieldTitle();

    @Key("get.logs.button")
    String getLogsButton();

    /* Error */
    @Key("not.app.engine.project")
    String notAppEngineProjectError();

    @Key("unknown.error.message")
    String unknownErrorMessage();

    /* Messages */
    @Key("update.pagespeed.successfully")
    String updatePageSpeedSuccessfully();

    @Key("update.queues.successfully")
    String updateQueuesSuccessfully();

    @Key("update.dos.successfully")
    String updateDosSuccessfully();

    @Key("update.indexes.successfully")
    @DefaultMessage("Application indexes are successfully updated")
    String updateIndexesSuccessfully();

    @Key("vacuum.indexes.successfully")
    @DefaultMessage("Application indexes are successfully updated")
    String vacuumIndexesSuccessfully();

    @Key("update.backend.successfully")
    String updateBackendSuccessfully(String backend);

    @Key("rollback.backend.successfully")
    String rollbackBackendSuccessfully(String backend);

    @Key("configure.backend.successfully")
    String configureBackendSuccessfully(String backend);

    @Key("delete.backend.successfully")
    String deleteBackendSuccessfully(String backend);

    @Key("update.all.backends.successfully")
    String updateAllBackendsSuccessfully();

    @Key("rollback.all.backends.successfully")
    String rollbackAllBackendsSuccessfully();

    @Key("configure.backend.button")
    @DefaultMessage("Configure Backend")
    String configureBackendButton();

    @Key("delete.backend.button")
    @DefaultMessage("Delete Backend")
    String deleteBackendButton();

    @Key("list.backends.button")
    @DefaultMessage("Backends")
    String getListBackendsButton();

    @Key("rollback.backend.button")
    @DefaultMessage("Rollback Backend")
    String rollbackBackendButton();

    @Key("rollback.allbackends.button")
    @DefaultMessage("Rollback All Backends")
    String rollbackAllBackendsButton();

    @Key("set.backend.state.button")
    @DefaultMessage("Set Backend State")
    String setBackendStateButton();

    @Key("update.all.backends.button")
    @DefaultMessage("Update All Backends")
    String updateAllBackendsButton();

    @Key("update.backend.button")
    @DefaultMessage("Update Backend")
    String updateBackendButton();

    @Key("update.backends.button")
    @DefaultMessage("Update Backends")
    String updateBackendsButton();

    @Key("update.crons.successfully")
    String updateCronsSuccessfully();

    /* Crons */
    @Key("cron.url.title")
    String cronUrlTitle();

    @Key("cron.description.title")
    String cronDescriptionTitle();

    @Key("cron.schedule.title")
    String cronScheduleTitle();

    @Key("cron.timezone.title")
    String cronTimezoneTitle();

    @Key("cron.grid.message")
    String cronGridMessage();

    /* Resource limits */
    @Key("resource.limits.tab.title")
    String resourceLimitsTabTitle();

    @Key("resource.column.title")
    String resourceColumnTitle();

    @Key("limit.column.title")
    String limitColumnTitle();

    @Key("max.blob.size")
    String maxBlobSize();

    @Key("max.file.size")
    String maxFileSize();

    @Key("max.file.count")
    String maxFileCount();

    @Key("max.total.file.size")
    String maxTotalFileSize();

    /* Backend */
    @Key("backend.name.title")
    String backendNameTitle();

    @Key("backend.state.title")
    String backendStateTitle();

    @Key("backend.class.title")
    String backendClassTitle();

    @Key("backend.instances.title")
    String backendInstancesTitle();

    @Key("backend.dynamic.title")
    String backendDynamicTitle();

    @Key("backend.public.title")
    String backendPublicTitle();

    @Key("backend.delete.title")
    String backendDeleteTitle();

    @Key("backend.delete.question")
    String backendDeleteQuestion(String backend);

    @Key("backend.update.started")
    String backendUpdateStarted(String backend);

    @Key("backend.update.finished")
    String backendUpdateFinished(String backend);

    @Key("backends.update.started")
    String backendsUpdateStarted();

    @Key("backends.update.finished")
    String backendsUpdateFinished();

    @Key("login.oauth.title")
    String loginOAuthTitle();

    @Key("login.oauth.label")
    String loginOAuthLabel();
}
