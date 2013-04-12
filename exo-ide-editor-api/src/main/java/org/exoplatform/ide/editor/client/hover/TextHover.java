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
package org.exoplatform.ide.editor.client.hover;


import com.google.gwt.user.client.Element;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * Computes the information to be shown in a hover popup which appears on top of
 * the text viewer's text widget when a hover event occurs. If the text hover
 * does not provide information no hover popup is shown.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface TextHover {
    /**
     * Returns the information which should be presented when a hover popup is shown
     * for the specified hover region. The hover region has the same semantics
     * as the region returned by <code>getHoverRegion</code>. If the returned
     * information is <code>null</code> or empty no hover popup will be shown.
     *
     * @param editor
     *         the viewer on which the hover popup should be shown
     * @param hoverRegion
     *         the text range in the viewer which is used to determine
     *         the hover display information
     * @return the hover popup display information, or <code>null</code> if none available
     */
    Element getHoverInfo(Editor editor, IRegion hoverRegion);

    /**
     * Returns the text region which should serve as the source of information
     * to compute the hover popup display information. The popup has been requested
     * for the given offset.<p>
     * For example, if hover information can be provided on a per method basis in a
     * source viewer, the offset should be used to find the enclosing method and the
     * source range of the method should be returned.
     *
     * @param editor
     *         the viewer on which the hover popup should be shown
     * @param offset
     *         the offset for which the hover request has been issued
     * @return the hover region used to compute the hover display information
     */
    IRegion getHoverRegion(Editor editor, int offset);
}
