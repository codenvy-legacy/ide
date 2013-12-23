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
package com.codenvy.ide.keybinding;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Element;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.Scheme;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.util.browser.UserAgent;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.input.SignalEventUtils;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

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
    public CharCodeWithModifiers getKeyBinding(@NotNull String actionId) {
        CharCodeWithModifiers keyBinding = activeScheme.getKeyBinding(actionId);
        if (keyBinding != null)
            return keyBinding;
        else {
            return globalScheme.getKeyBinding(actionId);
        }
    }
}
