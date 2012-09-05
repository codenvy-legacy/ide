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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AsmAnnotation implements Annotation
{

   private AnnotationNode annotationNode;

   /**
    * @param annotationNode
    */
   public AsmAnnotation(AnnotationNode annotationNode)
   {
      this.annotationNode = annotationNode;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getTypeName()
    */
   @Override
   public String getTypeName()
   {
      return annotationNode.desc;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getAnnotationParameters()
    */
   @Override
   public AnnotationParameter[] getAnnotationParameters()
   {
      if(annotationNode.values == null)
         return new AnnotationParameter[0];
      
      AnnotationParameter[] param = new AnnotationParameter[annotationNode.values.size() / 2];
      int i = 0;
      for (Iterator iterator = annotationNode.values.iterator(); iterator.hasNext();)
      {
         Object type =  iterator.next();
         param[i] = new AsmAnnotationParameter(type.toString(), iterator.next());
         i++;
      }
      return param;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setTypeName()
    */
   @Override
   public void setTypeName(String name)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setAnnotationParameters(org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter[])
    */
   @Override
   public void setAnnotationParameters(AnnotationParameter[] parameters)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

}
