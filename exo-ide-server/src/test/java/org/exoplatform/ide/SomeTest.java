/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
