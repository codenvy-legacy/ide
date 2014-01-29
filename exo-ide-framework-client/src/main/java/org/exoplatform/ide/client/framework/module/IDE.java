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
package org.exoplatform.ide.client.framework.module;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.outline.OutlineItemCreator;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.workspaceinfo.CurrentWorkspaceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: IDE Feb 4, 2011 11:01:38 AM evgen $
 */
public abstract class IDE {

    private static IDE instance;

    public static UserInfo user;
    
    public static CurrentWorkspaceInfo currentWorkspace;
    
    private static List<Extension> extensions = new ArrayList<Extension>();

    private static HandlerManager eventBus = new SafeHandlerManager();

    /** Message bus for communicate over WebSocket. */
    private static MessageBus wsMessageBus;

    /** @return the instance */
    public static IDE getInstance() {
        return instance;
    }

    /**
     * Get list of registered extensions.
     *
     * @return list of registered extensions
     */
    public static List<Extension> getExtensions() {
        return extensions;
    }

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    protected IDE() {
        instance = this;
    }

    /**
     * Returns EventBus.
     *
     * @return EventBus.
     */
    public static HandlerManager eventBus() {
        return eventBus;
    }

    /**
     * Add handler to EventBus.
     *
     * @param type
     * @param handler
     * @return
     */
    public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, final H handler) {
        return eventBus.addHandler(type, handler);
    }

    /**
     * Remove handler from EventBus.
     *
     * @param type
     * @param handler
     */
    public static <H extends EventHandler> void removeHandler(GwtEvent.Type<H> type, final H handler) {
        eventBus.removeHandler(type, handler);
    }

    /**
     * Fire event to EventBus.
     *
     * @param event
     */
    public static void fireEvent(GwtEvent<?> event) {
        eventBus.fireEvent(event);
    }

    /**
     * Returns EventBus.
     *
     * @return EventBus.
     */
    public static void setMessageBus(MessageBus messageBus) {
        wsMessageBus = messageBus;
    }

    /**
     * Returns EventBus.
     *
     * @return EventBus.
     */
    public static MessageBus messageBus() {
        return wsMessageBus;
    }

    /**
     * Add control to main menu/tool bar or status bar
     *
     * @param control
     * @param docking
     *         where control dock(toolbar/statusbar)
     */
    public abstract void addControl(Control<?> control, Docking docking);

    /**
     * Add control to main menu
     *
     * @param control
     *         control to be added
     */
    public abstract void addControl(Control<?> control);

    /**
     * Add formatter for IDE controls.
     *
     * @param controlsFormatter
     *         formatter to be added
     */
    public abstract void addControlsFormatter(ControlsFormatter controlsFormatter);

    /**
     * Get list of controls.
     *
     * @return
     */
    public abstract List<Control> getControls();

    /**
     * Open {@link View}
     *
     * @param view
     *         to open
     */
    public abstract void openView(View view);

    /**
     * Close {@link View}
     *
     * @param viewId
     *         ID of view
     */
    public abstract void closeView(String viewId);

    /**
     * Returns FileTypeRegistry.
     *
     * @return
     */
    public abstract FileTypeRegistry getFileTypeRegistry();

    /**
     * Add new outline item creator extension
     *
     * @param outlineItemCreator
     */
    public abstract void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator);

    /**
     * Get OutlineItemCreator for mimeType
     *
     * @param mimeType
     *         of file
     * @return {@link OutlineItemCreator} for mimeType
     */
    public abstract OutlineItemCreator getOutlineItemCreator(String mimeType);

    public abstract List<PaaS> getPaaSes();

    public abstract void registerPaaS(PaaS paas);

    /**
     * Returns <b>true</b> whether current user has "developer" or "admin" role, <b>false</b> otherwise.
     * 
     * @return <b>true</b> is user has "developer" of "admin" role, <b>false</b> otherwise
     */
    public static boolean isRoUser() {
        if (user == null)
            return true;
        return !user.getRoles().contains("developer") && !user.getRoles().contains("admin");
    }
    
    /**
     * Determines is current user temporary.
     * 
     * @return <b>true</b> if the user is temporary, <b>false</b> otherwise
     */
    public static boolean isTemporaryUser() {
        if (user == null)
            return true;
        return user.isTemporary();
    }
    
    /**
     * Returns full name of current user.
     * 
     * @return full name of current user
     */
    public static String getUserFullName() {
        if (user == null) {
            return "";
        }
        
        String fullName = "";
        if (user.getFirstName() != null) {
            fullName = user.getFirstName();
        }
        
        if (user.getLastName() != null) {
            fullName += (fullName.isEmpty() ? "" : " ") + user.getLastName();
        }
        
        return fullName;
    }
    
    /*
     * Send message to special status changed listener.
     * Uses for communication IDE with other JavaScript objects on a page.
     */
    public static native void notifyStatusChanged(String message) /*-{
        try {
            if ($wnd["ide-status-changed-listener"]) {
                $wnd["ide-status-changed-listener"](message);
            }
        } catch (e) {
            console.log(e.message);
        }
    }-*/;
    
}
