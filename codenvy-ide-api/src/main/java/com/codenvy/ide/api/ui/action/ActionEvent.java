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
package com.codenvy.ide.api.ui.action;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Container for the information necessary to execute or update an {@link Action}.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 * @see Action#actionPerformed(ActionEvent)
 * @see Action#update(ActionEvent)
 */
public class ActionEvent {
    private static final String            ourInjectedPrefix = "$injected$.";
    private static final StringMap<String> ourInjectedIds    = Collections.createStringMap();
    private final ActionManager myActionManager;
    private final String        myPlace;
    private final Presentation  myPresentation;
    private final int           myModifiers;

    /**
     * @throws IllegalArgumentException
     *         if <code>dataContext</code> is <code>null</code> or
     *         <code>place</code> is <code>null</code> or <code>presentation</code> is <code>null</code>
     */
    public ActionEvent(@NotNull String place,
                       @NotNull Presentation presentation,
                       ActionManager actionManager,
                       int modifiers) {
        myActionManager = actionManager;
        myPlace = place;
        myPresentation = presentation;
        myModifiers = modifiers;
    }

    private boolean myWorksInInjected;

//    public static ActionEvent createFromInputEvent(Action action, InputEvent event, String place) {
//        DataContext context =
//                event == null ? DataManager.getInstance().getDataContext() : DataManager.getInstance().getDataContext(event
// .getComponent());
//        int modifiers = event == null ? 0 : event.getModifiers();
//        return new AnActionEvent(
//                event,
//                context,
//                place,
//                action.getTemplatePresentation(),
//                ActionManager.getInstance(),
//                modifiers
//        );
//    }

//    /**
//     * Returns the <code>InputEvent</code> which causes invocation of the action. It might be
//     * <code>KeyEvent</code>, <code>MouseEvent</code>.
//     * @return the <code>InputEvent</code> instance.
//     */
//    public InputEvent getInputEvent() {
//        return myInputEvent;
//    }

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

//    /**
//     * Returns the context which allows to retrieve information about the state of IDE related to
//     * the action invocation (active editor, selection and so on).
//     *
//     * @return the data context instance.
//     */
//    public DataContext getDataContext() {
//        if (!myWorksInInjected) {
//            return myDataContext;
//        }
//        return new DataContext() {
//            @Override
//            @Nullable
//            public Object getData(String dataId) {
//                Object injected = myDataContext.getData(injectedId(dataId));
//                if (injected != null) return injected;
//                return myDataContext.getData(dataId);
//            }
//        };
//    }
//
//    public <T> T getData(DataKey<T> key) {
//        return key.getData(getDataContext());
//    }
//
//    public <T> T getRequiredData(DataKey<T> key) {
//        T data = getData(key);
//        assert data != null;
//        return data;
//    }

    /**
     * Returns the identifier of the place in the IDE user interface from where the action is invoked
     * or updated.
     *
     * @return the place identifier
     * @see ActionPlaces
     */
    public String getPlace() {
        return myPlace;
    }

    /**
     * Returns the presentation which represents the action in the place from where it is invoked
     * or updated.
     *
     * @return the presentation instance.
     */
    public Presentation getPresentation() {
        return myPresentation;
    }

    /**
     * Returns the modifier keys held down during this action event.
     *
     * @return the modifier keys.
     */
    public int getModifiers() {
        return myModifiers;
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

//    public void accept(ActionEventVisitor visitor) {
//        visitor.visitEvent(this);
//    }
}
