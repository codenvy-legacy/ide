package org.exoplatform.ide.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import org.exoplatform.ide.client.inject.IDEInjector;
import org.exoplatform.ide.client.workspace.WorkspacePeresenter;

/**
 * The EntryPoint of the IDE application 
 */
public class IDE implements EntryPoint
{
   // TODO : to be replaced by Application Controller invocation

   /**
    * This is the entry point method.
    */
   public void onModuleLoad()
   {
      IDEInjector injector = GWT.create(IDEInjector.class);
      WorkspacePeresenter workspacePeresenter = injector.getWorkspacePeresenter();
      workspacePeresenter.go(RootLayoutPanel.get());
   }
}
