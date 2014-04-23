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
