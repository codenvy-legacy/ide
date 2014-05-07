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
