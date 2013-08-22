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

package org.exoplatform.gwtframework.commons.registry;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GwtTestRegistry extends GWTTestCase {

    private static String conf =
            "<?xml version=\"1.0\" ?><BackupManager jcr:primaryType=\"exo:registryEntry\">"
            + "<context jcr:primaryType=\"nt:unstructured\">portal/rest</context>"
            + "<repository jcr:primaryType=\"nt:unstructured\">repository</repository>"
            + "<workspace jcr:primaryType=\"nt:unstructured\">collaboration</workspace>" + "</BackupManager>";

    private final static String PROP_NAME = "repository";

    private final static String PROP_VALUE = "repository";

    @Override
    public String getModuleName() {
        return "org.exoplatform.gwt.commons.CommonsJUnit";
    }

    //  public void testGetProperties(){
    //    Document  doc = XMLParser.parse(conf);
    //    Registry registry = new Registry(doc);
    //    String value = registry.getProperties(PROP_NAME);
    //    assertEquals(PROP_VALUE, value);
    //  }

}
