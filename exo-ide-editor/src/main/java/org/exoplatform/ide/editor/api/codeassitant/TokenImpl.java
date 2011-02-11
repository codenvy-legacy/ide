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
package org.exoplatform.ide.editor.api.codeassitant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Basic implementation of {@link Token} interface
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:22:15 PM evgen $
 *
 */
public class TokenImpl implements Token
{
   private Map<String, TokenProperty> properties = new LinkedHashMap<String, TokenProperty>();

   private String name;

   private TokenType type;

   /**
    * @param name
    * @param type
    */
   public TokenImpl(String name, TokenType type)
   {
      this.name = name;
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#getType()
    */
   @Override
   public TokenType getType()
   {
      return type;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#getPropertiesKeys()
    */
   @Override
   public Set<String> getPropertiesNames()
   {
      return properties.keySet();
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#getProperties()
    */
   @Override
   public Collection<TokenProperty> getProperties()
   {
      return properties.values();
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#getProperty(java.lang.String)
    */
   @Override
   public TokenProperty getProperty(String key)
   {
      return properties.get(key);
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#setProperty(java.lang.String, org.exoplatform.ide.editor.api.codeassitant.TokenProperty)
    */
   @Override
   public void setProperty(String key, TokenProperty property)
   {
      properties.put(key, property);
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.Token#hasProperty(java.lang.String)
    */
   @Override
   public boolean hasProperty(String name)
   {
      return properties.containsKey(name);
   }

}
