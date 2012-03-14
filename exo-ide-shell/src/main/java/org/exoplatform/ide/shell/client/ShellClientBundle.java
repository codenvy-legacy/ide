/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.shell.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * Client bundle for Shell application.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 6, 2012 12:08:36 PM anya $
 * 
 */
public interface ShellClientBundle extends ClientBundle
{
   /**
    * Instance of {@link ShellClientBundle}.
    */
   ShellClientBundle INSTANCE = GWT.<ShellClientBundle> create(ShellClientBundle.class);

   /**
    * CSS resources for console.
    */
   @Source("org/exoplatform/ide/shell/client/Shell.css")
   Style css();

   /**
    * CSS styles.
    */
   public interface Style extends CssResource
   {
      String shellContainer();
      
      String content();
      
      String term();
      
      String serverResponseLabelError();

      String blink();

      String cursor();

      String crashAutocomplete();
   }
}
