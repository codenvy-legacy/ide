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
package com.codenvy.ide.java.client.env;

import com.codenvy.ide.java.client.internal.compiler.env.IBinaryElementValuePair;

import com.google.gwt.json.client.JSONObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class BinaryElementValuePairImpl implements IBinaryElementValuePair
{

   private final JSONObject val;

   /**
    * @param val
    */
   public BinaryElementValuePairImpl(JSONObject val)
   {
      this.val = val;
   }

   /**
    * @see com.codenvy.ide.java.client.internal.compiler.env.IBinaryElementValuePair#getName()
    */
   @Override
   public char[] getName()
   {
      return val.get("name").isString().stringValue().toCharArray();
   }

   /**
    * @see com.codenvy.ide.java.client.internal.compiler.env.IBinaryElementValuePair#getValue()
    */
   @Override
   public Object getValue()
   {
      return AnnotationParseUtil.getValue(val.get("value").isObject());
   }

}
