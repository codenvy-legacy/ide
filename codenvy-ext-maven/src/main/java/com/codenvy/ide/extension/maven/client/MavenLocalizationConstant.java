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
package com.codenvy.ide.extension.maven.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle:
 * 'MavenLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface MavenLocalizationConstant extends Messages {
    /* Actions */
    @Key("control.customBuildProject.id")
    String buildProjectControlId();

    @Key("control.customBuildProject.text")
    String buildProjectControlTitle();

    @Key("control.customBuildProject.description")
    String buildProjectControlDescription();

    @Key("button.cancel")
    @DefaultMessage("Cancel")
    String buttonCancel();

    @Key("maven.build.skipTest")
    @DefaultMessage("Skip Test (-Dmaven.test.skip)")
    String skipTest();

    @Key("maven.build.updateSnapshot")
    @DefaultMessage("Update Snapshot (-U,--update-snapshots)")
    String updateSnapshot();

    @Key("maven.build.updateSnapshot.desc")
    @DefaultMessage("Forces a check for updated releases \n" +
                    "and snapshots on remote repositories")
    String updateSnapshotDesc();

    @Key("maven.build.start")
    @DefaultMessage("Start build")
    String startBuild();

    @Key("maven.builder.title")
    @DefaultMessage("Maven Builder")
    String mavenBuilder();

    @Key("maven.builder.offline-mode")
    @DefaultMessage("Work Offline (-o,--offline)")
    String offlineMode();
}
