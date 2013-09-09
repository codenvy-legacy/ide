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

package org.exoplatform.ide.client.navigation;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NavigationMenuGroup extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Window/Navigation";

    public static final String TITLE = "Navigation";

    public NavigationMenuGroup() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setGroupName(GroupNames.NAVIGATION);
    }

    @Override
    public void initialize() {
        setVisible(true);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() != null) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

}
