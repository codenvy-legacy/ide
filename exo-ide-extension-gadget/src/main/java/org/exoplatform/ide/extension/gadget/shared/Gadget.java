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
