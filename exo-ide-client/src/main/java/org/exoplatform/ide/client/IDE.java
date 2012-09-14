package org.exoplatform.ide.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.inject.IDEInjector;

/**
 * The EntryPoint of the IDE application 
 */
public class IDE implements EntryPoint
{
   // TODO : to be replaced by Application Controller invocation

   /**
    * This is the entry point method.
    */
   @Override
   public void onModuleLoad()
   {

      IDEInjector injector = GWT.create(IDEInjector.class);
      BootstrapController bootstrap = injector.getBootstrapController();
   }
}
