/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.google.collide.client.document;

import com.google.collide.client.CollabEditorExtension;

import org.exoplatform.ide.client.framework.event.CollaborationChangedEvent;
import org.exoplatform.ide.client.framework.event.CollaborationChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;

/*
* Class is used to clean list of collaboration documents if collaboration mode is disabled.
*
* * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a> */
public class DocumentManagerCleaner implements CollaborationChangedHandler {

    public DocumentManagerCleaner() {
        IDE.addHandler(CollaborationChangedEvent.TYPE, this);
    }

    @Override
    public void onCollaborationChanged(CollaborationChangedEvent event) {
        if (event.isEnabled()) {
           CollabEditorExtension.get().getManager().cleanupBeforeEnabledCollaboration();
        }
    }
}
