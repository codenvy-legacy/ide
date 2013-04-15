/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.part;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.ui.perspective.EditorPartStack;
import com.codenvy.ide.api.ui.perspective.PartPresenter;
import com.codenvy.ide.api.ui.perspective.PartStackView;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * EditorPartStackPresenter is a special PartStackPresenter that is shared among all
 * Perspectives and used to display Editors.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class EditorPartStackPresenter extends PartStackPresenter implements EditorPartStack {

    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public EditorPartStackPresenter(@Named("editorPartStack") PartStackView view, EventBus eventBus,
                                    PartStackEventHandler partStackEventHandler) {
        super(eventBus, partStackEventHandler, view, null);
        partsClosable = true;
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(PartPresenter part) {
        if (!(part instanceof EditorPartPresenter)) {
            Log.warn(getClass(), "EditorPartStack is not intended to be used to open non-Editor Parts.");
        }

        if (parts.contains(part)) {
            // part already exists
            // activate it
            setActivePart(part);
            // and return
            return;
        }
        parts.add(part);
        part.addPropertyListener(propertyListener);
        // include close button
        ImageResource titleImage = part.getTitleImage();
        PartStackView.TabItem tabItem =
                view.addTabButton(titleImage == null ? null : new Image(titleImage), part.getTitle(), part.getTitleToolTip(),
                                  partsClosable);
        bindEvents(tabItem, part);
        setActivePart(part);
        // requst focus
        onRequestFocus();
    }

    /** {@inheritDoc} */
    @Override
    public void setActivePart(PartPresenter part) {
        if (!(part instanceof EditorPartPresenter)) {
            Log.warn(getClass(), "EditorPartStack is not intended to be used to open non-Editor Parts.");
        }
        if (activePart == part) {
            return;
        }
        activePart = part;


        AcceptsOneWidget contentPanel = view.getContentPanel();

        if (part == null) {
            view.setActiveTabButton(-1);
        } else {
            view.setActiveTabButton(parts.indexOf(activePart));
            activePart.go(contentPanel);
        }
        // request part stack to get the focus
        onRequestFocus();
        // notify handler, that part changed
        partStackHandler.onActivePartChanged(activePart);

    }

    /**
     * Close Part
     *
     * @param part
     */
    protected void close(PartPresenter part) {
        // may cancel close
        if (part.onClose()) {
            int partIndex = parts.indexOf(part);
            view.removeTabButton(partIndex);
            parts.remove(part);
            part.removePropertyListener(propertyListener);
            if (activePart == part) {
                //select another part
                setActivePart(parts.isEmpty() ? null : parts.get(parts.size()-1));
            }
        }
    }



}
