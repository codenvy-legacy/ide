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
package org.exoplatform.ide.client.framework.invite;

import java.util.List;

/**
 * Describes a Google contact entry.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContact.java Aug 22, 2012 11:00:44 AM azatsarynnyy $
 */
public interface GoogleContact {
    /**
     * Returns the unique identifier for this contact.
     *
     * @return
     */
    String getId();

    /**
     * Sets the contact identifier.
     *
     * @param id
     *         the contact identifier
     */
    void setId(String id);

    /**
     * Returns the contact name.
     *
     * @return the contact name
     */
    String getName();

    /**
     * Sets the contact name.
     *
     * @param name
     *         the contact name to set
     */
    void setName(String name);

    /**
     * Returns the contact e-mail addresses.
     *
     * @return the contact e-mail addresses
     */
    List<String> getEmailAddresses();

    /**
     * Sets the contact e-mail addresses.
     *
     * @param email
     *         the contact e-mail addresses to set
     */
    void setEmailAddresses(List<String> emailAddresses);

    /**
     * Returns the contact photo as a Base64 encoded {@link String}.
     *
     * @return the contact photo as a Base64 encoded {@link String}
     */
    public String getPhotoBase64();

    /**
     * Sets the contact photo as a Base64 encoded {@link String}.
     *
     * @param photoBase64
     *         the contact photo as a Base64 encoded {@link String}
     */
    public void setPhotoBase64(String photoBase64);
}
