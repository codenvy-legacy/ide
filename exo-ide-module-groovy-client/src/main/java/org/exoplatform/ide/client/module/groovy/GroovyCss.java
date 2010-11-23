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
package org.exoplatform.ide.client.module.groovy;

import com.google.gwt.resources.client.CssResource;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 23, 2010 3:46:05 PM evgen $
 *
 */
public interface GroovyCss extends CssResource
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
}
