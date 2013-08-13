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
package com.codenvy.ide.part;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.ui.workspace.EditorPartStack;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.util.loging.Log;
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
                setActivePart(parts.isEmpty() ? null : parts.get(parts.size() - 1));
            }
        }
    }


}
