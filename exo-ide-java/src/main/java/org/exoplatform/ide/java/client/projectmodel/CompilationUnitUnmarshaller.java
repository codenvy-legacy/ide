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
package org.exoplatform.ide.java.client.projectmodel;

import com.google.gwt.json.client.JSONParser;

import com.google.gwt.http.client.Response;

import org.exoplatform.ide.commons.exception.UnmarshallerException;
import org.exoplatform.ide.rest.Unmarshallable;

/**
 * Unmarshaller for {@link CompilationUnit}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CompilationUnitUnmarshaller implements Unmarshallable<CompilationUnit>
{

   private final CompilationUnit compilationUnit;

   /**
    * @param compilationUnit
    */
   public CompilationUnitUnmarshaller(CompilationUnit compilationUnit)
   {
      this.compilationUnit = compilationUnit;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         compilationUnit.init(JSONParser.parseLenient(response.getText()).isObject());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse compilation unit.", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompilationUnit getPayload()
   {
      return compilationUnit;
   }

}
