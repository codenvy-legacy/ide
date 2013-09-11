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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ConfigurationOptionInfo {
    String getNamespace();

    void setNamespace(String namespace);

    String getName();

    void setName(String name);

    String getDefaultValue();

    void setDefaultValue(String defaultValue);

    ConfigurationOptionChangeSeverity getChangeSeverity();

    void setChangeSeverity(ConfigurationOptionChangeSeverity changeSeverity);

    boolean isUserDefined();

    void setUserDefined(boolean userDefined);

    ConfigurationOptionType getValueType();

    void setValueType(ConfigurationOptionType valueType);

    List<String> getValueOptions();

    void setValueOptions(List<String> valueOptions);

    Integer getMinValue();

    void setMinValue(Integer minValue);

    Integer getMaxValue();

    void setMaxValue(Integer maxValue);

    Integer getMaxLength();

    void setMaxLength(Integer maxLength);

    ConfigurationOptionRestriction getOptionRestriction();

    void setRegex(ConfigurationOptionRestriction optionRestriction);
}
