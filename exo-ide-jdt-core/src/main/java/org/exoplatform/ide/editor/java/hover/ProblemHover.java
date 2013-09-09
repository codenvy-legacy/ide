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
package org.exoplatform.ide.editor.java.hover;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import org.eclipse.jdt.client.core.compiler.IProblem;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProblemHover extends AbstractJavaHover {

    /** @param eventBus */
    public ProblemHover(HandlerManager eventBus) {
        super(eventBus);
    }

    /**
     * @see org.exoplatform.ide.editor.client.hover.TextHover#getHoverInfo(org.exoplatform.ide.editor.client.api.Editor,
     *      org.exoplatform.ide.editor.shared.text.IRegion)
     */
    @Override
    public Element getHoverInfo(Editor editor, IRegion hoverRegion) {
        IProblem currentProblem = null;
        if (cUnit == null)
            return null;
        for (IProblem p : cUnit.getProblems()) {
            if (p.getSourceStart() <= hoverRegion.getOffset()
                && p.getSourceEnd() >= (hoverRegion.getOffset() + hoverRegion.getLength() - 1)) {
                currentProblem = p;
            }
        }
        if (currentProblem == null)
            return null;
        Element div = DOM.createDiv();
        div.setInnerHTML(currentProblem.getMessage());
        return div;
    }
}
