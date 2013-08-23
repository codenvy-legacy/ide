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
package org.exoplatform.ide.client.edit.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Class, that implements this handler, will listen to click on Lock/Unlock button on toolbar.
 *
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public interface LockFileHandler extends EventHandler {

    /**
     * @param event
     *         - LockFileEvent generated after pressing on Lock/Unlock button on toolbar
     */
    void onLockFile(LockFileEvent event);

}
