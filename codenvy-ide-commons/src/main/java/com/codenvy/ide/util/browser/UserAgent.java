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


/**
 * Information about the current user agent. Some of the information is
 * dynamically determined at runtime, other information is determined at compile
 * time as it is baked into the particular permutation with the deferred binding
 * mechanism.
 * <p/>
 * Methods that are statically evaluable allow conditional compilation for
 * different user agents.
 * <p/>
 * e.g. currently, the following code:
 * <p/>
 * if (UserAgent.isIE()) {
 * // do IE-specific implementation
 * } else {
 * // do non-IE implementation
 * }
 * <p/>
 * should for "user.agent" set to "ie6" compile down to just the IE-specific
 * implementation.
 * <p/>
 * It is not exposed as part of this API which methods are statically determined
 * and which are not, as this may be subject to change. In general it should not
 * matter as the cost of a runtime check is very cheap. If it does matter, it is
 * up to the caller to know and keep track of the current state of affairs.
 */
public abstract class UserAgent {

    /** @return true iff the user agent uses webkit */
    public static boolean isWebkit() {
        return UserAgentStaticProperties.get().isWebkit();
    }

    /** @return true iff the user agent uses mobile webkit */
    public static boolean isMobileWebkit() {
        return UserAgentStaticProperties.get().isMobileWebkit();
    }

    /** @return true iff the user.agent GWT property is "safari" */
    public static boolean isSafari() {
        return UserAgentStaticProperties.get().isSafari();
    }

    /** @return true iff the user.agent GWT property is "gecko" or "gecko1_8" */
    public static boolean isFirefox() {
        return UserAgentStaticProperties.get().isFirefox();
    }

    /** @return true iff the user.agent GWT property is "ie6" */
    public static boolean isIE() {
        return UserAgentStaticProperties.get().isIE();
    }

    /** @return true iff the user.agent GWT property is "android" */
    public static boolean isAndroid() {
        return UserAgentStaticProperties.get().isAndroid();
    }

    /** @return true iff the user.agent GWT property is "iphone" */
    public static boolean isIPhone() {
        return UserAgentStaticProperties.get().isIPhone();
    }

    /** @return true if this is the chrome browser */
    public static boolean isChrome() {
        return UserAgentRuntimeProperties.get().isChrome();
    }

    public static boolean isIE7() {
        return UserAgentRuntimeProperties.get().isIe7();
    }

    public static boolean isIE8() {
        return UserAgentRuntimeProperties.get().isIe8();
    }

    /** @return true if we are on OSX */
    public static boolean isMac() {
        return UserAgentRuntimeProperties.get().isMac();
    }

    /** @return true if we are on Windows */
    public static boolean isWin() {
        return UserAgentRuntimeProperties.get().isWin();
    }

    /** @return true if we are on Linux */
    public static boolean isLinux() {
        return UserAgentRuntimeProperties.get().isLinux();
    }

    /**
     * Debug method that returns the user-agent string.
     * <p/>
     * NOTE(user): FOR DEBUGGING PURPOSES ONLY. DO NOT USE FOR PROGRAM LOGIC.
     */
    public static String debugUserAgentString() {
        return UserAgentRuntimeProperties.get().getUserAgent();
    }

    /**
     * @return whether the current user agent version is at least the one given by
     *         the method parameters.
     */
    public static boolean isAtLeastVersion(int major, int minor) {
        return UserAgentRuntimeProperties.get().isAtLeastVersion(major, minor);
    }

    /**
     * Do not use this for program logic - for debugging only. For program logic,
     * instead use {@link #isAtLeastVersion(int, int)}
     */
    public static int debugGetMajorVer() {
        return UserAgentRuntimeProperties.get().getMajorVer();
    }

    /**
     * Do not use this for program logic - for debugging only. For program logic,
     * instead use {@link #isAtLeastVersion(int, int)}
     */
    public static int debugGetMinorVer() {
        return UserAgentRuntimeProperties.get().getMinorVer();
    }

}
