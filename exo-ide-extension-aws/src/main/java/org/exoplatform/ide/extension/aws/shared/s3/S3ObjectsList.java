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
package org.exoplatform.ide.extension.aws.shared.s3;

import java.util.List;

/**
 * Information about S3 objects which contains in specified S3 bucket
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface S3ObjectsList
{
   /**
    * Get list of S3 objects with their properties
    *
    * @return
    *    list containing S3 objects
    */
   List<S3Object> getObjects();

   /**
    * Set list of S3 objects with their properties
    *
    * @param objects
    *    list containing S3 objects
    */
   void setObjects(List<S3Object> objects);

   /**
    * Get S3 bucket name in which retrieve objects
    *
    * @return
    *    name of S3 bucket
    */
   String getS3Bucket();

   /**
    * Set S3 bucket name in which retrieve objects
    *
    * @param s3Bucket
    *    name of S3 bucket
    */
   void setS3Bucket(String s3Bucket);

   /**
    * Get prefix which restricting what keys will be listed
    *
    * @return
    *    name of prefix
    */
   String getPrefix();

   /**
    * Set name of prefix which restricting what keys will be listed
    *
    * @param prefix
    *    name of prefix
    */
   void setPrefix(String prefix);

   /**
    * Get key marker indicating where listing results should begin
    *
    * @return
    *    value of key marker which indicate from what position should begin listing
    */
   String getNextMarker();

   /**
    * Set key marker indicating where listing results should begin
    *
    * @param nextMarker
    *    value of key marker which indicate from what position should begin listing
    */
   void setNextMarker(String nextMarker);

   /**
    * Get the maximum number of results to return
    *
    * @return
    *    value of the maximum number results to return
    */
   int getMaxKeys();

   /**
    * Set the maximum number of the results to return
    *
    * @param maxKeys
    *    value of the maximum number results to return
    */
   void setMaxKeys(int maxKeys);
}
