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

/**
 * Created by The eXo Platform SAS .
 * <p/>
 * Description of a Java problem, as detected by the compiler or some of the underlying
 * technology reusing the compiler.
 * A problem provides access to:
 * <ul>
 * <li> its location (originating source file name, source position, line number), </li>
 * <li> its message description and a predicate to check its severity (warning or error). </li>
 * <li> its ID : a number identifying the very nature of this problem. All possible IDs are listed
 * as constants on this interface. </li>
 * </ul>
 * <p/>
 * Note: the compiler produces Problems internally, which are turned into markers by the JavaBuilder
 * so as to persist problem descriptions. This explains why there is no API allowing to reach Problem detected
 * when compiling. However, the Java problem markers carry equivalent information to IProblem, in particular
 * their ID (attribute "id") is set to one of the IDs defined on this interface.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Marker {

    public enum Type {

    }

    /**
     * Returns the problem id
     *
     * @return the problem id
     */
    int getID();

    /**
     * Answer a localized, human-readable message string which describes the problem.
     *
     * @return a localized, human-readable message string which describes the problem
     */
    String getMessage();

    /**
     * Answer the line number in source where the problem begins.
     *
     * @return the line number in source where the problem begins
     */
    int getLineNumber();

    /**
     * Answer the end position of the problem (inclusive), or -1 if unknown.
     *
     * @return the end position of the problem (inclusive), or -1 if unknown
     */
    int getEnd();

    /**
     * Answer the start position of the problem (inclusive), or -1 if unknown.
     *
     * @return the start position of the problem (inclusive), or -1 if unknown
     */
    int getStart();

    /**
     * Checks the severity to see if the Error bit is set.
     *
     * @return true if the Error bit is set for the severity, false otherwise
     */
    boolean isError();

    /**
     * Checks the severity to see if the Error bit is not set.
     *
     * @return true if the Error bit is not set for the severity, false otherwise
     */
    boolean isWarning();

    /**
     * Checks the severity to see if this is Breakpoint
     *
     * @return true if this is breakpoint
     */
    boolean isBreakpoint();


    boolean isCurrentBreakPoint();

}
