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
package org.exoplatform.ide.vfs.server.observation;

import java.util.regex.Pattern;

/**
 * Filter for events by path of changed item. Path specified in constructor - regular expression.
 * For example, expression <code>^(.&#042/)?web\\.xml</code> matched for any web.xml files.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class PathFilter extends ChangeEventFilter {
    private final Pattern pattern;

    public PathFilter(String path) {
        this.pattern = Pattern.compile(path);
    }

    @Override
    public boolean matched(ChangeEvent event) {
        final String path = event.getItemPath();
        final String oldPath = event.getOldItemPath();
        return path != null && pattern.matcher(path).matches() || oldPath != null && pattern.matcher(oldPath).matches();
    }
}
