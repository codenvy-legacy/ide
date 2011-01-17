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
   /**
    * XPath locator for main form of IDE. Non-smartGWt.
    */
   public final static String MAIN_FORM_LOCATOR = "//div[@eventproxy = 'ideHorizontalSplitLayout' and @class='normal']";
   
   /**
    * XPath locator for operation panel of IDE. Non-smartGWt.
    */
   public final static String OPERATION_PANEL_LOCATOR = "//div[@eventproxy='ideOperationPanel' and @class='normal']";
   
   /**
    * XPath locator for content (editor) panel of IDE. Non-smartGWt.
    */
   public final static String EDITOR_PANEL_LOCATOR = "//div[@eventproxy='ideEditorPanel' and @class='normal']";
   
   /**
    * XPath locator for navigation panel of IDE. Non-smartGWt.
    */
   public final static String NAVIGATION_PANEL_LOCATOR = "//div[@eventproxy='ideNavigationPanel' and @class='normal']";
   
   /**
    * XPath locator for code helper (outline, versions) panel of IDE. Non-smartGWt.
    */
   public final static String CODE_HELPER_PANEL_LOCATOR = "//div[@eventproxy='ideCodeHelperPanel' and @class='normal']";

   public static final String VERTICAL_SPLIT_LAYOUT_LOCATOR = "//div[@eventproxy='ideVerticalSplitLayout' and @class='normal']";
   
   /**
    * SmartGWT locator for editor tabset.
    */
   public static final String SC_EDITOR_TABSET_LOCATOR = "scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]";
   
   /**
    * XPath locator for status bar of IDE. Non-smartGWt.
    */
   public static final String STATUS_BAR_LOCATOR = "//td[@class='exo-statusText-table-middle']/nobr";
   
   /**
    * Locator-suffix for close icon of different tabs.<p/>
    * 
    * Add this locator to you tab locator to get locator for close icon.<p/>
    * 
    * E.g. to close Outline panel use this command:
    * <p/>
    * <code>selenium.click(Locators.CodeHelperPanel.SC_OUTLINE_TAB_LOCATOR + Locators.CLOSE_ICON);</code>
    */
   public static final String CLOSE_ICON = "/icon";
   
   
   /**
    * Locators for forms and tabs of operation panel.
    */
   public interface OperationForm
   {
      public static final String OPERATION_TABSET_LOCATOR = "scLocator=//TabSet[ID=\"ideOperationPanel\"]";
      
      public static final String PROPERTIES_FORM_LOCATOR = "scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]";
      
      public static final String PROPERTIES_TAB_LOCATOR = OPERATION_TABSET_LOCATOR + "/tab[ID=idePropertiesView]";
      
      public static final String OUTPUT_TAB_LOCATOR = OPERATION_TABSET_LOCATOR + "/tab[ID=Output]";
      
      /**
       * Form, where notifications are shown.
       */
      public static final String OUTPUT_FORM_LOCATOR = "//div[@eventproxy='ideOutputForm']";
   }
   
   /**
    * Locators for Go to line dialog window.
    */
   public interface GoToLineWindow
   {
      public static final String GOTO_LINE_FORM_TEXT_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"ideGoToLineFormDynamicForm\"]/item[name=ideGoToLineFormLineNumberField||title=ideGoToLineFormLineNumberField||index=2||Class=TextItem]/element";
      
      public static final String GOTO_LINE_FORM_GO_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideGoToLineFormGoButton\"]/";
   }
   
   /**
    * Locators for elements from code helper panel.
    */
   public interface CodeHelperPanel
   {
      /**
       * SmartGWT locator for code helper tabset.
       */
      public static final String SC_CODE_HELPER_TABSET_LOCATOR = "scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]";
      
      /**
       * SmartGWT locator for outline tab.
       */
      public static final String SC_OUTLINE_TAB_LOCATOR = SC_CODE_HELPER_TABSET_LOCATOR + "/tab[ID=ideOutlineForm]";
      
      /**
       * SmartGWT locator for versions tab.
       */
      public static final String SC_VERSION_TAB_LOCATOR = SC_CODE_HELPER_TABSET_LOCATOR + "/tab[ID=ideVersionContentPanel]";
      
      /**
       * Non smartGWT locator for outline tab.
       */
      public static final String XPATH_OUTLINE_TAB_LOCATOR = "//div[@eventproxy='ideOutlineForm']";
      
      /**
       * Non smartGWT locator for versions tab.
       */
      public static final String XPATH_VERSION_TAB_LOCATOR = "//div[@eventproxy='ideVersionContentPanel']";
   }
   
   /**
    * Return locator for close icon of tab (tab with file) in editor tabset.
    * 
    * @param index - index of editor tab (numeration start with 0).
    * 
    * @return {@link String}
    */
   public static String getTabCloseButtonLocator(int index)
   {
      return SC_EDITOR_TABSET_LOCATOR + "/tab[index=" + index + "]/icon";
   }
   
   /**
    * Locators for components of Ask for value dialog window.
    * 
    * E.g. when you try to Save As new file.
    */
   public interface AskForValue
   {
      public static final String ASK_FOR_VALUE_DIALOG_LOCATOR = "scLocator=//Window[ID=\"ideAskForValueDialog\"]/";
      
      public static final String ASK_FOR_VALUE_OK_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/";

      public static final String ASK_FOR_VALUE_NO_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogNoButton\"]/";

      public static final String ASK_FOR_VALUE_CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/";

      public static final String ASK_FOR_VALUE_TEXT_FIELD_LOCATOR = ASK_FOR_VALUE_DIALOG_LOCATOR
         + "item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField]/element";
   }
   
   /**
    * Locators for elements from Rename Item dialog window.
    */
   public interface RenameItemForm
   {
      public static final String SC_RENAME_ITEM_WINDOW_LOCATOR = "scLocator=//Window[ID=\"ideRenameItemForm\"]";
      
      public static final String SC_RENAME_ITEM_DYNAMIC_FORM_LOCATOR = "scLocator=//DynamicForm[ID=\"ideRenameItemFormDynamicForm\"]";
      
      public static final String SC_NAME_FIELD_LOCATOR = SC_RENAME_ITEM_DYNAMIC_FORM_LOCATOR 
         + "/item[name=ideRenameItemFormRenameField]/element";
      
      public static final String SC_MIME_TYPE_FIELD_LOCATOR = SC_RENAME_ITEM_DYNAMIC_FORM_LOCATOR 
         + "/item[name=ideRenameItemFormMimeTypeField]/element";
      
      public static final String SC_RENAME_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideRenameItemFormRenameButton\"]";
      
      public static final String SC_CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideRenameItemFormCancelButton\"]";
      
      public static final String RENAME_ITEN_WINDOW_LOCATOR = "//div[@eventproxy='ideRenameItemForm']";
      
      /**
       * Non-smartGWT locator for disabled mime-type field.
       */
      public static final String MIME_TYPE_FIELD_DISABLED_LOCATOR = RENAME_ITEN_WINDOW_LOCATOR 
         + "//input[@class='selectItemTextDisabled' and @name='ideRenameItemFormMimeTypeField']";
      
      /**
       * Non-smartGWT locator for mime-type field.
       */
      public static final String MIME_TYPE_FIELD_LOCATOR = RENAME_ITEN_WINDOW_LOCATOR
         + "//input[@name='ideRenameItemFormMimeTypeField']";
   }
   
   public interface PropertiesPanel
   {
      public static final String SC_DYNAMIC_FORM_LOCATOR = "scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]";

      public static final String SC_CONTENT_TYPE_TEXTBOX = SC_DYNAMIC_FORM_LOCATOR
         + "/item[name=idePropertiesTextContentType]/textbox";

      public static final String SC_CONTENT_NODE_TYPE_TEXTBOX = SC_DYNAMIC_FORM_LOCATOR
         + "/item[name=idePropertiesTextContentNodeType]/textbox";
      
      public static final String SC_FILE_NODE_TYPE_TEXTBOX = SC_DYNAMIC_FORM_LOCATOR
         + "/item[name=idePropertiesTextFileNodeType]/textbox";

      public static final String SC_CONTENT_LENGTH_TEXTBOX = SC_DYNAMIC_FORM_LOCATOR
         + "/item[name=idePropertiesTextContentLength]/textbox";
      
      public static final String SC_DISPLAY_NAME_TEXTBOX = SC_DYNAMIC_FORM_LOCATOR
         + "/item[name=idePropertiesTextDisplayName]/textbox";
      
   }
   
   public interface DeleteForm
   {
      public static final String SC_DELETE_FORM = "scLocator=//Window[ID=\"ideDeleteItemForm\"]/";
   }
}
