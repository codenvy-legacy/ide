/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.ui.action;

/**
 * Represents constraints for some action. Constraints are used to specify
 * action's position in the default group, see {@link DefaultActionGroup}.
 *
 * @author Evgen Vidolob
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
