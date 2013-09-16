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
package org.exoplatform.ide.extension.gadget.shared;

/**
 * Interface describe OpenSocial Gadget views.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Views.java Mar 20, 2012 17:23:11 PM azatsarynnyy $
 */

public interface Views {
    /** @return preferred height */
    public double getPreferredHeight();

    /**
     * @param preferredHeight
     *         height to set
     */
    public void setPreferredHeight(double preferredHeight);

    /** @return preferred width */
    public double getPreferredWidth();

    /**
     * @param preferredWidth
     *         width to set
     */
    public void setPreferredWidth(double preferredWidth);

    /** @return quirks */
    public boolean getQuirks();

    /**
     * @param quirks
     *         quirks to set
     */
    public void setQuirks(boolean quirks);

    /** @return type */
    public String getType();

    /**
     * @param type
     *         type to set
     */
    public void setType(String type);
}