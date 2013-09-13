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
package org.exoplatform.ide.extension.heroku.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.heroku.shared.Credentials;
import org.exoplatform.ide.extension.heroku.shared.RakeCommandResult;
import org.exoplatform.ide.extension.heroku.shared.Stack;

/**
 * The interface for the {@link AutoBean} generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: HerokuAutoBeanFactory.java Mar 19, 2012 11:27:42 AM azatsarynnyy $
 */
public interface HerokuAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for a Heroku stack bean.
     *
     * @return an {@link AutoBean} of type {@link Stack}
     */
    AutoBean<Stack> stack();

    /**
     * A factory method for a credentials bean.
     *
     * @return an {@link AutoBean} of type {@link Credentials}
     */
    AutoBean<Credentials> credentials();

    /**
     * A factory method for a rake command result bean.
     *
     * @return an {@link AutoBean} of type {@link RakeCommandResult}
     */
    AutoBean<RakeCommandResult> rakeCommandResult();
}
