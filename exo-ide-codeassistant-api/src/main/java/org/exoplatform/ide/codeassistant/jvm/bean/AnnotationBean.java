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
package org.exoplatform.ide.codeassistant.jvm.bean;

import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AnnotationBean implements Annotation
{

   private String typeName;

   private AnnotationParameter[] annotationParameters;

   /**
    * 
    */
   public AnnotationBean()
   {
   }

   /**
    * @param typeName
    * @param annotationParameters
    */
   public AnnotationBean(String typeName, AnnotationParameter[] annotationParameters)
   {
      super();
      this.typeName = typeName;
      this.annotationParameters = annotationParameters;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getTypeName()
    */
   @Override
   public String getTypeName()
   {
      return typeName;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getAnnotationParameters()
    */
   @Override
   public AnnotationParameter[] getAnnotationParameters()
   {
      return annotationParameters;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setTypeName()
    */
   @Override
   public void setTypeName(String name)
   {
      typeName = name;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setAnnotationParameters(org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter[])
    */
   @Override
   public void setAnnotationParameters(AnnotationParameter[] parameters)
   {
      annotationParameters = parameters;
   }

}
