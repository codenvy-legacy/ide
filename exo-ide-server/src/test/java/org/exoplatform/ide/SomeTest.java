/*
 * Copyright (C) 2013 eXo Platform SAS.
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

import org.junit.Test;

import java.util.Arrays;

public class SomeTest {
//    
//    @Test
//    public void testname() throws Exception {
//        
//        String requestURI = "/w/dev-monit/some.js";
//        int i = requestURI.indexOf('/', 3);
//        String ws = requestURI.substring(3, i+1);
//        System.out.println("SomeTest.testname()" + ws);
//        String newuri = requestURI.replace(ws, "");
//        System.out.println("SomeTest.testname()" + newuri);
//    }
    
    @Test
    public void testname2() throws Exception {
        
        String requestURI = "/w/dev-monit";
        int i = requestURI.indexOf('/', 3);
        System.out.println("SomeTest.testname2()"+i);
        String ws;
        String newuri;
        if (i == -1){
            ws = requestURI.substring(3);
            newuri= requestURI.replace("w/" + ws, "ide/Application.html");
        }
        else {
            ws = requestURI.substring(3, i + 1);
            newuri= requestURI.replace("w/" + ws, "");
        }  
        System.out.println("SomeTest.testname()" + newuri);
        
        
    }

}
