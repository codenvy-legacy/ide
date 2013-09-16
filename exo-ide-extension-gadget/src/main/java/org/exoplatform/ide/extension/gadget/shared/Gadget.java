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

import java.util.List;

/**
 * Representation of OpenSocial Gadget.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Gedget.java Mar 21, 2012 3:34:23 PM azatsarynnyy $
 */
public interface Gadget {

    /**
     * Returns {@link List} of gadgets.
     *
     * @return {@link List} of gadgets.
     */
    List<GadgetMetadata> getGadgets();

    /**
     * Set the {@link List} of gadgets.
     *
     * @param gadgetMetadata
     *         {@link List} of gadgets.
     */
    void setGadgets(List<GadgetMetadata> gadgetMetadata);
}
