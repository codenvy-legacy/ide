/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.myextension.action;


import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

public class MyAction extends Action {
    private IconRegistry iconRegistry;


    /**
     * Define a constructor and pass over text to be displayed in the dialogue box
     */

    @Inject
    public MyAction(IconRegistry iconRegistry) {
        super("Show Image");
        this.iconRegistry = iconRegistry;
    }

    /**
     * Define the action required when calling this method. In our case it'll open a dialogue box with defined Image
     */

    @Override
    public void actionPerformed(ActionEvent arg0) {
        PopupPanel popup = new PopupPanel(true);
        popup.add(iconRegistry.getIcon("my.icon").getImage());
        popup.center();
        popup.show();


    }
}
