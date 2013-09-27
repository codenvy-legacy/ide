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
package org.exoplatform.ide.extension.heroku.server;

import org.exoplatform.ide.extension.heroku.shared.Stack;

/**
 * Heroku stack - deployment variable.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 28, 2011 4:09:51 PM anya $
 */
public class StackImpl implements Stack {
    /** Stack's name. */
    private String name;

    /** Beta version. If <code>true</code> the stack version is beta. */
    private boolean beta;

    /** If <code>true</code> the stack is current for the application. */
    private boolean current;

    /**
     *
     */
    private boolean requested;

    /** Default constructor. */
    public StackImpl() {

    }

    /**
     * @param name
     *         stack's name
     * @param current
     *         if <code>true</code> the stack is current for the application
     * @param beta
     *         if <code>true</code> the stack version is beta
     * @param requested
     *         f <code>true</code> the stack version is requested
     */
    public StackImpl(String name, boolean current, boolean beta, boolean requested) {
        this.name = name;
        this.beta = beta;
        this.current = current;
        this.requested = requested;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#getName() */
    @Override
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#setName(java.lang.String) */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#isBeta() */
    @Override
    public boolean isBeta() {
        return beta;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#setBeta(boolean) */
    @Override
    public void setBeta(boolean beta) {
        this.beta = beta;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#isCurrent() */
    @Override
    public boolean isCurrent() {
        return current;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#setCurrent(boolean) */
    @Override
    public void setCurrent(boolean current) {
        this.current = current;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#isRequested() */
    @Override
    public boolean isRequested() {
        return requested;
    }

    /** @see org.exoplatform.ide.extension.heroku.shared.Stack#setRequested(boolean) */
    @Override
    public void setRequested(boolean requested) {
        this.requested = requested;
    }
}
