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
package org.exoplatform.ide.client.framework.discovery;

/**
 * Interface describe REST-service.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IRestService.java Mar 26, 2012 10:55:52 AM azatsarynnyy $
 *
 */
public interface IRestService
{

   /**
    * Returns full qualified name of REST-service.
    * 
    * @return full qualified name
    */
   public String getFqn();

   /**
    * Set full qualified name of REST-service.
    * 
    * @param fqn full qualified name
    */
   public void setFqn(String fqn);

   /**
    * Returns REST-service path.
    * 
    * @return the REST-service path
    */
   public String getPath();

   /**
    * Set REST-service path.
    * 
    * @param path the REST-service path to set
    */
   public void setPath(String path);

   /**
    * Returns regex of REST-service.
    * 
    * @return the REST-service regex
    */
   public String getRegex();

   /**
    * Set regex of REST-service.
    * 
    * @param the REST-service regex
    */
   public void setRegex(String regex);

}