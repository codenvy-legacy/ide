/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General  License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General  License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.aws.shared.beanstalk;

import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ConfigurationOptionInfo {
    /**
     * A unique namespace identifying the option's associated AWS resource.
     *
     * @return A unique namespace identifying the option's associated AWS resource.
     */
    String getNamespace();

    /**
     * The name of the configuration option.
     *
     * @return The name of the configuration option.
     */
    String getName();

    /**
     * The default value for this configuration option.
     *
     * @return The default value for this configuration option.
     */
    String getDefaultValue();

    /**
     * An indication of which action is required if the value for this
     * configuration option changes: <enumValues> <value
     * name="NoInterruption"> <p> NoInterruption - There is no interruption
     * to the environment or application availability. </value> <value
     * name="RestartEnvironment"> <p> RestartEnvironment - The environment is
     * restarted, all AWS resources are deleted and recreated, and the
     * environment is unavailable during the process. </value> <value
     * name="RestartApplicationServer"> <p> RestartApplicationServer - The
     * environment is available the entire time. However, a short application
     * outage occurs when the application servers on the running Amazon EC2
     * instances are restarted. </value> </enumValues> <ul> <li>
     * <code>NoInterruption</code> : There is no interruption to the
     * environment or application availability. </li> <li>
     * <code>RestartEnvironment</code> : The environment is entirely
     * restarted, all AWS resources are deleted and recreated, and the
     * environment is unavailable during the process. </li> <li>
     * <code>RestartApplicationServer</code> : The environment is available
     * the entire time. However, a short application outage occurs when the
     * application servers on the running Amazon EC2 instances are restarted.
     * </li> </ul>
     *
     * @return An indication of which action is required if the value for this
     *         configuration option changes: <enumValues> <value
     *         name="NoInterruption"> <p> NoInterruption - There is no interruption
     *         to the environment or application availability. </value> <value
     *         name="RestartEnvironment"> <p> RestartEnvironment - The environment is
     *         restarted, all AWS resources are deleted and recreated, and the
     *         environment is unavailable during the process. </value> <value
     *         name="RestartApplicationServer"> <p> RestartApplicationServer - The
     *         environment is available the entire time. However, a short application
     *         outage occurs when the application servers on the running Amazon EC2
     *         instances are restarted. </value> </enumValues> <ul> <li>
     *         <code>NoInterruption</code> : There is no interruption to the
     *         environment or application availability. </li> <li>
     *         <code>RestartEnvironment</code> : The environment is entirely
     *         restarted, all AWS resources are deleted and recreated, and the
     *         environment is unavailable during the process. </li> <li>
     *         <code>RestartApplicationServer</code> : The environment is available
     *         the entire time. However, a short application outage occurs when the
     *         application servers on the running Amazon EC2 instances are restarted.
     *         </li> </ul>
     */
    ConfigurationOptionChangeSeverity getChangeSeverity();

    /**
     * An indication of whether the user defined this configuration option:
     * <enumValues> <value name="true"> <p> <code>true</code> : This
     * configuration option was defined by the user. It is a valid choice for
     * specifying this as an Option to Remove when updating configuration
     * settings. </value> <value name="false"> <p> <code>false</code> : This
     * configuration was not defined by the user. </value> </enumValues> <ul>
     * <li> <p> <code>true</code> : This configuration option was defined by
     * the user. It is a valid choice for specifying if this as an
     * <code>Option to Remove</code> when updating configuration settings.
     * </li> <li> <code>false</code> : This configuration was not defined by
     * the user. </li> </ul> <p> Constraint: You can remove only
     * <code>UserDefined</code> options from a configuration. <p> Valid
     * Values: <code>true</code> | <code>false</code>
     *
     * @return An indication of whether the user defined this configuration option:
     *         <enumValues> <value name="true"> <p> <code>true</code> : This
     *         configuration option was defined by the user. It is a valid choice for
     *         specifying this as an Option to Remove when updating configuration
     *         settings. </value> <value name="false"> <p> <code>false</code> : This
     *         configuration was not defined by the user. </value> </enumValues> <ul>
     *         <li> <p> <code>true</code> : This configuration option was defined by
     *         the user. It is a valid choice for specifying if this as an
     *         <code>Option to Remove</code> when updating configuration settings.
     *         </li> <li> <code>false</code> : This configuration was not defined by
     *         the user. </li> </ul> <p> Constraint: You can remove only
     *         <code>UserDefined</code> options from a configuration. <p> Valid
     *         Values: <code>true</code> | <code>false</code>
     */
    boolean isUserDefined();

    /**
     * An indication of which type of values this option has and whether it
     * is allowable to select one or more than one of the possible values:
     * <enumValues> <value name="Scalar"> <p> <code>Scalar</code> : Values
     * for this option are a single selection from the possible values, or a
     * unformatted string or numeric value governed by the MIN/MAX/Regex
     * constraints: </value> <value name="List"> <p> <code>List</code> :
     * Values for this option are multiple selections of the possible values.
     * </value> <value name="Boolean"> <p> <code>Boolean</code> : Values for
     * this option are either <code>true</code> or <code>false</code> .
     * </value> </enumValues> <p> <ul> <li> <code>Scalar</code> : Values for
     * this option are a single selection from the possible values, or an
     * unformatted string, or numeric value governed by the
     * <code>MIN/MAX/Regex</code> constraints. </li> <li> <code>List</code> :
     * Values for this option are multiple selections from the possible
     * values. </li> <li> <code>Boolean</code> : Values for this option are
     * either <code>true</code> or <code>false</code> . </li> </ul>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>Scalar, List
     *
     * @return An indication of which type of values this option has and whether it
     *         is allowable to select one or more than one of the possible values:
     *         <enumValues> <value name="Scalar"> <p> <code>Scalar</code> : Values
     *         for this option are a single selection from the possible values, or a
     *         unformatted string or numeric value governed by the MIN/MAX/Regex
     *         constraints: </value> <value name="List"> <p> <code>List</code> :
     *         Values for this option are multiple selections of the possible values.
     *         </value> <value name="Boolean"> <p> <code>Boolean</code> : Values for
     *         this option are either <code>true</code> or <code>false</code> .
     *         </value> </enumValues> <p> <ul> <li> <code>Scalar</code> : Values for
     *         this option are a single selection from the possible values, or an
     *         unformatted string, or numeric value governed by the
     *         <code>MIN/MAX/Regex</code> constraints. </li> <li> <code>List</code> :
     *         Values for this option are multiple selections from the possible
     *         values. </li> <li> <code>Boolean</code> : Values for this option are
     *         either <code>true</code> or <code>false</code> . </li> </ul>
     *
     * @see ConfigurationOptionType
     */
    ConfigurationOptionType getValueType();

    /**
     * If specified, values for the configuration option are selected from
     * this list.
     *
     * @return If specified, values for the configuration option are selected from
     *         this list.
     */
    JsonArray<String> getValueOptions();

    /**
     * If specified, the configuration option must be a numeric value greater
     * than this value.
     *
     * @return If specified, the configuration option must be a numeric value greater
     *         than this value.
     */
    Integer getMinValue();

    /**
     * If specified, the configuration option must be a numeric value less
     * than this value.
     *
     * @return If specified, the configuration option must be a numeric value less
     *         than this value.
     */
    Integer getMaxValue();

    /**
     * If specified, the configuration option must be a string value no
     * longer than this value.
     *
     * @return If specified, the configuration option must be a string value no
     *         longer than this value.
     */
    Integer getMaxLength();

    /**
     * If specified, the configuration option must be a string value that
     * satisfies this regular expression.
     *
     * @return If specified, the configuration option must be a string value that
     *         satisfies this regular expression.
     */
    ConfigurationOptionRestriction getOptionRestriction();
}
