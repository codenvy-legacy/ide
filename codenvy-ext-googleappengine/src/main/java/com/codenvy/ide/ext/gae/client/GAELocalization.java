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
package com.codenvy.ide.ext.gae.client;

/**
 * Google App Engine client localization resources.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public interface GAELocalization extends com.google.gwt.i18n.client.Messages {
    /* Buttons */
    @Key("cancel.button")
    String cancelButton();

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

    @Key("rollback.button")
    String rollbackButton();

    @Key("rollback.all.button")
    String rollbackAllButton();

    @Key("logs.button")
    String logsButton();

    @Key("vacuum.button")
    String vacuumButton();

    /* Create application */
    @Key("create.application.view.title")
    String createApplicationViewTitle();

    @Key("create.application.instruction")
    String createApplicationInstruction();

    @Key("create.application.cannot.deploy")
    String createApplicationCannotDeploy();

    /* Deploy application */

    @Key("deploy.application.success")
    String deployApplicationSuccess(String project, String link);

    @Key("deploy.application.started")
    String deployApplicationStarted(String projectName);

    @Key("deploy.application.finished")
    String deployApplicationFinished(String projectName);

    @Key("deploy.application.instruction")
    String deployApplicationInstruction();

    /* Manage Application */
    @Key("manage.application.view.title")
    String manageApplicationViewTitle();

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

    @Key("rollback.update.success")
    String rollbackUpdateSuccess();

    /* Log in */
    @Key("logout.not.logged")
    String logoutNotLogged();

    @Key("logout.success")
    String logoutSuccess();

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

    @Key("update.crons.successfully")
    String updateCronsSuccessfully();

    /* Crons */
    @Key("cron.url.title")
    String cronUrlTitle();

    @Key("cron.description.title")
    String cronDescriptionTitle();

    @Key("cron.grid.message")
    String cronGridMessage();

    /* Resource limits */
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

    @Key("backend.update.started")
    String backendUpdateStarted(String backend);

    @Key("backend.update.finished")
    String backendUpdateFinished(String backend);

    @Key("backends.update.started")
    String backendsUpdateStarted();

    @Key("backends.update.finished")
    String backendsUpdateFinished();
}