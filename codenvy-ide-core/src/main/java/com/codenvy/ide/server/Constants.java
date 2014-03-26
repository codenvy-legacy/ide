/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.server;

/** @author Artem Zatsarynnyy */
public interface Constants {
    // project type ids
    final String UNKNOWN_ID                   = "unknown";
    final String UNKNOWN_CATEGORY             = UNKNOWN_ID;
    final String CODENVY_PLUGIN_ID            = "codenvy_extension";
    // project type names
    final String UNKNOWN_NAME                 = "Unknown project type";
    final String CODENVY_PLUGIN_NAME          = "Codenvy extension";
    final String CODENVY_CATEGORY             = "Condevy";
    // project attribute names
    final String LANGUAGE                     = "language";
    final String LANGUAGE_VERSION             = "language.version";
    final String FRAMEWORK                    = "framework";
    final String BUILDER_NAME                 = "builder.name";
    final String BUILDER_MAVEN_SOURCE_FOLDERS = "builder.maven.source_folders";
    final String RUNNER_NAME                  = "runner.name";
}
