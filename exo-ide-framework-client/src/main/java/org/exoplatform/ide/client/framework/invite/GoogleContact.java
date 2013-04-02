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
