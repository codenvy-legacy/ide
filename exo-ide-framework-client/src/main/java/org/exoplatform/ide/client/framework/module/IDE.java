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

    // /**
    // * Add new editor.
    // *
    // * @param editor
    // */
    // public abstract void addEditor(Editor editor);
    //
    // /**
    // * Returns array of EditorBuilder for mimeType
    // *
    // * @param mimeType of file
    // * @return {@link EditorBuilder} for mimeType
    // * @throws EditorNotFoundException if {@link EditorProducer} not found for mimeType
    // */
    // public abstract Editor[] getEditors(String mimeType) throws EditorNotFoundException;

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
     * Return true if user not "developer" and not "admin" 
     * 
     * @return
     */
    public static boolean isRoUser() {
        if (user == null)
            return true;
        return !user.getRoles().contains("developer") && !user.getRoles().contains("admin");
    }
}
