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

/**
 * Filter events by media type of changed Item.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class MimeTypeFilter extends ChangeEventFilter {
    private final MimeType pattern;

    public MimeTypeFilter(String mimeType) {
        pattern = fromString(mimeType);
    }

    @Override
    public boolean matched(ChangeEvent event) {
        String actual = event.getMimeType();
        if (actual == null) {
            return false;
        }
        MimeType other = fromString(actual);
        if (pattern.type.equals("*")) {
            return true;
        }
        if (pattern.type.equalsIgnoreCase(other.type)) {
            if (pattern.subType.equals("*") || pattern.subType.equalsIgnoreCase(other.subType)) {
                return true;
            }
        }
        return false;
    }

    private MimeType fromString(String str) {
        int sl = str.indexOf('/');
        int col = str.indexOf(';');

        if (sl < 0 && col < 0) {
            return new MimeType(str, "*");
        } else if (sl > 0 && col < 0) {
            return new MimeType(str.substring(0, sl), str.substring(sl + 1));
        } else if (sl < 0 && col > 0) {
            return new MimeType(str.substring(0, col), "*");
        }
        return new MimeType(str.substring(0, sl), str.substring(sl + 1, col));
    }

    private static class MimeType {
        final String type;
        final String subType;

        private MimeType(String type, String subType) {
            this.type = type;
            this.subType = subType;
        }
    }
}
