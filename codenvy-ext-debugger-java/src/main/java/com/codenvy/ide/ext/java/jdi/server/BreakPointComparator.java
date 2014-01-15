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
package com.codenvy.ide.ext.java.jdi.server;

import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;

import java.util.Comparator;

/**
 * Helps to order breakpoints by name of location and line number.
 *
 * @author andrew00x
 */
final class BreakPointComparator implements Comparator<BreakPoint> {
    @Override
    public int compare(BreakPoint o1, BreakPoint o2) {
        String className1 = o1.getLocation().getClassName();
        String className2 = o2.getLocation().getClassName();
        if (className1 == null && className2 == null) {
            return 0;
        }
        if (className1 == null) {
            return 1;
        }
        if (className2 == null) {
            return -1;
        }
        int result = className1.compareTo(className2);
        if (result == 0) {
            result = o1.getLocation().getLineNumber() - o2.getLocation().getLineNumber();
        }
        return result;
    }
}
