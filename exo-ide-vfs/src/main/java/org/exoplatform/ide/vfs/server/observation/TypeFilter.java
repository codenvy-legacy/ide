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
