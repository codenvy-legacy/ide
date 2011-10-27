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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.CellTree;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CellTableResource.java Mar 11, 2011 11:33:27 AM vereshchaka $
 *
 */
public interface CellTreeResource extends CellTree.Resources
{
   
   @Source({CellTree.Style.DEFAULT_CSS, "CustomizedCellTree.css"})
   CellTreeStyle cellTreeStyle();
   
   /**
    * An image indicating an open branch.
    */
   @ImageOptions(flipRtl = true)
   @Source("close.png")
   ImageResource cellTreeOpenItem();
   
   /**
    * An image indicating a closed branch.
    */
   @ImageOptions(flipRtl = true)
   @Source("open.png")
   ImageResource cellTreeClosedItem();   

   interface CellTreeStyle extends CellTree.Style
   {
   }

}
