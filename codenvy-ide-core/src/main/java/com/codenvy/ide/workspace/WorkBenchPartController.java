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
package com.codenvy.ide.workspace;

/**
 * This interface give ability part stack manipulate visibility an size in container.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface WorkBenchPartController {

    /**
     * Get part stack size.
     *
     * @return the size
     */
    double getSize();

    /**
     * Set part stack size.
     *
     * @param size
     */
    void setSize(double size);

    /**
     * Show/hide part stack.
     *
     * @param hidden
     */
    void setHidden(boolean hidden);
}
