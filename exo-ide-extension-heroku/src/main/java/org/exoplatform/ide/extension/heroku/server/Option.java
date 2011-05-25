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
package org.exoplatform.ide.extension.heroku.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation of heroku command option. Fields of instance {@link HerokuCommand} initialized by values supplied in Map
 * passed to {@link Heroku#execute(String, java.util.Map, java.util.List, java.io.File)}. Value from {@link #name()}
 * used to obtain correct value from Map. If option is required but not provided then IllegalArgumentException thrown by
 * method {@link Heroku#execute(String, java.util.Map, java.util.List, java.io.File)}.
 * <p>
 * Example:
 * 
 * <pre>
 * public class MyCommand extends HerokuCommand
 * {
 *    &#064;Option(name = "option0", required = true)
 *    private String option0;
 * 
 *    ...
 * }
 * </pre>
 * 
 * </p>
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
   /**
    * Option name
    * 
    * @return option name
    */
   String name();

   /**
    * @return <code>true</code> if option required and <code>false</code> otherwise
    */
   boolean required() default false;
}
