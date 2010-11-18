/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide;

/**
 * Contains locators of main parts of IDE gadget.
 * 
 * Performs methods to get locators to some of most used parts of IDE,
 * which must be calculated (such as close button of editor tab)
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class Locators
{
   public final static String MAIN_FORM_LOCATOR = "//div[@eventproxy = 'ideHorizontalSplitLayout' and @class='normal']";
   
   public final static String OPERATION_PANEL_LOCATOR = "//div[@eventproxy='ideOperationPanel' and @class='normal']";
   
   public final static String CONTENT_PANEL_LOCATOR = "//div[@eventproxy='ideEditorPanel' and @class='normal']";
   
   public final static String NAVIGATION_PANEL_LOCATOR = "//div[@eventproxy='ideNavigationPanel' and @class='normal']";
   
   public final static String CODE_HELPER_PANEL_LOCATOR = "//div[@eventproxy='ideCodeHelperPanel' and @class='normal']";

   public static final String VERTICAL_SPLIT_LAYOUT_LOCATOR = "//div[@eventproxy='ideVerticalSplitLayout' and @class='normal']";
   
   public static final String EDITOR_TABSET_LOCATOR = "scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/";
   
   public static final String STATUS_BAR_LOCATOR = "//td[@class='exo-statusText-table-middle']/nobr";
   
   public static final String CLOSE_ICON = "/icon";
   
   
   public interface OperationForm
   {
      public static final String OPERATION_TABSET_LOCATOR = "scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]";
      
      public static final String PROPERTIES_FORM_LOCATOR = "scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]";
      
      public static final String PROPERTIES_TAB_LOCATOR = OPERATION_TABSET_LOCATOR + "/tab[ID=idePropertiesView]";
   }
   
   // go to line window locators
   public interface GoToLineWindow
   {
      public static final String GOTO_LINE_FORM_TEXT_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element";
      
      public static final String GOTO_LINE_FORM_GO_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideGoToLineFormGoButton\"]/";
   }
   
   public static String getTabCloseButtonLocator(int index)
   {
      return EDITOR_TABSET_LOCATOR + "tab[index=" + index + "]/icon";
   }
   
   public interface AskForValue
   {
      public static final String ASK_FOR_VALUE_DIALOG_LOCATOR = "scLocator=//Window[ID=\"ideAskForValueDialog\"]/";
      
      public static final String ASK_FOR_VALUE_OK_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/";

      public static final String ASK_FOR_VALUE_NO_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogNoButton\"]/";

      public static final String ASK_FOR_VALUE_CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/";

      public static final String ASK_FOR_VALUE_TEXT_FIELD_LOCATOR = ASK_FOR_VALUE_DIALOG_LOCATOR
         + "item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField]/element";
   }
}
