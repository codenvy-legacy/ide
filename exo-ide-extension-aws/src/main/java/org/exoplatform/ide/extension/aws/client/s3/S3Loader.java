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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.user.client.DOM;

import org.exoplatform.gwtframework.commons.loader.Loader;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3Loader.java Oct 4, 2012 vetal $
 *
 */
public class S3Loader extends Loader
{

   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader#show()
    */
   @Override
   public void show()
   {
      DOM.getElementById("gwt-debug-s3loader").setAttribute("style", "visibility: visible;");
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader#hide()
    */
   @Override
   public void hide()
   {
     DOM.getElementById("gwt-debug-s3loader").setAttribute("style", "visibility: hidden;");
   }

}
