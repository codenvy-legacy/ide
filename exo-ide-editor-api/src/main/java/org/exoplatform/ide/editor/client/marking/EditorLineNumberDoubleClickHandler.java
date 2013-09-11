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
package org.exoplatform.ide.editor.client.marking;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  11:32:21 AM Mar 26, 2012 evgen $
 */
public interface EditorLineNumberDoubleClickHandler extends EventHandler {

    /**
     * Perform actions on line number double click.
     *
     * @param event
     */
    void onEditorLineNumberDoubleClick(EditorLineNumberDoubleClickEvent event);

}
