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
package org.exoplatform.ide.git.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 27, 2011 12:28:59 PM anya $
 *
 */
public class GwtMarshallerTestSuite extends TestCase
{
   public static Test suite()
   {
      GWTTestSuite suite = new GWTTestSuite("eXo IDE Git Marshaller GWT Tests");
      suite.addTestSuite(AddRequestMarshallerGwtTest.class);
      suite.addTestSuite(BranchesMarshallerGwtTest.class);
      suite.addTestSuite(CloneMarshallerGwtTest.class);
      suite.addTestSuite(CommitMarshallerGwtTest.class);
      suite.addTestSuite(FetchMarshallerGwtTest.class);
      suite.addTestSuite(InitMarshallerGwtTest.class);
      suite.addTestSuite(LogMarshallerGwtTest.class);
      suite.addTestSuite(PushMarshallerGwtTest.class);
      suite.addTestSuite(PullMarshallerGwtTest.class);
      suite.addTestSuite(RemotesMarshallerGwtTest.class);
      suite.addTestSuite(RemoveMarshallerGwtTest.class);
      suite.addTestSuite(ResetMarshallerGwtTest.class);
      suite.addTestSuite(StatusMarshallerGwtTest.class);
      return suite;
   }
}
