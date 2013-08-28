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
