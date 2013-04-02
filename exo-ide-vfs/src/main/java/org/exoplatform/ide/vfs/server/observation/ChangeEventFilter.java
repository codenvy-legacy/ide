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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.List;

/**
 * Can be used to narrow the set of messages received by the EventListener.
 * Filter may be passed when new listener is registered in EventListenerList.
 * Filter may be combined, see methods <code>createAndFilter</code> or <code>createOrFilter</code>.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class ChangeEventFilter {
    public static ChangeEventFilter createAndFilter(ChangeEventFilter... filters) {
        if (filters == null || filters.length < 2) {
            throw new IllegalArgumentException("At least two filters required. ");
        }
        ChangeEventFilter[] copy = new ChangeEventFilter[filters.length];
        System.arraycopy(filters, 0, copy, 0, filters.length);
        return new AndChangeEventFilter(copy);
    }

    public static ChangeEventFilter createAndFilter(List<ChangeEventFilter> filters) {
        return createAndFilter(filters.toArray(new ChangeEventFilter[filters.size()]));
    }

    private static class AndChangeEventFilter extends ChangeEventFilter {
        private final ChangeEventFilter[] filters;

        @Override
        public boolean matched(ChangeEvent event) throws VirtualFileSystemException {
            for (ChangeEventFilter filter : filters) {
                if (!filter.matched(event)) {
                    return false;
                }
            }
            return true;
        }

        private AndChangeEventFilter(ChangeEventFilter[] filters) {
            this.filters = filters;
        }
    }

    public static ChangeEventFilter createOrFilter(ChangeEventFilter... filters) {
        if (filters == null || filters.length < 2) {
            throw new IllegalArgumentException("At least two filters required. ");
        }
        ChangeEventFilter[] copy = new ChangeEventFilter[filters.length];
        System.arraycopy(filters, 0, copy, 0, filters.length);
        return new OrChangeEventFilter(copy);
    }

    public static ChangeEventFilter createOrFilter(List<ChangeEventFilter> filters) {
        return createOrFilter(filters.toArray(new ChangeEventFilter[filters.size()]));
    }

    private static class OrChangeEventFilter extends ChangeEventFilter {
        private final ChangeEventFilter[] filters;

        @Override
        public boolean matched(ChangeEvent event) throws VirtualFileSystemException {
            for (ChangeEventFilter filter : filters) {
                if (filter.matched(event)) {
                    return true;
                }
            }
            return false;
        }

        private OrChangeEventFilter(ChangeEventFilter[] filters) {
            this.filters = filters;
        }
    }

    public static final ChangeEventFilter ANY_FILTER = new AnyFilter();

    private static class AnyFilter extends ChangeEventFilter {
        @Override
        public boolean matched(ChangeEvent event) {
            return true;
        }
    }

   /* ================================================= */

    public abstract boolean matched(ChangeEvent event) throws VirtualFileSystemException;

}
