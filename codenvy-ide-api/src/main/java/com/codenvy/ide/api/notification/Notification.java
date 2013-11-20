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
package com.codenvy.ide.api.notification;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

import java.util.Date;

import static com.codenvy.ide.api.notification.Notification.State.READ;
import static com.codenvy.ide.api.notification.Notification.State.UNREAD;
import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Type.*;

/**
 * Presents an entity that reflects the state of a notification.
 * <p/>
 * In order to show a notification you need to create an instance of this class and give it to {@link NotificationManager}. The manager
 * knows how to show and handle it. In case you want to change the notification you will change your own instance and these changes will be
 * take place in view.
 * <p/>
 * The notification makes it possible to delegate some actions in response to opening and closing of a notification. Also the notification
 * has an important state. The notification with this state will be closed only when user clicks 'Close' button. Other notifications
 * (non-important) will be closed after 5 a second time-out.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public final class Notification {
    /** Required for delegating open function in notification. */
    public interface OpenNotificationHandler {
        /** Performs some actions in response to a user's opening a notification */
        void onOpenClicked();
    }

    /** Required for delegating close function in notification. */
    public interface CloseNotificationHandler {
        /** Performs some actions in response to a user's closing a notification */
        void onCloseClicked();
    }

    /** Notification observer. */
    public interface NotificationObserver {
        /** Performs some actions in response to a user's changing something */
        void onValueChanged();
    }

    /** Type of notification. The notification has 3 types: information message, warning message, error message. */
    public enum Type {
        INFO, WARNING, ERROR
    }

    /** Status of a notification. The notification has 2 statuses: a notification is still in progress, and notification is finished. */
    public enum Status {
        PROGRESS, FINISHED
    }

    /** State of notification. The notification has 2 states: a notification is read and a notification is unread. */
    public enum State {
        READ, UNREAD
    }

    private String                          message;
    private Type                            type;
    private Status                          status;
    private State                           state;
    private Date                            time;
    private boolean                         important;
    private OpenNotificationHandler         openHandler;
    private CloseNotificationHandler        closeHandler;
    private JsonArray<NotificationObserver> observers;

    /**
     * Create notification with message and type. Other parameters will be added with default values. This notification has got
     * an unread state, a finished status. It will be a non-important message that does not delegate any actions in response to opening and
     * closing of a notification.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     */
    public Notification(@NotNull String message, @NotNull Type type) {
        this(message, type, null, null);
    }

    /**
     * Create notification with message, type and action delegate on opening of a notification. Other parameters will be added with
     * default values. This notification has got an unread state, a finished status and makes it possible to delegate some action in
     * response to opening of a notification. It will be a non-important message.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param openHandler
     *         delegate that provides some actions when opening notification
     */
    public Notification(@NotNull String message, @NotNull Type type, @NotNull OpenNotificationHandler openHandler) {
        this(message, type, openHandler, null);
    }

    /**
     * Create notification with message, type and action delegate on closing of a notification. Other parameters will be added with
     * default values. This notification has got an unread state, a finished status and makes it possible to delegate some action in
     * response to closing of a notification. It will be a non-important message.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Type type, @NotNull CloseNotificationHandler closeHandler) {
        this(message, type, null, closeHandler);
    }

    /**
     * Create notification with message, type and action delegates on opening and closing of a notification. Other parameters will
     * be added with default values. This notification has got an unread state, a finished status and makes it possible to delegate some
     * action in response to opening and closing of a notification. It will be a non-important message.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param openHandler
     *         delegate that provides some actions when opening notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Type type, @Nullable OpenNotificationHandler openHandler,
                        @Nullable CloseNotificationHandler closeHandler) {
        this(message, type, false, openHandler, closeHandler);
    }

    /**
     * Create notification with message, type and note about important this one. Other parameters will be added with default
     * values. This notification have got an unread state, a finished status. This notification does not delegate any actions in response
     * to opening and closing of a notification.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param important
     *         note about important this notification
     */
    public Notification(@NotNull String message, @NotNull Type type, boolean important) {
        this(message, type, important, null, null);
    }

    /**
     * Create notification with message, type, note about important this one and action delegate on opening of a notification. Other
     * parameters will be added with default values. This notification have got an unread state, a finished status. This notification
     * delegates some actions in response to opening of a notification.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param important
     *         note about important this notification
     * @param openHandler
     *         delegate that provides some actions when opening notification
     */
    public Notification(@NotNull String message, @NotNull Type type, boolean important,
                        @NotNull OpenNotificationHandler openHandler) {
        this(message, type, important, openHandler, null);
    }

    /**
     * Create notification with message, type, note about important this one and action delegate on closing of a notification.
     * Other parameters will be added with default values. This notification have got an unread state, a finished status. This notification
     * delegates some actions in response to closing of a notification.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param important
     *         note about important this notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Type type, boolean important,
                        @NotNull CloseNotificationHandler closeHandler) {
        this(message, type, important, null, closeHandler);
    }

    /**
     * Create notification with message, type, note about important this one and action delegates on opening and closing of a
     * notification. Other parameters will be added with default values. This notification have got an unread state, a finished status.
     * This notification delegates some actions in response to opening and closing of a notification.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param important
     *         note about important this notification
     * @param openHandler
     *         delegate that provides some actions when opening notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Type type, boolean important,
                        @Nullable OpenNotificationHandler openHandler, @Nullable CloseNotificationHandler closeHandler) {
        this(message, type, FINISHED, UNREAD, new Date(), important, openHandler, closeHandler);
    }

    /**
     * Create notification with message and status. Other parameters will be added with default values. This notification have
     * got an unread state, a info type.jr l It will be a non-important message that does not delegate any actions in response to opening
     * and
     * closing of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     */
    public Notification(@NotNull String message, @NotNull Status status) {
        this(message, status, null, null);
    }

    /**
     * Create notification with message, status and action delegate on opening of a notification. Other parameters will be added
     * with default values. This notification have got an unread state, a info type. It will be a non-important message. This notification
     * delegates some actions in response to opening of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param openHandler
     *         delegate that provides some actions when opening notification
     */
    public Notification(@NotNull String message, @NotNull Status status,
                        @NotNull OpenNotificationHandler openHandler) {
        this(message, status, openHandler, null);
    }

    /**
     * Create notification with message, status and action delegate on closing of a notification. Other parameters will be added
     * with default values. This notification have got an unread state, a info type. It will be a non-important message. This notification
     * delegates some actions in response to closing of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Status status,
                        @NotNull CloseNotificationHandler closeHandler) {
        this(message, status, null, closeHandler);
    }

    /**
     * Create notification with message, status and action delegates on opening and closing of a notification. Other parameters will
     * be added with default values. This notification have got an unread state, a info type. It will be a non-important message. This
     * notification delegates some actions in response to opening and closing of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param openHandler
     *         delegate that provides some actions when opening notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Status status, @Nullable OpenNotificationHandler openHandler,
                        @Nullable CloseNotificationHandler closeHandler) {
        this(message, status, false, openHandler, closeHandler);
    }

    /**
     * Create notification with message, status and note about important this one. Other parameters will be added with default
     * values. This notification have got an unread state, a info type. This notification does not delegate any actions in response to
     * opening and closing of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param important
     *         note about important this notification
     */
    public Notification(@NotNull String message, @NotNull Status status, boolean important) {
        this(message, status, important, null, null);
    }

    /**
     * Create notification with message, status, note about important this one and action delegate on opening of a notification.
     * Other parameters will be added with default values. This notification have got an unread state, a info type. This notification
     * delegates some actions in response to opening of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param important
     *         note about important this notification
     * @param openHandler
     *         delegate that provides some actions when opening notification
     */
    public Notification(@NotNull String message, @NotNull Status status, boolean important, @NotNull OpenNotificationHandler openHandler) {
        this(message, INFO, status, UNREAD, new Date(), important, openHandler, null);
    }

    /**
     * Create notification with message, status, note about important this one and action delegate on closing of a notification.
     * Other parameters will be added with default values. This notification have got an unread state, a info type. This notification
     * delegates some actions in response to closing of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param important
     *         note about important this notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Status status, boolean important,
                        @NotNull CloseNotificationHandler closeHandler) {
        this(message, INFO, status, UNREAD, new Date(), important, null, closeHandler);
    }

    /**
     * Create notification with message, status, note about important this one and action delegates on opening and closing of a
     * notification. Other parameters will be added with default values. This notification have got an unread state, a info type. This
     * notification delegates some actions in response to opening and closing of a notification.
     *
     * @param message
     *         notification's message
     * @param status
     *         notification's status
     * @param important
     *         note about important this notification
     * @param openHandler
     *         delegate that provides some actions when opening notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    public Notification(@NotNull String message, @NotNull Status status, boolean important, @Nullable OpenNotificationHandler openHandler,
                        @Nullable CloseNotificationHandler closeHandler) {
        this(message, INFO, status, UNREAD, new Date(), important, openHandler, closeHandler);
    }

    /**
     * Create notification with all parameters.
     *
     * @param message
     *         notification's message
     * @param type
     *         notification's type
     * @param status
     *         notification's status
     * @param state
     *         notification's state
     * @param time
     *         time when this notification was showed
     * @param important
     *         note about important this notification
     * @param openHandler
     *         delegate that provides some actions when opening notification
     * @param closeHandler
     *         delegate that provides some actions when closing notification
     */
    protected Notification(@NotNull String message, @NotNull Type type, @NotNull Status status, @NotNull State state, @NotNull Date time,
                           boolean important, @Nullable OpenNotificationHandler openHandler,
                           @Nullable CloseNotificationHandler closeHandler) {
        this.message = message;
        this.type = type;
        this.status = status;
        this.state = state;
        this.time = time;
        this.important = important;
        this.openHandler = openHandler;
        this.closeHandler = closeHandler;
        this.observers = JsonCollections.createArray();
    }

    /** @return notification's message */
    @NotNull
    public String getMessage() {
        return message;
    }

    /**
     * Set notification's message
     *
     * @param message
     */
    public void setMessage(@NotNull String message) {
        this.message = message;
        this.time = new Date();
        notifyObservers();
    }

    /**
     * Returns whether this notification is a information message.
     *
     * @return <code>true</code> if the notification is information message, and <code>false</code> if it's not
     */
    public boolean isInfo() {
        return type.equals(INFO);
    }

    /**
     * Returns whether this notification is a warning message.
     *
     * @return <code>true</code> if the notification is warning message, and <code>false</code> if it's not
     */
    public boolean isWarning() {
        return type.equals(WARNING);
    }

    /**
     * Returns whether this notification is a error message.
     *
     * @return <code>true</code> if the notification is error message, and <code>false</code> if it's not
     */
    public boolean isError() {
        return type.equals(ERROR);
    }

    /** @return notification's type */
    @NotNull
    public Type getType() {
        return type;
    }

    /**
     * Set notification's type
     *
     * @param type
     */
    public void setType(@NotNull Type type) {
        this.type = type;
        this.time = new Date();
        notifyObservers();
    }

    /**
     * Returns whether this notification is finished.
     *
     * @return <code>true</code> if the notification is finished, and <code>false</code> if it's not
     */
    public boolean isFinished() {
        return status.equals(FINISHED);
    }

    /**
     * Set notification's status
     *
     * @param status
     */
    public void setStatus(@NotNull Status status) {
        this.status = status;
        this.time = new Date();
        notifyObservers();
    }

    /**
     * Returns whether this notification is read.
     *
     * @return <code>true</code> if the notification is read, and <code>false</code> if it's not
     */
    public boolean isRead() {
        return state.equals(READ);
    }

    /**
     * Set notification's state
     *
     * @param state
     */
    public void setState(@NotNull State state) {
        this.state = state;
        notifyObservers();
    }

    /** @return time when this notification was showed */
    @NotNull
    public Date getTime() {
        return time;
    }

    /**
     * Returns whether this notification is important.
     *
     * @return <code>true</code> if the notification is important, and <code>false</code> if it's not
     */
    public boolean isImportant() {
        return important;
    }

    /**
     * Set important note
     *
     * @param important
     */
    public void setImportant(boolean important) {
        this.important = important;
        this.time = new Date();
        notifyObservers();
    }

    /** @return delegate with actions in response on opening notification */
    @Nullable
    public OpenNotificationHandler getOpenHandler() {
        return openHandler;
    }

    /** @return delegate with actions in response on closing notification */
    @Nullable
    public CloseNotificationHandler getCloseHandler() {
        return closeHandler;
    }

    /** @return a clone of this instance */
    @NotNull
    public Notification clone() {
        return new Notification(message, type, status, state, time, important, openHandler, closeHandler);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification)o;

        if (important != that.important) return false;
        if (!message.equals(that.message)) return false;
        if (state != that.state) return false;
        if (status != that.status) return false;
        if (!time.equals(that.time)) return false;
        if (type != that.type) return false;

        return true;
    }

    /**
     * Add a notification's observer.
     *
     * @param observer
     *         observer that need to add
     */
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove a notification's observer.
     *
     * @param observer
     *         observer that need to remove
     */
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    /** Notify observes. */
    public void notifyObservers() {
        for (NotificationObserver observer : observers.asIterable()) {
            observer.onValueChanged();
        }
    }
}