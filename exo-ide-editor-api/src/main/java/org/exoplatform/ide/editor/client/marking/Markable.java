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

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * This interface determines that its implementor (usually editor) can mark problems.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 8, 2012 4:45:51 PM anya $
 */
public interface Markable {

    void addProblems(Marker[] problems);

    /**
     * Marks problem
     *
     * @param problem
     */
    void markProblem(Marker problem);

    /**
     * Remove problem
     *
     * @param problem
     */
    void unmarkProblem(Marker problem);

    /** Unmarks all problems */
    void unmarkAllProblems();

    /**
     * Adds handler for {@link ProblemClickEvent}
     *
     * @param handler
     *         handler of {@link ProblemClickEvent}
     * @return {@link HandlerRegistration} of {@link ProblemClickHandler}
     */
    HandlerRegistration addProblemClickHandler(ProblemClickHandler handler);

    /**
     * Adds handler for {@link EditorLineNumberDoubleClickEvent}
     *
     * @param handler
     *         handler of {@link EditorLineNumberDoubleClickEvent}
     * @return {@link HandlerRegistration}
     */
    HandlerRegistration addLineNumberDoubleClickHandler(EditorLineNumberDoubleClickHandler handler);

    /**
     * Adds handler for {@link EditorLineNumberContextMenuEvent} event.
     *
     * @param handler
     *         handler for {@link EditorLineNumberContextMenuEvent}
     * @return {@link HandlerRegistration}
     */
    HandlerRegistration addLineNumberContextMenuHandler(EditorLineNumberContextMenuHandler handler);

}
