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
package com.codenvy.ide.ext.git.server.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Commons class for Git and nested modules.
 *
 * @author Vladyslav Zhukovskii
 */
public class Util {
    public static final Pattern GIT_SSH_URL_PATTERN = Pattern.compile("((((git|ssh)://)(([^\\\\/@:]+@)??)" +
                                                                      "[^\\\\/@:]+)|([^\\\\/@:]+@[^\\\\/@:]+)" +
                                                                      ")(:|/)[^\\\\@:]+");

    public static final Pattern GITHUB_URL_PATTERN = Pattern.compile(".*github\\.com.*");

    public static String getCodenvyTimeStamptKeyLabel() {
        return "Codenvy SSH Key (" + new SimpleDateFormat().format(new Date()) + ")";
    }

    public static boolean isSSH(String url) {
        return GIT_SSH_URL_PATTERN.matcher(url).matches();
    }

    public static boolean isGitHub(String url) {
        return GITHUB_URL_PATTERN.matcher(url).matches();
    }
}
