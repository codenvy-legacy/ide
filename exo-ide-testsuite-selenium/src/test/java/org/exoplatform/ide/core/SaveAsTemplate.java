/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;

/**
 * Class for operations with templates: file and projects from templates.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Templates.java May 6, 2011 12:14:41 PM vereshchaka $
 *
 */
public class SaveAsTemplate extends AbstractTestModule
{
   //------Save as template form elements----------------------
   public static final String SAVE_AS_TEMPLATE_FORM_ID = "ideSaveAsTemplateForm";
   
   public static final String SAVE_AS_TEMPLATE_CANCEL_BUTTON_ID = "ideSaveAsTemplateFormCancelButton";
   
   public static final String SAVE_BUTTON_ID = "ideSaveAsTemplateFormSaveButton";
   
   public static final String SAVE_AS_TEMPLATE_TYPE_FIELD_ID = "ideSaveAsTemplateFormTypeField";
   
   public static final String NAME_FIELD_ID = "ideSaveAsTemplateFormNameField";
   
   public static final String SAVE_AS_TEMPLATE_DESC_FIELD_ID = "ideSaveAsTemplateFormDescriptionField";
   
   public void checkSaveAsTemplateWindow()
   {
      assertTrue(selenium().isElementPresent(SAVE_AS_TEMPLATE_FORM_ID));
      assertTrue(selenium().isElementPresent(SAVE_AS_TEMPLATE_CANCEL_BUTTON_ID));
      assertTrue(selenium().isElementPresent(SAVE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(SAVE_AS_TEMPLATE_TYPE_FIELD_ID));
      assertTrue(selenium().isElementPresent(NAME_FIELD_ID));
      assertTrue(selenium().isElementPresent(SAVE_AS_TEMPLATE_DESC_FIELD_ID));
      assertTrue(selenium().isTextPresent("Type:"));
      assertTrue(selenium().isTextPresent("Name:"));
      assertTrue(selenium().isTextPresent("Description:"));
   }

}
