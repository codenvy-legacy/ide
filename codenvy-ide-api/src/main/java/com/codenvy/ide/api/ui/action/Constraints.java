/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.api.ui.action;

/**
 * Represents constraints for some action. Constraints are used to specify
 * action's position in the default group, see {@link DefaultActionGroup}.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Constraints {
    public final static Constraints FIRST = new Constraints(Anchor.FIRST, null);
    public final static Constraints LAST  = new Constraints(Anchor.LAST, null);
    /** Anchor. */
    public Anchor myAnchor;

    /**
     * Id of the action to be positioned relative to. Used when anchor type
     * is either {@link Anchor#AFTER} or {@link Anchor#BEFORE}.
     */
    public String myRelativeToActionId;

    /**
     * Creates a new constraints instance with the specified anchor type and
     * id of the relative action.
     *
     * @param anchor
     *         anchor
     * @param relativeToActionId
     *         Id of the relative action
     */
    public Constraints(Anchor anchor, String relativeToActionId) {
        myAnchor = anchor;
        myRelativeToActionId = relativeToActionId;
    }

    public Constraints clone() {
        Constraints c = new Constraints(myAnchor, myRelativeToActionId);
        return c;
    }

}
