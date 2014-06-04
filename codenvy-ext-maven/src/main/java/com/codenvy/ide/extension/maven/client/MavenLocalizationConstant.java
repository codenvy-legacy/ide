/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
