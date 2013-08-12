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

package com.codenvy.ide.util.browser;

import com.google.gwt.core.client.GWT;

/**
 * Class to allow conditional compilation for different user agents.
 * <p/>
 * All methods should return values that are known at compile time.
 * *
 * TODO(user): Should this ever be thrown in for free with GWT, rather use
 * their approach. A relevant thread is:
 * http://groups.google.com/group/Google-Web-Toolkit-Contributors/browse_thread/thread/6745dee7a85eb585/bd58d1a9f2344b34
 */
public abstract class UserAgentStaticProperties {

    static UserAgentStaticProperties get() {
        return INSTANCE;
    }

    private static final UserAgentStaticProperties INSTANCE = createInstance();

    /**
     * Creates an instance of UserAgent.
     * <p/>
     * NOTE(danilatos): This method is designed to be statically evaluable by
     * the compiler, such that the compiler can determine that
     * only one subclass of UserAgent is ever used within a
     * given permutation. This is possible because
     * GWT.isClient() is replaced with true by the compiler,
     * even though it is executed normally in unit tests.
     * Testing the return value of GWT.create() is not adequate
     * because only boolean values can be statically evaluated
     * by the compiler at this time.
     *
     * @return an instance of UserAgent.
     */
    private static UserAgentStaticProperties createInstance() {
        if (GWT.isClient()) {
            return GWT.create(UserAgentStaticProperties.class);
        } else {
            return new FirefoxImpl();
        }
    }

    final boolean isWebkit() {
        return isSafari() || isMobileWebkit();
    }

    /** @return true iff the user agent uses mobile webkit */
    final boolean isMobileWebkit() {
        return isAndroid() || isIPhone();
    }

    // Default instance methods: most return false, since they are intended to be overriden.
    boolean isSafari() {
        return false;
    }

    boolean isFirefox() {
        return false;
    }

    boolean isIE() {
        return false;
    }

    boolean isAndroid() {
        return false;
    }

    boolean isIPhone() {
        return false;
    }

    // NOTE(user): Created via deferred binding
    public static class SafariImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isSafari() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class FirefoxImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isFirefox() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class IEImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isIE() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class AndroidImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isAndroid() {
            return true;
        }
    }

    // NOTE(user): Created via deferred binding
    public static class IPhoneImpl extends UserAgentStaticProperties {
        @Override
        protected boolean isIPhone() {
            return true;
        }
    }
}
