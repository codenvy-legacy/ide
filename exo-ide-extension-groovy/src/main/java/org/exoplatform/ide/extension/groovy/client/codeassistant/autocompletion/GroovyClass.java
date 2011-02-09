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
package org.exoplatform.ide.extension.groovy.client.codeassistant.autocompletion;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 24, 2010 3:18:00 PM evgen $
 *
 */
public class GroovyClass
{
 
   private List<TokenExt> methods = new ArrayList<TokenExt>();
   
   private List<TokenExt> fields = new ArrayList<TokenExt>();
   
   private List<TokenExt> constructors = new ArrayList<TokenExt>();

   /**
    * @return the {@link List} of public methods
    */
   public List<TokenExt> getPublicMethods()
   {
      return methods;
   }

   /**
    * @return the {@link List} of public fields
    */
   public List<TokenExt> getPublicFields()
   {
      return fields;
   }

   /**
    * @return the {@link List} of public constructors
    */
   public List<TokenExt> getPublicConstructors()
   {
      return constructors;
   }
   
   
   
}
