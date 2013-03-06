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
package com.codenvy.ide.text;

/**
 * Definition of Java partitioning and its partitions.
 *
 */
//TODO move to java extensions
public interface IJavaPartitions {

   /**
    * The identifier of the Java partitioning.
    */
   String JAVA_PARTITIONING= "___java_partitioning";  //$NON-NLS-1$

   /**
    * The identifier of the single-line (JLS2: EndOfLineComment) end comment partition content type.
    */
   String JAVA_SINGLE_LINE_COMMENT= "__java_singleline_comment"; //$NON-NLS-1$

   /**
    * The identifier multi-line (JLS2: TraditionalComment) comment partition content type.
    */
   String JAVA_MULTI_LINE_COMMENT= "__java_multiline_comment"; //$NON-NLS-1$

   /**
    * The identifier of the Javadoc (JLS2: DocumentationComment) partition content type.
    */
   String JAVA_DOC= "__java_javadoc"; //$NON-NLS-1$

   /**
    * The identifier of the Java string partition content type.
    */
   String JAVA_STRING= "__java_string"; //$NON-NLS-1$

   /**
    * The identifier of the Java character partition content type.
    */
   String JAVA_CHARACTER= "__java_character";  //$NON-NLS-1$
}
