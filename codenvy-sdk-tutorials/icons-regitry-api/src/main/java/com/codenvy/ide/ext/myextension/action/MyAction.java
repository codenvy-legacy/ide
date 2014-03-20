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
        popup.add(iconRegistry.getIcon("my.icon"));
        popup.center();
        popup.show();


    }
}
