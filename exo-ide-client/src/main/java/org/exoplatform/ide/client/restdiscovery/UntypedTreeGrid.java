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
package org.exoplatform.ide.client.restdiscovery;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;

import org.exoplatform.ide.client.framework.discovery.RestService;

import java.util.List;
import java.util.Set;

/**
 * A widget that implements this interface is untyped(or typed by {@link Object}) tree<br>
 * Used for REST Service Discovery<br>
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 23, 2010 3:48:02 PM evgen $
 * 
 */
public interface UntypedTreeGrid extends HasOpenHandlers<Object>, HasSelectionHandlers<Object>, HasClickHandlers
{
   /**
    * Set root resources
    * 
    * @param item root resource
    * @param restClassPath Paths of class mapping
    */
   void setRootValue(RestService item, Set<String> restClassPath);

   /**
    * Set paths mapped in root resource
    * 
    * @param service root resource
    * @param list of methods and resources mapped in root resource
    */
   void setPaths(RestService service, List<?> list);

}
