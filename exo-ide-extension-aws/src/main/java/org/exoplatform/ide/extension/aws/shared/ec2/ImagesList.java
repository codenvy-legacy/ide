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
package org.exoplatform.ide.extension.aws.shared.ec2;

import java.util.List;

/**
 * Information about available AMIs
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ImagesList
{
   /**
    * Get list of AMIs.
    *
    * @return
    *    list of AMIs
    */
   List<ImageInfo> getImages();

   /**
    * Set list of AMIs.
    *
    * @param images
    *    list of AMIs
    */
   void setImages(List<ImageInfo> images);

   /**
    * Get count of described AMIs.
    *
    * @return
    *    total count of AMIs
    */
   int getTotal();

   /**
    * Set count of described AMIs.
    *
    * @param total
    *    total count of described AMIs
    */
   void setTotal(int total);

   /**
    * Get true if there are more images available to show in result set if max items less then all images count.
    *
    * @return
    *    true if there are more images available to show if max items less then all images count otherwise false
    */
   boolean isHasMore();

   /**
    * Set true if there are more images available to show in result set if max items less then all images count.
    *
    * @param hasMore
    *    true if there are more images available to show if max items less then all images count otherwise false
    */
   void setHasMore(boolean hasMore);

   /**
    * Get value from which should skipped images while viewing result set
    *
    * @return
    *    skip count
    */
   int getNextSkip();

   /**
    * Set value from which should skipped images while viewing result set
    *
    * @param skip
    *    skip count
    */
   void setNextSkip(int skip);

   /**
    * Get items count which will be included in result set.
    *
    * @return
    *    items count which will be included in result set
    */
   int getMaxItems();

   /**
    * Set items count which will be included in result set.
    *
    * @param maxItems
    *    items count which will be included in result set, if maxItems -1 then include all available images
    */
   void setMaxItems(int maxItems);
}
