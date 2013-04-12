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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

import java.util.Comparator;

/**
 * Helps to order breakpoints by name of location and line number.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class BreakPointComparator implements Comparator<BreakPoint> {
    @Override
    public int compare(BreakPoint o1, BreakPoint o2) {
        String className1 = o1.getLocation().getClassName();
        String className2 = o2.getLocation().getClassName();
        if (className1 == null && className2 == null) {
            return 0;
        }
        if (className1 == null) {
            return 1;
        }
        if (className2 == null) {
            return -1;
        }
        int result = className1.compareTo(className2);
        if (result == 0) {
            result = o1.getLocation().getLineNumber() - o2.getLocation().getLineNumber();
        }
        return result;
    }
}
