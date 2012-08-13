/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.text;

/**
 * Extension interface for {@link org.eclipse.jface.text.IDocument}.
 * <p>
 * 
 * It adds configuration methods to post notification replaces and document listener notification.
 * 
 * @since 2.1
 */
public interface IDocumentExtension2
{

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
    * <p>
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
