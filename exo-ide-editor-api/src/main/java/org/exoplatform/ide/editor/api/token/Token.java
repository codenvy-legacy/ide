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
package org.exoplatform.ide.editor.api.token;

import java.util.Collection;
import java.util.Set;

/**
 * Token interface represent abstract token. 
 *   
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface Token
{

   /**
    * Get name of token
    * @return name
    */
   String getName();

   /**
    * Get token type
    * @return type
    */
   TokenType getType();

   /**
    * Get names of all token properties 
    * @return {@link Set} of {@link String} properties names
    */
   Set<String> getPropertiesNames();

   /**
    * Get all token properties
    * @return {@link Collection} of {@link TokenProperty} 
    */
   Collection<TokenProperty> getProperties();

   /**
    * Get specific property
    * @param name of property
    * @return {@link TokenProperty} property 
    */
   TokenProperty getProperty(String name);

   /**
    * Set property
    * @param name of property
    * @param property value
    */
   void setProperty(String name, TokenProperty property);
   
   /**
    * Check is token has property
    * @param name of property
    * @return <code>true</code> if token has property, otherwise <code>false</code> 
    */
   boolean hasProperty(String name);

}
