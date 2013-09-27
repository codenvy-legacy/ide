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
package org.exoplatform.ide.client.debug;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowImagesControl extends SimpleControl implements IDEControl, ViewOpenedHandler, ViewClosedHandler {

    public static final String ID = "Help/Debug/Show Images";

    public ShowImagesControl() {
        super(ID);
        setTitle("Show Images");
        setPrompt("Show Images");
        // setImages(IDEImageBundle.INSTANCE., IDEImageBundle.INSTANCE.propertiesDisabled());
        setEvent(new ShowImagesEvent());
        setVisible(true);
        setEnabled(true);
        setGroupName("Debug");
    }

    public void initialize() {
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
    }

}
