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

package org.exoplatform.ide.editor.client.marking;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires when problem marker clicked.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProblemClickEvent extends GwtEvent<ProblemClickHandler> {

    /** Type of this Event. */
    public static final GwtEvent.Type<ProblemClickHandler> TYPE = new GwtEvent.Type<ProblemClickHandler>();

    /** Array of problems */
    private Marker[] problems;

    /**
     * Creates new instance of ProblemClickEvent
     *
     * @param problems
     *         array of problems
     */
    public ProblemClickEvent(Marker[] problems) {
        this.problems = problems;
    }

    /**
     * Returns array of problems
     *
     * @return array of problems
     */
    public Marker[] getProblems() {
        return problems;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ProblemClickHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ProblemClickHandler handler) {
        handler.onProblemClick(this);
    }

}
