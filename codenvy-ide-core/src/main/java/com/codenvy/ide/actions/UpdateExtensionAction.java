/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.actions;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to already launched update extension.
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
        e.getPresentation().setEnabledAndVisible(Window.Location.getPort().equals("8081"));
    }

    /** Update already launched Codenvy extension. */
    public static native void update()
    /*-{
        $wnd.__gwt_bookmarklet_params = {server_url: 'http://localhost:9876/', module_name: 'IDE'};
        var s = $doc.createElement('script');
        s.src = 'http://localhost:9876/dev_mode_on.js';
        void($doc.getElementsByTagName('head')[0].appendChild(s));
    }-*/;
}
