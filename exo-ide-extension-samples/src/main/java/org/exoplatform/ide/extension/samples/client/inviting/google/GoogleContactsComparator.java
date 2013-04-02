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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import org.exoplatform.ide.client.framework.invite.GoogleContact;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for ordering Google contacts list alphabetically, by first e-mail.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContactsComparator.java Oct 31, 2012 12:47:56 PM azatsarynnyy $
 */
final class GoogleContactsComparator implements Comparator<GoogleContact> {
    /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
    @Override
    public int compare(GoogleContact c1, GoogleContact c2) {
        List<String> emailAddresses1 = c1.getEmailAddresses();
        List<String> emailAddresses2 = c2.getEmailAddresses();

        if (emailAddresses1 == null || emailAddresses1.isEmpty()) {
            return -1;
        } else if (emailAddresses2 == null || emailAddresses2.isEmpty()) {
            return 1;
        }

        return emailAddresses1.get(0).compareTo(emailAddresses2.get(0));
    }
}
