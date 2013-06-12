/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.Presentation;

import java.util.HashMap;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PresentationFactory {
    private final HashMap<Action, Presentation> myAction2Presentation;

    public PresentationFactory() {
        myAction2Presentation = new HashMap<Action, Presentation>();
    }

    public final Presentation getPresentation(@NotNull Action action) {
        Presentation presentation = myAction2Presentation.get(action);
        if (presentation == null) {
            presentation = action.getTemplatePresentation().clone();
            myAction2Presentation.put(action, processPresentation(presentation));
        }
        return presentation;
    }

    protected Presentation processPresentation(Presentation presentation) {
        return presentation;
    }

    public void reset() {
        myAction2Presentation.clear();
    }
}
