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
