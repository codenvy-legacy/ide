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
package com.codenvy.ide.ext.java.shared;

/** @author Artem Zatsarynnyy */
public interface Constants {
    final String MAVEN_ID = "maven";

    // project type names
    final String MAVEN_NAME                   = "Maven Project";
    // project categories
    final String JAVA_CATEGORY                = "Java";
    // project attribute names
    final String LANGUAGE                     = "language";
    final String LANGUAGE_VERSION             = "language.version";
    final String FRAMEWORK                    = "framework";
    final String FRAMEWORK_VERSION            = "framework.version";
    final String BUILDER_NAME                 = "builder.name";
    final String BUILDER_SOURCE_FOLDERS       = "builder.${builder}.source_folders";
    final String BUILDER_ANT_SOURCE_FOLDERS   = "builder.ant.source_folders";
    final String BUILDER_MAVEN_SOURCE_FOLDERS = "builder.maven.source_folders";
    final String RUNNER_NAME                  = "runner.name";
}
