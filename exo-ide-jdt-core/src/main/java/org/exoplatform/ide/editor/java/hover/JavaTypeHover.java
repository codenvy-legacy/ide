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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.hover.TextHover;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaTypeHover implements TextHover {

    private static final HoverResources resources = GWT.create(HoverResources.class);

    static {
        resources.hover().ensureInjected();
    }

    private ProblemHover problemHover;

    /**
     *
     */
    public JavaTypeHover(HandlerManager eventBus) {
        problemHover = new ProblemHover(eventBus);
    }

    /**
     * @see org.exoplatform.ide.editor.client.hover.TextHover#getHoverInfo(org.exoplatform.ide.editor.client.api.Editor,
     *      org.exoplatform.ide.editor.shared.text.IRegion)
     */
    @Override
    public Element getHoverInfo(Editor editor, IRegion hoverRegion) {
        Element problemElement = problemHover.getHoverInfo(editor, hoverRegion);
        if (problemElement != null)
            return problemElement;

        return null;
    }

    /** @see org.exoplatform.ide.editor.client.hover.TextHover#getHoverRegion(org.exoplatform.ide.editor.client.api.Editor, int) */
    @Override
    public IRegion getHoverRegion(Editor editor, int offset) {
        return problemHover.getHoverRegion(editor, offset);
    }

}
