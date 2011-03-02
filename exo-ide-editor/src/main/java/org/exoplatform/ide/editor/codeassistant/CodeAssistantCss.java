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
package org.exoplatform.ide.editor.codeassistant;

import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Codeassistant Feb 22, 2011 5:17:02 PM evgen $
 *
 */
public interface CodeAssistantCss extends CssResource
{

   @ClassName("exo-autocomplete-panel")
   String panelStyle(); 
   
   @ClassName("exo-autocomplete-list-item")
   String item();
   
   @ClassName("exo-autocomplete-list-item-overed")
   String overedItem();
   
   @ClassName("exo-autocomplete-list-item-selected")
   String selectedItem();
   
   @ClassName("exo-autocomplete-description")
   String description();
   
   @ClassName("exo-autocomplete-list")
   String listStyle();
   
   @ClassName("exo-autocomplete-fqn")
   String fqnStyle();
   
   @ClassName("exo-autocomplete-keyword")
   String keywordStyle();
   
   @ClassName("exo-autocomplete-edit")
   String edit();

   @ClassName("exo-code-error-mark")
   String codeErrorMarkStyle();   
}
