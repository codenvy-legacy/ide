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
package org.exoplatform.ide.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/vetal/eXo/eXoRpojects/exo-int/web-tools/trunk/applications/gwt/devtool/src/main/resources/org/exoplatform/gadgets/devtool/client/BuildNumber.properties'.
 */
public interface BuildNumber extends com.google.gwt.i18n.client.Messages
{

   /**
    * Translated "4417".
    * 
    * @return translated "4417"
    */
   @DefaultMessage("")
   @Key("buildNumber")
   String buildNumber();

   /**
    * Translated "2009-12-21 15:05:49".
    * 
    * @return translated "2009-12-21 15:05:49"
    */
   @DefaultMessage("")
   @Key("buildTime")
   String buildTime();

   /**
    * Translated "1.0-SNAPSHOT".
    * 
    * @return translated "1.0-SNAPSHOT"
    */
   @DefaultMessage("1.0-SNAPSHOT")
   @Key("version")
   String version();

}
