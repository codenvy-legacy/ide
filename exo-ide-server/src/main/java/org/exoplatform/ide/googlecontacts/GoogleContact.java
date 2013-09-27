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
package org.exoplatform.ide.googlecontacts;

import com.google.gdata.data.extensions.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a Google contact entry.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContact.java Aug 22, 2012 11:00:44 AM azatsarynnyy $
 */
public class GoogleContact {
    /** The unique identifier for this contact. */
    private String id;

    /** The contact name. */
    private String name;

    /** The contact photo as a Base64 encoded {@link String}. */
    private String photoBase64;

    /** The contact e-mail addresses. */
    private List<String> emailAddresses;

    /**
     * Returns the contact identifier.
     *
     * @return the contact identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the contact identifier.
     *
     * @param id
     *         the contact identifier to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the contact name.
     *
     * @return the contact name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the contact name.
     *
     * @param name
     *         the contact name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the contact e-mail addresses.
     *
     * @return the contact e-mail addresses
     */
    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    /**
     * Sets the contact e-mail addresses.
     *
     * @param email
     *         the contact e-mail addresses to set
     */
    public void setEmailAddresses(List<Email> emailAddresses) {
        if (this.emailAddresses == null) {
            this.emailAddresses = new ArrayList<String>();
        }

        this.emailAddresses.clear();

        for (Email email : emailAddresses) {
            this.emailAddresses.add(email.getAddress());
        }
    }

    /**
     * Returns the contact photo as a Base64 encoded {@link String}.
     *
     * @return the contact photo as a Base64 encoded {@link String}
     */
    public String getPhotoBase64() {
        return photoBase64;
    }

    /**
     * Sets the contact photo as a Base64 encoded {@link String}.
     *
     * @param photoBase64
     *         the contact photo as a Base64 encoded {@link String}
     */
    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }
}
