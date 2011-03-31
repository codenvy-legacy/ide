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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * Display interface, that templates view have to implement. 
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public interface CreateFromTemplateDisplay<T extends Template>
{
   /**
    * Get the template list grid for registration handlers.
    * @return
    */
   ListGridItem<T> getTemplateListGrid();

   /**
    * Get the value of name field.
    * @return
    */
   HasValue<String> getNameField();

   /**
    * Get cancel button for registration click handlers.
    * @return
    */
   HasClickHandlers getCancelButton();

   /**
    * Get create button for registration click handlers.
    * @return
    */
   HasClickHandlers getCreateButton();

   /**
    * Get delete button for registration click handlers.
    * @return
    */
   HasClickHandlers getDeleteButton();
   
   /**
    * Get the list of selected templates in list grid.
    * @return
    */
   List<T> getTemplatesSelected();

   /**
    * Close templates form.
    */
   void closeForm();

   /**
    * Make create button enabled.
    */
   void enableCreateButton();

   /**
    * Make crate button disabled.
    */
   void disableCreateButton();
   
   /**
    * Make delete button enabled.
    */
   void enableDeleteButton();

   /**
    * Make delete button disable.
    */
   void disableDeleteButton();
   
   /**
    * Enable the name field.
    */
   void enableNameField();

   /**
    * Disable the name field.
    */
   void disableNameField();

   /**
    * Select the last template in list grid.
    */
   void selectLastTemplate();

}
