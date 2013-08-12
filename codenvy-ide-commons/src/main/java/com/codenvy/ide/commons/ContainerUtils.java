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
package com.codenvy.ide.commons;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.container.xml.ValuesParam;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ContainerUtils {
    /**
     * Get 'value-param' with <code>name</code> from InitParams instance. If <code>initParams == null</code> or does not
     * contains requested 'value-param' this method return <code>null</code>.
     *
     * @param initParams
     *         the InitParams
     * @param name
     *         the name of 'value-param'
     * @return the value of requested 'value-param' or <code>null</code>
     */
    public static String readValueParam(InitParams initParams, String name) {
        return readValueParam(initParams, name, null);
    }

    /**
     * Get 'value-param' with <code>name</code> from InitParams instance. If <code>initParams == null</code> or does
     * not contains requested 'value-param' this method returns <code>defaultValue</code>.
     *
     * @param initParams
     *         the InitParams
     * @param name
     *         name of 'value-param'
     * @return 'value-param' with specified name or <code>defaultValue</code>
     */
    public static String readValueParam(InitParams initParams, String name, String defaultValue) {
        if (initParams != null) {
            ValueParam vp = initParams.getValueParam(name);
            if (vp != null) {
                return vp.getValue();
            }
        }
        return defaultValue;
    }

    /**
     * Get 'values-param' with <code>name</code> from InitParams instance. If <code>initParams == null</code> or does
     * not contains requested 'values-param' this method returns empty List never <code>null</code>. The returned List
     * is
     * unmodifiable.
     * <p/>
     * If part of configuration looks like:
     * <p/>
     * <pre>
     * ...
     * &lt;init-params&gt;
     *    &lt;values-param&gt;
     *       &lt;name&gt;my-parameters&lt;/name&gt;
     *       &lt;value&gt;foo&lt;/value&gt;
     *       &lt;value&gt;bar&lt;/value&gt;
     *    &lt;/values-param&gt;
     * &lt;/init-params&gt;
     * ...
     * </pre>
     * <p/>
     * It becomes to List: <code>["foo", "bar"]</code>
     *
     * @param initParams
     *         the InitParams
     * @param name
     *         name of 'values-param'
     * @return unmodifiable List of requested 'values-param' or empty List if requested parameter not found
     */
    @SuppressWarnings("unchecked")
    public static List<String> readValuesParam(InitParams initParams, String name) {
        if (initParams != null) {
            ValuesParam vp = initParams.getValuesParam(name);
            if (vp != null) {
                return Collections.unmodifiableList(vp.getValues());
            }
        }
        return Collections.emptyList();
    }

    private ContainerUtils() {
    }
}