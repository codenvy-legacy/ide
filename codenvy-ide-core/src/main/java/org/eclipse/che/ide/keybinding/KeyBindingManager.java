/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.keybinding;

import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.EventListener;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.keybinding.KeyBindingAgent;
import org.eclipse.che.ide.api.keybinding.Scheme;
import org.eclipse.che.ide.collections.Array;

import org.eclipse.che.ide.toolbar.PresentationFactory;

import org.eclipse.che.ide.util.browser.UserAgent;
import org.eclipse.che.ide.util.dom.Elements;
import org.eclipse.che.ide.util.input.CharCodeWithModifiers;
import org.eclipse.che.ide.util.input.SignalEvent;
import org.eclipse.che.ide.util.input.SignalEventUtils;

import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implementation of the {@link KeyBindingAgent}.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyBindingManager implements KeyBindingAgent {

    private final PresentationFactory presentationFactory;
    private final EventListener downListener = new EventListener() {
        @Override
        public void handleEvent(Event event) {
            SignalEvent signalEvent = SignalEventUtils.create(event, false);
            if (signalEvent == null) {
                return;
            }
            //handle event in active scheme

            int digest = CharCodeWithModifiers.computeKeyDigest(signalEvent);
            Array<String> actionIds = activeScheme.getActionIds(digest);
            if (!actionIds.isEmpty()) {
                runActions(actionIds);
                event.preventDefault();
                event.stopPropagation();
            }
            //else handle event in global scheme
            else if (!(actionIds = globalScheme.getActionIds(digest)).isEmpty()) {
                runActions(actionIds);
                event.preventDefault();
                event.stopPropagation();
            }
            //default, lets this event handle other part of the IDE
        }
    };
    private SchemeImpl    globalScheme;
    private SchemeImpl    activeScheme;
    private SchemeImpl    eclipseScheme;
    private ActionManager actionManager;

    @Inject
    public KeyBindingManager(ActionManager actionManager) {
        this.actionManager = actionManager;
        globalScheme = new SchemeImpl("ide.ui.keyBinding.global", "Global");
        eclipseScheme = new SchemeImpl("ide.ui.keyBinding.eclipse", "Eclipse Scheme");
        //TODO check user settings
        activeScheme = eclipseScheme;

        presentationFactory = new PresentationFactory();

        // Attach the listeners.
        final Element documentElement = Elements.getDocument().getDocumentElement();
        if (UserAgent.isFirefox()) {
            // firefox fiers keypress events
            documentElement.addEventListener(Event.KEYPRESS, downListener, true);
        } else {
            //webkit browsers fiers keydown events
            documentElement.addEventListener(Event.KEYDOWN, downListener, true);
        }
    }

    private void runActions(Array<String> actionIds) {
        for (String actionId : actionIds.asIterable()) {
            Action action = actionManager.getAction(actionId);
            ActionEvent e = new ActionEvent("", presentationFactory.getPresentation(action), actionManager, 0);
            action.update(e);
            if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
                action.actionPerformed(e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Scheme getGlobal() {
        return globalScheme;
    }

    /** {@inheritDoc} */
    @Override
    public Scheme getEclipse() {
        return eclipseScheme;
    }

    @Override
    public Scheme getActive() {
        return activeScheme;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public CharCodeWithModifiers getKeyBinding(@Nonnull String actionId) {
        CharCodeWithModifiers keyBinding = activeScheme.getKeyBinding(actionId);
        if (keyBinding != null)
            return keyBinding;
        else {
            return globalScheme.getKeyBinding(actionId);
        }
    }
}
