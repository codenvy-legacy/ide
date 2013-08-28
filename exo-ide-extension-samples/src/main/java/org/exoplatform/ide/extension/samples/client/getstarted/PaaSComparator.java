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
package org.exoplatform.ide.extension.samples.client.getstarted;

import org.exoplatform.ide.client.framework.paas.PaaS;

import java.util.Comparator;

/**
 * Comparator for ordering PaaSes alphabetically.
 *
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: PaaSComparator.java May 17, 2013 4:47:56 PM vsvydenko $
 */
final class PaaSComparator implements Comparator<PaaS> {
    /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
    @Override
    public int compare(PaaS p1, PaaS p2) {
        String title1 = p1.getTitle();
        String title2 = p2.getTitle();

        return title1.compareTo(title2);
    }
}
