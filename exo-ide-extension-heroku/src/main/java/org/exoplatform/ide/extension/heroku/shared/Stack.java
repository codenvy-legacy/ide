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
package org.exoplatform.ide.extension.heroku.shared;

/**
 * Heroku stack - deployment variable.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Stack.java Mar 19, 2012 11:53:45 AM azatsarynnyy $
 */
public interface Stack {

    /**
     * Returns the stack's name.
     *
     * @return the name stack's name.
     */
    public String getName();

    /**
     * Changes the name of this stack to be equal to the argument <code>name</code>.
     *
     * @param name
     *         the new name of this stack.
     */
    public void setName(String name);

    /**
     * Returns <code>true</code> if the stack version is beta.
     *
     * @return <code>true</code> when the stack version is beta.
     */
    public boolean isBeta();

    /**
     * Set the stack version is beta.
     *
     * @param beta,
     *         if <code>true</code> the stack version is beta.
     */
    public void setBeta(boolean beta);

    /**
     * Test if this stack is a current stack for the application.
     *
     * @return the current if <code>true</code> the stack is current for the application
     */
    public boolean isCurrent();

    /**
     * Set the stack is current the the application.
     *
     * @param current
     *         if <code>true</code> the stack is current for the application
     */
    public void setCurrent(boolean current);

    /**
     * Returns <code>true</code> if the stack is requested.
     *
     * @return <code>true</code> if the stack is requested
     */
    public boolean isRequested();

    /**
     * If the argument value is <code>true</code> set the stack is requested.
     *
     * @param requested
     *         if <code>true</code> the stack is requested.
     */
    public void setRequested(boolean requested);

}