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
package org.exoplatform.ide.editor.codeassistant;


import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GwtTestSuite Feb 28, 2011 2:21:56 PM evgen $
 *
 */
public class GwtCodeAssistantTestSuite extends TestCase
{
   public static Test suite()
   {
       GWTTestSuite suite = new GWTTestSuite( "eXo IDE CodeAssistant GWT Tests" );
       suite.addTestSuite(CssGwtTestCodeAssistant.class );
       suite.addTestSuite(HtmlGwtTestCodeAssistant.class );
       suite.addTestSuite(JavaScriptGwtTestCodeAssistant.class );
       suite.addTestSuite(XmlGwtTestCodeAssistant.class);
       suite.addTestSuite(NetvibesGwtTestCodeAssistant.class);
       suite.addTestSuite(JavaGwtTestCodeAssistant.class);
       return suite;
   }
   
}
