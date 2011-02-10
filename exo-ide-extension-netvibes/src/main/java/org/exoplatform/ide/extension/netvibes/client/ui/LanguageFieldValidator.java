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
package org.exoplatform.ide.extension.netvibes.client.ui;

import org.exoplatform.ide.extension.netvibes.client.model.Languages;

import com.smartgwt.client.widgets.form.validator.CustomValidator;

/**
 * Validator for language field values.
 * Checks whether entered value is in the list of available languages.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 1, 2010 $
 *
 */
public class LanguageFieldValidator extends CustomValidator
{
   
   /**
    * @see com.smartgwt.client.widgets.form.validator.CustomValidator#condition(java.lang.Object)
    */
   @Override
   protected boolean condition(Object value)
   {
      return Languages.getLanguagesMap().containsKey(value);
   }

}
