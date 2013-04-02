/*
 * Copyright (C) 2013 eXo Platform SAS.
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
