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
package com.codenvy.ide.collaboration.dto;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface UserDetails {
    /**
     * Returns a unique ID for the user. This ID should be used in client-to-server
     * requests that identify a specific user.
     */
    String getUserId();

    /**
     * Returns the email address of a user. The email address may be obfuscated
     * depending on the user's privacy settings, and may not be a valid email
     * address.
     */
    String getDisplayEmail();

    /**
     * Returns the display name of the user. If the display name is not available,
     * returns the email.
     */
    String getDisplayName();

    /**
     * Returns the given (first) name of the user. If the given name is not
     * available, returns the display name. If the display name is not available
     * either, returns the email.
     */
    String getGivenName();

    /** Returns the portrait URL with the default size of 24 pixels. */
    String getPortraitUrl();

    /**
     * Returns a boolean indicating that this {@link UserDetails} represents the
     * current user.
     */
    boolean isCurrentUser();
}
