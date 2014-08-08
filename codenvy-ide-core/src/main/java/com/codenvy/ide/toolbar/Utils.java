/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.Separator;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.util.loging.Log;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Utils {

    /**
     * @param list
     *         this list contains expanded actions.
     * @param actionManager
     *         manager
     */
    public static void expandActionGroup(@NotNull ActionGroup group,
                                         Array<Action> list,
                                         PresentationFactory presentationFactory,
                                         @NotNull String place,
                                         ActionManager actionManager,
                                         boolean transparentOnly) {
        Presentation presentation = presentationFactory.getPresentation(group);
        ActionEvent e = new ActionEvent(
                place,
                presentation,
                actionManager,
                0
        );
        if (!doUpdate(group, e, presentation)) return;

        if (!presentation.isVisible()) { // don't process invisible groups
            return;
        }
        Action[] children = group.getChildren(e);
        for (int i = 0; i < children.length; i++) {
            Action child = children[i];
            if (child == null) {
                String groupId = actionManager.getId(group);
                Log.error(Utils.class, "action is null: i=" + i + " group=" + group + " group id=" + groupId);
                continue;
            }

            presentation = presentationFactory.getPresentation(child);
            ActionEvent e1 = new ActionEvent(place, presentation, actionManager, 0);
//            e1.setInjectedContext(child.isInInjectedContext());

            if (transparentOnly && child.isTransparentUpdate() || !transparentOnly) {
                if (!doUpdate(child, e1, presentation)) continue;
            }

            if (!presentation.isVisible()) { // don't create invisible items in the menu
                continue;
            }
            if (child instanceof ActionGroup) {
                ActionGroup actionGroup = (ActionGroup)child;
                if (actionGroup.isPopup()) { // popup menu has its own presentation
                    if (actionGroup.disableIfNoVisibleChildren()) {
                        final boolean visibleChildren = hasVisibleChildren(actionGroup, presentationFactory, actionManager, place);
                        if (actionGroup.hideIfNoVisibleChildren() && !visibleChildren) {
                            continue;
                        }
                        presentation.setEnabled(actionGroup.canBePerformed() || visibleChildren);
                    }

                    list.add(child);
                } else {
                    expandActionGroup((ActionGroup)child, list, presentationFactory, place, actionManager);
                }
            } else if (child instanceof Separator) {
                if (!list.isEmpty() && !(list.get(list.size() - 1) instanceof Separator)) {
                    list.add(child);
                }
            } else {
                list.add(child);
            }
        }
    }


    /**
     * @param list
     *         this list contains expanded actions.
     * @param actionManager
     *         manager
     */
    public static void expandActionGroup(@NotNull ActionGroup group,
                                         Array<Action> list,
                                         PresentationFactory presentationFactory,
                                         String place,
                                         ActionManager actionManager) {
        expandActionGroup(group, list, presentationFactory, place, actionManager, false);
    }

    // returns false if exception was thrown and handled
    private static boolean doUpdate(final Action action, final ActionEvent e, final Presentation presentation) {
//        if (ApplicationManager.getApplication().isDisposed()) return false;

//        long startTime = System.currentTimeMillis();
        final boolean result = true;
//        try {
//            result = !ActionUtil.performDumbAwareUpdate(action, e, false);
//        }
//        catch (ProcessCanceledException ex) {
//            throw ex;
//        }
//        catch (Throwable exc) {
//            handleUpdateException(action, presentation, exc);
//            return false;
//        }
        action.update(e);
//        long endTime = System.currentTimeMillis();
//        if (endTime - startTime > 10) {
//            Log.debug(Utils.class, "Action " + action + ": updated in " + (endTime-startTime) + " ms");
//        }
        return result;
    }

    public static boolean hasVisibleChildren(ActionGroup group, PresentationFactory factory, ActionManager actionManager, String place) {
        ActionEvent event = new ActionEvent(place, factory.getPresentation(group), actionManager, 0);
//        event.setInjectedContext(group.isInInjectedContext());
        for (Action anAction : group.getChildren(event)) {
            if (anAction == null) {
                Log.error(Utils.class, "Null action found in group " + group + ", " + factory.getPresentation(group));
                continue;
            }
            if (anAction instanceof Separator) {
                continue;
            }
//            final Project project = PlatformDataKeys.PROJECT.getData(context);
//            if (project != null && DumbService.getInstance(project).isDumb() && !anAction.isDumbAware()) {
//                continue;
//            }

            final Presentation presentation = factory.getPresentation(anAction);
            updateGroupChild(place, anAction, presentation, actionManager);
            if (anAction instanceof ActionGroup) {
                ActionGroup childGroup = (ActionGroup)anAction;

                // popup menu must be visible itself
                if (childGroup.isPopup()) {
                    if (!presentation.isVisible()) {
                        continue;
                    }
                }

                if (hasVisibleChildren(childGroup, factory, actionManager, place)) {
                    return true;
                }
            } else if (presentation.isVisible()) {
                return true;
            }
        }

        return false;
    }

    public static void updateGroupChild(String place, Action anAction, final Presentation presentation, ActionManager actionManager) {
        ActionEvent event1 = new ActionEvent(place, presentation, actionManager, 0);
//        event1.setInjectedContext(anAction.isInInjectedContext());
        doUpdate(anAction, event1, presentation);
    }
}
