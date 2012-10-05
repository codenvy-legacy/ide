/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.part;

import com.google.gwt.resources.client.CssResource;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface PartStackCss extends CssResource
{

   @ClassName("ide-PartStack")
   String idePartStack();

   @ClassName("ide-PartStack-Tab")
   String idePartStackTab();

   @ClassName("ide-PartStack-Tabs")
   String idePartStackTabs();

   @ClassName("ide-PartStack-Tab-selected")
   String idePartStackTabSelected();

   @ClassName("ide-PartStack-Content")
   String idePartStackContent();

   @ClassName("ide-PartStack-focused")
   String idePartStackFocused();
}
