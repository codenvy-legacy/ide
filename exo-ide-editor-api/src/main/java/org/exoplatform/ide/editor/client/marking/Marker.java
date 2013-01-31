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

package org.exoplatform.ide.editor.client.marking;

/**
 * Created by The eXo Platform SAS .
 * 
 * Description of a Java problem, as detected by the compiler or some of the underlying
 * technology reusing the compiler.
 * A problem provides access to:
 * <ul>
 * <li> its location (originating source file name, source position, line number), </li>
 * <li> its message description and a predicate to check its severity (warning or error). </li>
 * <li> its ID : a number identifying the very nature of this problem. All possible IDs are listed
 * as constants on this interface. </li>
 * </ul>
 *
 * Note: the compiler produces Problems internally, which are turned into markers by the JavaBuilder
 * so as to persist problem descriptions. This explains why there is no API allowing to reach Problem detected
 * when compiling. However, the Java problem markers carry equivalent information to IProblem, in particular
 * their ID (attribute "id") is set to one of the IDs defined on this interface.
 *
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Marker
{
   
   public enum Type
   {
      
   }

   /**
    * Returns the problem id
    *
    * @return the problem id
    */
   int getID();

   /**
    * Answer a localized, human-readable message string which describes the problem.
    *
    * @return a localized, human-readable message string which describes the problem
    */
   String getMessage();

   /**
    * Answer the line number in source where the problem begins.
    *
    * @return the line number in source where the problem begins
    */
   int getLineNumber();

   /**
    * Answer the end position of the problem (inclusive), or -1 if unknown.
    *
    * @return the end position of the problem (inclusive), or -1 if unknown
    */
   int getEnd();

   /**
    * Answer the start position of the problem (inclusive), or -1 if unknown.
    *
    * @return the start position of the problem (inclusive), or -1 if unknown
    */
   int getStart();

   /**
    * Checks the severity to see if the Error bit is set.
    *
    * @return true if the Error bit is set for the severity, false otherwise
    */
   boolean isError();

   /**
    * Checks the severity to see if the Error bit is not set.
    *
    * @return true if the Error bit is not set for the severity, false otherwise
    */
   boolean isWarning();
   
   /**
    * Checks the severity to see if this is Breakpoint
    * @return true if this is breakpoint
    */
   boolean isBreakpoint();


   boolean isCurrentBreakPoint();

}
