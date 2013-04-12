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

import java.util.EnumSet;
import java.util.Set;

import static org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;

/**
 * Filter events by ChangeEvent.ChangeType.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class TypeFilter extends ChangeEventFilter {
    private final Set<ChangeType> types;

    public TypeFilter(ChangeType type) {
        this.types = EnumSet.of(type);
    }

    public TypeFilter(ChangeType type1,
                      ChangeType type2) {
        this.types = EnumSet.of(type1, type2);
    }

    public TypeFilter(ChangeType type1,
                      ChangeType type2,
                      ChangeType type3) {
        this.types = EnumSet.of(type1, type2, type3);
    }

    public TypeFilter(ChangeType type1,
                      ChangeType type2,
                      ChangeType type3,
                      ChangeType type4) {
        this.types = EnumSet.of(type1, type2, type3, type4);
    }

    public TypeFilter(ChangeType type1,
                      ChangeType type2,
                      ChangeType type3,
                      ChangeType type4,
                      ChangeType type5) {
        this.types = EnumSet.of(type1, type2, type3, type4, type5);
    }

    public TypeFilter(Set<ChangeType> types) {
        this.types = EnumSet.copyOf(types);
    }

    @Override
    public boolean matched(ChangeEvent event) {
        ChangeType eventType = event.getType();
        return eventType != null && this.types.contains(eventType);
    }
}
