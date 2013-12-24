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
package com.codenvy.ide.ext.java.messages;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.shared.RoutingType;
import com.google.gwt.webworker.client.messages.Message;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = Message.NON_ROUTABLE_TYPE)
public interface ProblemLocationMessage extends Message{
    /**
     * Returns the start offset of the problem.
     *
     * @return the start offset of the problem
     */
    int getOffset();

    /**
     * Returns the length of the problem.
     *
     * @return the length of the problem
     */
    int getLength();

    /**
     * Returns the marker type of this problem.
     *
     * @return The marker type of the problem.
     */
    String getMarkerType();

    /**
     * Returns the id of problem. Note that problem ids are defined per problem marker type.
     * See {@link com.codenvy.ide.ext.java.jdt.core.compiler.IProblem} for id definitions for problems of type
     * <code>org.eclipse.jdt.core.problem</code> and <code>org.eclipse.jdt.core.task</code>.
     *
     * @return The id of the problem.
     */
    int getProblemId();

    /**
     * Returns the original arguments recorded into the problem.
     *
     * @return String[] Returns the problem arguments.
     */
    Array<String> getProblemArguments();

    /**
     * Returns if the problem has error severity.
     *
     * @return <code>true</code> if the problem has error severity
     */
    boolean isError();
}
