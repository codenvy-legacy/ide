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
package com.codenvy.ide.actions;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to update launched Codenvy application with a custom extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: UpdateExtensionAction.java Jul 3, 2013 1:58:47 PM azatsarynnyy $
 */
@Singleton
public class UpdateExtensionAction extends Action {

    @Inject
    public UpdateExtensionAction(Resources resources) {
        super("Update", "Update launched extension", resources.file());
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(!Window.Location.getPort().equals("8080"));
    }

    /** Update already launched Codenvy application with a custom extension. */
    public static native void update()
    // TODO code server may be binded to port which differs from a default one (9876)
    /*-{
        $wnd.__gwt_bookmarklet_params = {server_url: 'http://localhost:9876/', module_name: 'IDE'};
        var s = $doc.createElement('script');
        s.src = 'http://localhost:9876/dev_mode_on.js';
        void($doc.getElementsByTagName('head')[0].appendChild(s));
    }-*/;
}
