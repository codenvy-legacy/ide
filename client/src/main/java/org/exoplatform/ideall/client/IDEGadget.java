package org.exoplatform.ideall.client;

import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
@ModulePrefs(title = "DevTool", author = "eXo Platform", author_email = "info@exoplatform.com.ua", height = 500, description = "DevTool")
public class IDEGadget extends Gadget<UserPreferences>
{

   @Override
   protected void init(UserPreferences preferences)
   {
      Utils.expandGadgetWidth();
      new IDE();
   }

}
