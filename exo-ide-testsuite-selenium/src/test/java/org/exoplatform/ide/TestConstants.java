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
package org.exoplatform.ide;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 *
 */
public interface TestConstants
{
   
   public static final int IDE_INITIALIZATION_PERIOD = 3000;
   
   public static final int REDRAW_PERIOD = 500;

   public static final int TYPE_DELAY_PERIOD = 70;

   public static final int ANIMATION_PERIOD = 100;

   public static final int PAGE_LOAD_PERIOD = 2000;

   public static final int EDITOR_OPEN_PERIOD = 1500;

   public static final int IDE_LOAD_PERIOD = 20000;
   
   public static final int SLEEP = 3000;

   public static final int SLEEP_SHORT = 500;
   
   /**
    * Period to wait for element present.
    */
   public static final int WAIT_PERIOD = 60;
         
   /**
    *Realm for GateIn gatein-domain
    */
   public static final String REALM_GATEIN_DOMAIN = "exo-domain";
   
   /**
    * root
    */
   public static final String USER = "root";
   
   /**
    * gtn
    */
   public static final String PASSWD = "gtn";

   public interface NodeTypes
   {
      /**
       * exo:groovyResourceContainer
       */
      public static final String EXO_GROOVY_RESOURCE_CONTAINER = "exo:groovyResourceContainer";

      /**
       *nt:resource 
       */
      public static final String NT_RESOURCE = "nt:resource";
      
      /**
       *nt:file
       */
      public static final String NT_FILE = "nt:file";

      /**
       * exo:googleGadget
       */
      public static final String EXO_GOOGLE_GADGET = "exo:googleGadget";
   }
   
   /**
    * Users, allowed in IDE
    */
   public interface Users
   {
      /**
       * administrators and developers
       */
      public static final String ROOT = "root";
      
      /**
       * developers
       */
      public static final String JOHN = "john";
      
      /**
       * administrator
       */
      public static final String ADMIN = "admin";
   }
   
   String CODEMIRROR_EDITOR_LOCATOR = "//body[@class='editbox']";
   
   String CK_EDITOR_LOCATOR = "//body";
   
   public static final String UNTITLED_FILE_NAME = "Untitled file";
}
