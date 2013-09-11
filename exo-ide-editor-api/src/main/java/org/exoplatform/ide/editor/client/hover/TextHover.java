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
