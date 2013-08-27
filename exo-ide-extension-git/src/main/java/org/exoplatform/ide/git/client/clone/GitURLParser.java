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
package org.exoplatform.ide.git.client.clone;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class GitURLParser {

    /**
     * Parse GitHub url. Need extract "user" and "repository" name. If given Url its GitHub url return array of string first element will be
     * user name, second repository name else return null. GitHub url formats: - https://github.com/user/repo.git -
     * git@github.com:user/repo.git - git://github.com/user/repo.git
     * 
     * @param gitUrl
     * @return array of string
     */
    public static String[] parseGitHubUrl(String gitUrl) {
        if (gitUrl.endsWith("/")) {
            gitUrl = gitUrl.substring(0, gitUrl.length() - 1);
        }
        if (gitUrl.endsWith(".git")) {
            gitUrl = gitUrl.substring(0, gitUrl.length() - 4);
        }
        if (gitUrl.startsWith("git@github.com:")) {
            gitUrl = gitUrl.split("git@github.com:")[1];
            return gitUrl.split("/");
        } else if (gitUrl.startsWith("git://github.com/")) {
            gitUrl = gitUrl.split("git://github.com/")[1];
            return gitUrl.split("/");
        } else if (gitUrl.startsWith("https://github.com/")) {
            gitUrl = gitUrl.split("https://github.com/")[1];
            return gitUrl.split("/");
        }
        return null;
    }

}
