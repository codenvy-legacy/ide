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
package org.eclipse.che.ide.api.action;

import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Container for the information necessary to execute or update an {@link Action}.
 *
 * @author Evgen Vidolob
 * @see Action#actionPerformed(ActionEvent)
 * @see Action#update(ActionEvent)
 */
public class ActionEvent {
    private static final String            ourInjectedPrefix = "$injected$.";
    private static final StringMap<String> ourInjectedIds    = Collections.createStringMap();
    private final ActionManager       myActionManager;
    private final String              myPlace;
    private final Presentation        myPresentation;
    private final int                 myModifiers;
    private final Map<String, String> myParameters;
    private       boolean             myWorksInInjected;

    /**
     * Create new action event.
     *
     * @param place
     *         the identifier of the place in the IDE UI from where the action is invoked or updated
     * @param presentation
     *         the presentation which represents the action in the place from where it is invoked or updated
     * @param actionManager
     *         the manager for actions
     * @param modifiers
     *         the modifier keys held down during this action event
     */
    public ActionEvent(@Nonnull String place,
                       @Nonnull Presentation presentation,
                       @Nonnull ActionManager actionManager,
                       int modifiers) {
        this(place, presentation, actionManager, modifiers, null);
    }

    /**
     * Create new action event.
     *
     * @param place
     *         the identifier of the place in the IDE UI from where the action is invoked or updated
     * @param presentation
     *         the presentation which represents the action in the place from where it is invoked or updated
     * @param actionManager
     *         the manager for actions
     * @param modifiers
     *         the modifier keys held down during this action event
     * @param parameters
     *         the parameters with which the action is invoked or updated
     */
    public ActionEvent(@Nonnull String place,
                       @Nonnull Presentation presentation,
                       @Nonnull ActionManager actionManager,
                       int modifiers,
                       @Nullable Map<String, String> parameters) {
        myActionManager = actionManager;
        myPlace = place;
        myPresentation = presentation;
        myModifiers = modifiers;
        myParameters = parameters;
    }

    public static String injectedId(String dataId) {
        synchronized (ourInjectedIds) {
            String injected = ourInjectedIds.get(dataId);
            if (injected == null) {
                injected = ourInjectedPrefix + dataId;
                ourInjectedIds.put(dataId, injected);
            }
            return injected;
        }
    }

    public static String uninjectedId(String dataId) {
        return StringUtils.trimStart(dataId, ourInjectedPrefix);
    }

    /**
     * Returns the identifier of the place in the IDE user interface from where the action is invoked or updated.
     *
     * @return the place identifier
     * @see ActionPlaces
     */
    public String getPlace() {
        return myPlace;
    }

    /**
     * Returns the presentation which represents the action in the place from where it is invoked or updated.
     *
     * @return the presentation instance
     */
    public Presentation getPresentation() {
        return myPresentation;
    }

    /**
     * Returns the modifier keys held down during this action event.
     *
     * @return the modifier keys
     */
    public int getModifiers() {
        return myModifiers;
    }

    /**
     * Returns the parameters with which the action is invoked or updated.
     *
     * @return action's parameters
     */
    @Nullable
    public Map<String, String> getParameters() {
        return myParameters;
    }

    public ActionManager getActionManager() {
        return myActionManager;
    }

    public void setInjectedContext(boolean worksInInjected) {
        myWorksInInjected = worksInInjected;
    }

    public boolean isInInjectedContext() {
        return myWorksInInjected;
    }
}
