package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerEvent;

import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DebuggerExtension extends Extension
{

   public static final DebuggerAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(DebuggerAutoBeanFactory.class);

   public static String DEBUG_ID;

   /**
    * 
    */
   public static final DebuggerLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(DebuggerLocalizationConstant.class);

   @Override
   public void initialize()
   {
      IDE.getInstance().addControl(new LaunchDebuggerControl());
      DebuggerPresenter debuggerPresenter = new DebuggerPresenter();

      IDE.addHandler(LaunchDebuggerEvent.TYPE, debuggerPresenter);
      IDE.addHandler(DebuggerConnectedEvent.TYPE, debuggerPresenter);
   }

}
