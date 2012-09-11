package org.exoplatform.ide.core;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:mmusienko@exoplatform.com">Musienko Maksim</a>
 * @version $
 */

public class Preferences extends AbstractTestModule
{
   private interface Locators
   {
      String PREFERNCESS_FORM_ID = "eXoPreferencesView-window";
      
   }

     @FindBy(id = Locators.PREFERNCESS_FORM_ID)
      private WebElement preferenceForm;

}
