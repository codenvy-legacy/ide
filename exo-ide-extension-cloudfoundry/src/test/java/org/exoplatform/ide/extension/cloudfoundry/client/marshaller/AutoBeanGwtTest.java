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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.junit.Test;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class AutoBeanGwtTest extends GWTTestCase {

    interface MyFactory extends AutoBeanFactory {
        AutoBean<SystemInfo> systemInfo();

        AutoBean<SystemResources> systemResources();
    }

    @Override
    public String getModuleName() {
        return "org.exoplatform.ide.extension.cloudfoundry.CloudFoundryExtension";
    }

    @Test
    public void testName() throws Exception {
        String json =
                "{\"limits\":null,\"support\":\"http://support.cloudfoundry.com\",\"description\":\"VMware's Cloud Application " +
                "Platform\",\"name\":\"vcap\",\"usage\":null,\"user\":null,\"version\":\"0.999\"}";
        MyFactory myFactory = GWT.create(MyFactory.class);
        AutoBean<SystemInfo> bean = AutoBeanCodex.decode(myFactory, SystemInfo.class, json);
        SystemInfo info = bean.as();
        System.out.println(info.getClass().getName());
        assertEquals("http://support.cloudfoundry.com", info.getSupport());
    }

    public void testName2() throws Exception {
        MyFactory myFactory = GWT.create(MyFactory.class);
        SystemInfo info = myFactory.systemInfo().as();
//      ISystemInfo> bean = AutoBeanCodex.decode(myFactory, ISystemInfo.class, json);
//      ISystemInfo info = bean.as();
//      assertEquals("http://support.cloudfoundry.com", info.getSupport());
    }

}
