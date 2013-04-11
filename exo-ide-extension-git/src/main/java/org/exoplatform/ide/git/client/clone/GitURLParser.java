/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
