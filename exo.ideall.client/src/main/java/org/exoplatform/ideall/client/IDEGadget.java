package org.exoplatform.ideall.client;

import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
@ModulePrefs(title = "IDEall", author = "eXo Platform", author_email = "info@exoplatform.com.ua", height = 500, description = "IDEall")
public class IDEGadget extends Gadget<UserPreferences> implements NeedsDynamicHeight
{
   private DynamicHeightFeature dynamicHeightFeature;
   
   private static Integer defHeight = 470; 
   
   @Override
   protected void init(UserPreferences preferences)
   {
      final IDE ide = new IDE();
      RootPanel.get().add(ide);
      Integer h = getFixHeight();
      if (h!= null) ide.setHeight(h + "px");
      dynamicHeightFeature.adjustHeight();
      
   }
   
   public void initializeFeature(DynamicHeightFeature feature)
   {
    this.dynamicHeightFeature = feature;
   }

   
   //Fix gadget height work only in Gatein
   //get height of parent element in the DOM.  
   private static native String expandGadgetHeight() /*-{
     var y = $wnd.parent.document.getElementById("UIGadgetPortlet").parentNode;
     return y.style.height;
  }-*/;
   
   

   private Integer getFixHeight()
   {
      String height = expandGadgetHeight();
      Integer newHeight;
      if (height.contains("px"))
      {
         try {
            newHeight = Integer.parseInt(height.replace("px", "")) - 30;
         } catch (NumberFormatException e) {
            newHeight = defHeight;
         }
      } else
      {
         try {
            newHeight = Integer.parseInt(height) - 10;
         } catch (NumberFormatException e) {
            newHeight = defHeight;
         }
      }
     return newHeight;  
   }
   
   
}





