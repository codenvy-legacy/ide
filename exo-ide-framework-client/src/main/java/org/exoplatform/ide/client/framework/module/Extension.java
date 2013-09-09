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

import com.google.gwt.core.client.EntryPoint;

import org.exoplatform.gwtframework.ui.client.command.Control;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public abstract class Extension implements EntryPoint {

    /** @see com.google.gwt.core.client.EntryPoint#onModuleLoad() */
    @Override
    public void onModuleLoad() {
        IDE.registerExtension(this);
    }

    /**
     * This method called after IDE initialized. In this method you can add controls and views.<br>
     * To add {@link Control} call
     * {@link IDE#addControl(Control, org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget, boolean)}<br>
     * To add Editor call {@link IDE#addEditor(org.exoplatform.ide.editor.api.EditorProducer)}
     */
    public abstract void initialize();

}
