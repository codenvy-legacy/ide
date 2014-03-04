package com.codenvy.ide.ext.helloworld.action;

/**
 * As usual, importing resources, related to Action API.
 * The 3rd import is required to call a default alert box.
 */
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

public class HelloWorldAction extends Action
{
    /**
     * Define a constructor and pass over text to be displayed in the dialogue box
     */

    @Inject
    public HelloWorldAction() {
        super("Hello World");
    }

    /**
     * Define the action required when calling this method. In our case it'll open a dialogue box with 'Hello world'
     */

    @Override
    public void actionPerformed(ActionEvent arg0) {
        Window.alert("Hello world");
    }
}
