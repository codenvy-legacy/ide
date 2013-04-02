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
