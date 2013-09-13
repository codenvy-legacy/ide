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
package org.exoplatform.ide.extension.php.client;

/**
 * Interface to represent the messages contained in resource bundle: PhpExtensionLocalizationConstant.properties'.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpExtensionLocalizationConstant.java Apr 17, 2013 4:07:00 PM azatsarynnyy $
 *
 */
public interface PhpExtensionLocalizationConstant extends com.google.gwt.i18n.client.Messages {
    @Key("run.application.control.title")
    String runApplicationControlTitle();

    @Key("run.application.control.prompt")
    String runApplicationControlPrompt();

    @Key("starting.project.message")
    String startingProjectMessage(String project);

    @Key("project.started.message")
    String projectStartedMessage(String project);

    @Key("start.application.failed")
    String startApplicationFailed();

    @Key("application.started.url")
    String applicationStartedUrl(String application, String url);

    @Key("stop.application.control.title")
    String stopApplicationControlTitle();

    @Key("stop.application.control.prompt")
    String stopApplicationControlPrompt();

    @Key("stopping.project.message")
    String stoppingProjectMessage(String project);

    @Key("stop.application.failed")
    String stopApplicationFailed();

    @Key("project.stopped.message")
    String projectStoppedMessage(String project);

    @Key("not.php.project")
    String notPhpProject();

    @Key("no.run.application")
    String noRunningApplication();

    @Key("show.logs.control.title")
    String showLogsControlTitle();

    @Key("show.logs.control.prompt")
    String showLogsControlPrompt();

    @Key("get.logs.error.message")
    String getLogsErrorMessage();
}