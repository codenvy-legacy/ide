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
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
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
    public interface Bundle extends ClientBundle {
        @Source("com/codenvy/ide/codesrv")
        ExternalTextResource codesrvAddress();
    }

    @Inject
    public UpdateExtensionAction(Resources resources) {
        super("Update", "Update launched extension", resources.file());
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Bundle bundle = GWT.create(Bundle.class);
        try {
            bundle.codesrvAddress().getText(new ResourceCallback<TextResource>() {
                @Override
                public void onSuccess(TextResource resource) {
                    if (resource.getText() == null || resource.getText().isEmpty()) {
                        update(Window.Location.getHostName(), "9876");
                    } else {
                        final String[] codeSrvAddress = resource.getText().split(":");
                        update(codeSrvAddress[0].isEmpty() ? Window.Location.getHostName() : codeSrvAddress[0], codeSrvAddress[1]);
                    }
                }

                @Override
                public void onError(ResourceException e) {
                    Log.error(getClass(), e.getMessage());
                }
            });
        } catch (ResourceException ex) {
            Log.error(getClass(), ex.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(!Window.Location.getPort().equals("8080"));
    }

    /** Update already launched Codenvy application with a custom extension. */
    public static native void update(String host, String port)
    /*-{
        $wnd.__gwt_bookmarklet_params = {server_url: 'http://' + host + ':' + port + '/', module_name: '_app'};
        var s = $doc.createElement('script');
        s.src = 'http://' + host + ':' + port + '/dev_mode_on.js';
        void($doc.getElementsByTagName('head')[0].appendChild(s));
    }-*/;
}
