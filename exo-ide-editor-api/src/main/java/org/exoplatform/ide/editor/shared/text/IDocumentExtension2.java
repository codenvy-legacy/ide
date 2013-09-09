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
package org.exoplatform.ide.editor.shared.text;

/**
 * Extension interface for {@link org.eclipse.jface.text.IDocument}.
 * <p/>
 * <p/>
 * It adds configuration methods to post notification replaces and document listener notification.
 *
 * @since 2.1
 */
public interface IDocumentExtension2 {

    /**
     * Tells the receiver to ignore calls to <code>registerPostNotificationReplace</code> until
     * <code>acceptPostNotificationReplaces</code> is called.
     */
    void ignorePostNotificationReplaces();

    /**
     * Tells the receiver to accept calls to <code>registerPostNotificationReplace</code> until
     * <code>ignorePostNotificationReplaces</code> is called.
     */
    void acceptPostNotificationReplaces();

    /**
     * Can be called prior to a <code>replace</code> operation. After the <code>replace</code>
     * <code>resumeListenerNotification</code> must be called. The effect of these calls is that no document listener is notified
     * until <code>resumeListenerNotification</code> is called. This allows clients to update structure before any listener is
     * informed about the change.
     * <p/>
     * Listener notification can only be stopped for a single <code>replace</code> operation. Otherwise, document change
     * notifications will be lost.
     */
    void stopListenerNotification();

    /**
     * Resumes the notification of document listeners which must previously have been stopped by a call to
     * <code>stopListenerNotification</code>.
     */
    void resumeListenerNotification();
}
