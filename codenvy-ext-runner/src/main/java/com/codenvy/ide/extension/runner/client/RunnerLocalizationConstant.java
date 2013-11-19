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
package com.codenvy.ide.extension.runner.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle:
 * 'BuilderLocalizationConstant.properties'.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderLocalizationConstant.java Feb 21, 2012 3:06:29 PM azatsarynnyy $
 */
public interface RunnerLocalizationConstant extends Messages {


    @Key("runner.starting")
    String starting(String project);

    @Key("runner.started")
    String started(String project);

    @DefaultMessage("Run/Run")
    @Key("control.runAppControlId")
    String runAppControlId();

    @DefaultMessage("Show Logs...")
    @Key("control.show.logs.title")
    String showLogsControlTitle();

    @DefaultMessage("Show Application Logs...")
    @Key("control.show.logs.prompt")
    String showLogsControlPrompt();

    @DefaultMessage("Run/Stop")
    @Key("control.stopAppControlId")
    String stopAppControlId();

    @DefaultMessage("Run/Show Logs...")
    @Key("control.showlogsid")
    String showLogsControlId();


    @DefaultMessage("Application <b>{0}</b> started.")
    @Key("appStarted")
    String applicationStarted(String name);

    @DefaultMessage("Application <b>{0}</b> stopped.")
    @Key("appStoped")
    String applicationStoped(String name);

    @DefaultMessage("Updated application <b>{0}</b> on {1}.")
    @Key("appUpdated")
    String applicationUpdated(String name, String uris);

    @DefaultMessage("Update application <b>{0}</b> failed.")
    @Key("updateAppFailed")
    String updateApplicationFailed(String name);

    @DefaultMessage("Start application failed.")
    @Key("startAppFailed")
    String startApplicationFailed();

    @DefaultMessage("Stop application failed.")
    @Key("stop.application.failed")
    String stopApplicationFailed();



}
