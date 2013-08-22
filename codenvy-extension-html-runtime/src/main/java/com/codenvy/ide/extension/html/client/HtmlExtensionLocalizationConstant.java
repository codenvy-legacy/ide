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
package com.codenvy.ide.extension.html.client;

/**
 * Interface to represent the messages contained in resource bundle: HtmlExtensionLocalizationConstant.properties'.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlExtensionLocalizationConstant.java Jun 26, 2013 11:04:52 AM azatsarynnyy $
 */
public interface HtmlExtensionLocalizationConstant extends com.google.gwt.i18n.client.Messages {
    @Key("run.application.control.title")
    String runApplicationControlTitle();

    @Key("run.application.control.prompt")
    String runApplicationControlPrompt();

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

    @Key("stop.application.failed")
    String stopApplicationFailed();

    @Key("project.stopped.message")
    String projectStoppedMessage(String project);

    @Key("not.html.project")
    String notHtmlProject();
}
