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
package com.codenvy.ide.extension.html.server;

/**
 * Class represents runned HTML-application.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunnedApplication.java Jul 1, 2013 11:55:48 AM azatsarynnyy $
 */
class RunnedApplication {
    final String name;
    final String projectName;
    final long   expirationTime;
    final String projectPath;

    RunnedApplication(String name, long expirationTime, String projectName, String projectPath) {
        this.name = name;
        this.expirationTime = expirationTime;
        this.projectName = projectName;
        this.projectPath = projectPath;
    }

    boolean isExpired() {
        return expirationTime < System.currentTimeMillis();
    }
}
