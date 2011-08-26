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
package org.exoplatform.ide.client.framework.ui.impl;

/**
 * Types of view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 8, 2010 $
 *
 */
public interface ViewType
{

   /**
    * View type "navigation". All views of this type will be opened in the left part of IDE.
    */
   String NAVIGATION = "navigation";

   /**
    * View type "editor". All views of this type will be opened in the editor panel.
    */
   String EDITOR = "editor";

   /**
    * View type "information". All views of this type will be opened in the right part of IDE.
    */
   String INFORMATION = "information";

   /**
    * View type "operation". All views of this type will be opened in the bottom part of IDE.
    */
   String OPERATION = "operation";

   /**
    * View type "popup". All views of this type will be opened in window.
    */
   String POPUP = "popup";

   /**
    * View type "modal". All views of this type will be opened in modal window.
    */
   String MODAL = "modal";

}
