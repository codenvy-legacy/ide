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
package com.codenvy.ide.extension.builder.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle:
 * 'BuilderLocalizationConstant.properties'.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderLocalizationConstant.java Feb 21, 2012 3:06:29 PM azatsarynnyy $
 */
public interface BuilderLocalizationConstant extends Messages {
    /* Actions */
    @Key("control.buildProject.id")
    String buildProjectControlId();

    @Key("control.buildProject.text")
    String buildProjectControlTitle();

    @Key("control.buildProject.description")
    String buildProjectControlDescription();

    @Key("control.buildAndPublishProject.id")
    String buildAndPublishProjectControlId();

    @Key("control.buildAndPublishProject.text")
    String buildAndPublishProjectControlText();

    @Key("control.buildAndPublishProject.description")
    String buildAndPublishProjectControlDescription();

    /* Messages */
    @Key("messages.buildInProgress")
    String buildInProgress(String project);

    @Key("messages.buildSuccess")
    String buildSuccess();

    @Key("messages.buildFailed")
    String buildFailed();

    @Key("messages.selectedItemsFail")
    String selectedItemsFail();

    @Key("messages.downloadArtifact")
    String downloadArtifact(String url);

    @Key("messages.dependencyForYourPom")
    String dependencyForYourPom(String dependency);

    @Key("messages.failGetBuildResult")
    String failGetBuildResult();

    /* BuildProjectView */
    @Key("buildProject.id")
    String buildProjectId();

    @Key("buildProject.title")
    String buildProjectTitle();

    @Key("buildProject.outputClear")
    String outputClear();

    /* InitRequestHandler */
    @Key("build.started")
    String buildStarted(String project);

    @Key("build.finished")
    String buildFinished(String project);
}
