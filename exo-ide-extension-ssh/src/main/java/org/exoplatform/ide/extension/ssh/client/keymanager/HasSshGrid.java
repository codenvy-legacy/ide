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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;

/**
 * A widget that implements this interface provides registration for
 * {@link ClickHandler} instances, for two action column in grid component.
 * Need to add buttons click handlers in presenter.
 * <br />
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: HasSshKeyButtonsClickHandler May 18, 2011 3:57:06 PM evgen $
 *
 */
public interface HasSshGrid<T> extends ListGridItem<T>
{

   /**
    * Add handler to View public key button
    * @param handler
    * @return {@link HandlerRegistration}
    */
   HandlerRegistration addViewButtonSelectionHandler(SelectionHandler<T> handler);

   /**
    * Add handler to Delete Key button
    * @param handler
    * @return {@link HandlerRegistration}
    */
   HandlerRegistration addDeleteButtonSelectionHandler(SelectionHandler<T> handler);

}
