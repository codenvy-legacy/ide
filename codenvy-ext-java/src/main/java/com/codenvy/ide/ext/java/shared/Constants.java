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
    // project type ids
    final String JAR_ID                       = "jar";
    final String WAR_ID                       = "war";
    final String SPRING_ID                    = "spring";
    final String MULTI_MODULE_ID              = "maven_multi_module";
    // project type names
    final String JAR_NAME                     = "Java Library (JAR)";
    final String WAR_NAME                     = "Java Web Application (WAR)";
    final String SPRING_NAME                  = "Spring Application";
    final String MULTI_MODULE_NAME            = "Maven Multi Module Project";
    // project attribute names
    final String LANGUAGE                     = "language";
    final String LANGUAGE_VERSION             = "language.version";
    final String FRAMEWORK                    = "framework";
    final String BUILDER_NAME                 = "builder.name";
    final String BUILDER_SOURCE_FOLDERS       = "builder.${builder}.source_folders";
    final String BUILDER_ANT_SOURCE_FOLDERS   = "builder.ant.source_folders";
    final String BUILDER_MAVEN_SOURCE_FOLDERS = "builder.maven.source_folders";
    final String RUNNER_NAME                  = "runner.name";
}
