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