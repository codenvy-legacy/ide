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


// GENERATED SOURCE. DO NOT EDIT.
package com.codenvy.ide.ext.aws.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "9871263944342f769ff31c5f6f4b4a6dfcf6941f";


  public static class CredentialsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.Credentials {
    protected CredentialsImpl() {}

    @Override
    public final native java.lang.String getAccess_key() /*-{
      return this["access_key"];
    }-*/;

    public final native CredentialsImpl setAccess_key(java.lang.String access_key) /*-{
      this["access_key"] = access_key;
      return this;
    }-*/;

    public final native boolean hasAccess_key() /*-{
      return this.hasOwnProperty("access_key");
    }-*/;

    @Override
    public final native java.lang.String getSecret_key() /*-{
      return this["secret_key"];
    }-*/;

    public final native CredentialsImpl setSecret_key(java.lang.String secret_key) /*-{
      this["secret_key"] = secret_key;
      return this;
    }-*/;

    public final native boolean hasSecret_key() /*-{
      return this.hasOwnProperty("secret_key");
    }-*/;

    public static native CredentialsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ApplicationInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo {
    protected ApplicationInfoImpl() {}

    @Override
    public final native double getCreated() /*-{
      return this["created"];
    }-*/;

    public final native ApplicationInfoImpl setCreated(double created) /*-{
      this["created"] = created;
      return this;
    }-*/;

    public final native boolean hasCreated() /*-{
      return this.hasOwnProperty("created");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getVersions() /*-{
      return this["versions"];
    }-*/;

    public final native ApplicationInfoImpl setVersions(com.codenvy.ide.json.JsonArray<java.lang.String> versions) /*-{
      this["versions"] = versions;
      return this;
    }-*/;

    public final native boolean hasVersions() /*-{
      return this.hasOwnProperty("versions");
    }-*/;

    @Override
    public final native double getUpdated() /*-{
      return this["updated"];
    }-*/;

    public final native ApplicationInfoImpl setUpdated(double updated) /*-{
      this["updated"] = updated;
      return this;
    }-*/;

    public final native boolean hasUpdated() /*-{
      return this.hasOwnProperty("updated");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getConfigurationTemplates() /*-{
      return this["configurationTemplates"];
    }-*/;

    public final native ApplicationInfoImpl setConfigurationTemplates(com.codenvy.ide.json.JsonArray<java.lang.String> configurationTemplates) /*-{
      this["configurationTemplates"] = configurationTemplates;
      return this;
    }-*/;

    public final native boolean hasConfigurationTemplates() /*-{
      return this.hasOwnProperty("configurationTemplates");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ApplicationInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native ApplicationInfoImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native ApplicationInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ApplicationVersionInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo {
    protected ApplicationVersionInfoImpl() {}

    @Override
    public final native double getCreated() /*-{
      return this["created"];
    }-*/;

    public final native ApplicationVersionInfoImpl setCreated(double created) /*-{
      this["created"] = created;
      return this;
    }-*/;

    public final native boolean hasCreated() /*-{
      return this.hasOwnProperty("created");
    }-*/;

    @Override
    public final native double getUpdated() /*-{
      return this["updated"];
    }-*/;

    public final native ApplicationVersionInfoImpl setUpdated(double updated) /*-{
      this["updated"] = updated;
      return this;
    }-*/;

    public final native boolean hasUpdated() /*-{
      return this.hasOwnProperty("updated");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.S3Item getS3Location() /*-{
      return this["s3Location"];
    }-*/;

    public final native ApplicationVersionInfoImpl setS3Location(com.codenvy.ide.ext.aws.shared.beanstalk.S3Item s3Location) /*-{
      this["s3Location"] = s3Location;
      return this;
    }-*/;

    public final native boolean hasS3Location() /*-{
      return this.hasOwnProperty("s3Location");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native ApplicationVersionInfoImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native ApplicationVersionInfoImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native ApplicationVersionInfoImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native ApplicationVersionInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ConfigurationImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.Configuration {
    protected ConfigurationImpl() {}

    @Override
    public final native java.lang.String getSolutionStackName() /*-{
      return this["solutionStackName"];
    }-*/;

    public final native ConfigurationImpl setSolutionStackName(java.lang.String solutionStackName) /*-{
      this["solutionStackName"] = solutionStackName;
      return this;
    }-*/;

    public final native boolean hasSolutionStackName() /*-{
      return this.hasOwnProperty("solutionStackName");
    }-*/;

    @Override
    public final native java.lang.Long getCreated() /*-{
      return this["created"];
    }-*/;

    public final native ConfigurationImpl setCreated(java.lang.Long created) /*-{
      this["created"] = created;
      return this;
    }-*/;

    public final native boolean hasCreated() /*-{
      return this.hasOwnProperty("created");
    }-*/;

    @Override
    public final native java.lang.Long getUpdated() /*-{
      return this["updated"];
    }-*/;

    public final native ConfigurationImpl setUpdated(java.lang.Long updated) /*-{
      this["updated"] = updated;
      return this;
    }-*/;

    public final native boolean hasUpdated() /*-{
      return this.hasOwnProperty("updated");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native ConfigurationImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native ConfigurationImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getEnvironmentName() /*-{
      return this["environmentName"];
    }-*/;

    public final native ConfigurationImpl setEnvironmentName(java.lang.String environmentName) /*-{
      this["environmentName"] = environmentName;
      return this;
    }-*/;

    public final native boolean hasEnvironmentName() /*-{
      return this.hasOwnProperty("environmentName");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationTemplateDeploymentStatus getDeploymentStatus() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationTemplateDeploymentStatus::valueOf(Ljava/lang/String;)(this["deploymentStatus"]);
    }-*/;

    public final native ConfigurationImpl setDeploymentStatus(com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationTemplateDeploymentStatus deploymentStatus) /*-{
      deploymentStatus = deploymentStatus.@com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationTemplateDeploymentStatus::toString()();
      this["deploymentStatus"] = deploymentStatus;
      return this;
    }-*/;

    public final native boolean hasDeploymentStatus() /*-{
      return this.hasOwnProperty("deploymentStatus");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native ConfigurationImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> getOptions() /*-{
      return this["options"];
    }-*/;

    public final native ConfigurationImpl setOptions(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> options) /*-{
      this["options"] = options;
      return this;
    }-*/;

    public final native boolean hasOptions() /*-{
      return this.hasOwnProperty("options");
    }-*/;

    public static native ConfigurationImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ConfigurationOptionImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption {
    protected ConfigurationOptionImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ConfigurationOptionImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getValue() /*-{
      return this["value"];
    }-*/;

    public final native ConfigurationOptionImpl setValue(java.lang.String value) /*-{
      this["value"] = value;
      return this;
    }-*/;

    public final native boolean hasValue() /*-{
      return this.hasOwnProperty("value");
    }-*/;

    @Override
    public final native java.lang.String getNamespace() /*-{
      return this["namespace"];
    }-*/;

    public final native ConfigurationOptionImpl setNamespace(java.lang.String namespace) /*-{
      this["namespace"] = namespace;
      return this;
    }-*/;

    public final native boolean hasNamespace() /*-{
      return this.hasOwnProperty("namespace");
    }-*/;

    public static native ConfigurationOptionImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ConfigurationOptionInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionInfo {
    protected ConfigurationOptionInfoImpl() {}

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionChangeSeverity getChangeSeverity() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionChangeSeverity::valueOf(Ljava/lang/String;)(this["changeSeverity"]);
    }-*/;

    public final native ConfigurationOptionInfoImpl setChangeSeverity(com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionChangeSeverity changeSeverity) /*-{
      changeSeverity = changeSeverity.@com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionChangeSeverity::toString()();
      this["changeSeverity"] = changeSeverity;
      return this;
    }-*/;

    public final native boolean hasChangeSeverity() /*-{
      return this.hasOwnProperty("changeSeverity");
    }-*/;

    @Override
    public final native boolean isUserDefined() /*-{
      return this["isUserDefined"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setIsUserDefined(boolean isUserDefined) /*-{
      this["isUserDefined"] = isUserDefined;
      return this;
    }-*/;

    public final native boolean hasIsUserDefined() /*-{
      return this.hasOwnProperty("isUserDefined");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getValueOptions() /*-{
      return this["valueOptions"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setValueOptions(com.codenvy.ide.json.JsonArray<java.lang.String> valueOptions) /*-{
      this["valueOptions"] = valueOptions;
      return this;
    }-*/;

    public final native boolean hasValueOptions() /*-{
      return this.hasOwnProperty("valueOptions");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionRestriction getOptionRestriction() /*-{
      return this["optionRestriction"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setOptionRestriction(com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionRestriction optionRestriction) /*-{
      this["optionRestriction"] = optionRestriction;
      return this;
    }-*/;

    public final native boolean hasOptionRestriction() /*-{
      return this.hasOwnProperty("optionRestriction");
    }-*/;

    @Override
    public final native java.lang.Integer getMaxValue() /*-{
      return this["maxValue"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setMaxValue(java.lang.Integer maxValue) /*-{
      this["maxValue"] = maxValue;
      return this;
    }-*/;

    public final native boolean hasMaxValue() /*-{
      return this.hasOwnProperty("maxValue");
    }-*/;

    @Override
    public final native java.lang.Integer getMaxLength() /*-{
      return this["maxLength"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setMaxLength(java.lang.Integer maxLength) /*-{
      this["maxLength"] = maxLength;
      return this;
    }-*/;

    public final native boolean hasMaxLength() /*-{
      return this.hasOwnProperty("maxLength");
    }-*/;

    @Override
    public final native java.lang.Integer getMinValue() /*-{
      return this["minValue"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setMinValue(java.lang.Integer minValue) /*-{
      this["minValue"] = minValue;
      return this;
    }-*/;

    public final native boolean hasMinValue() /*-{
      return this.hasOwnProperty("minValue");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionType getValueType() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionType::valueOf(Ljava/lang/String;)(this["valueType"]);
    }-*/;

    public final native ConfigurationOptionInfoImpl setValueType(com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionType valueType) /*-{
      valueType = valueType.@com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionType::toString()();
      this["valueType"] = valueType;
      return this;
    }-*/;

    public final native boolean hasValueType() /*-{
      return this.hasOwnProperty("valueType");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getDefaultValue() /*-{
      return this["defaultValue"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setDefaultValue(java.lang.String defaultValue) /*-{
      this["defaultValue"] = defaultValue;
      return this;
    }-*/;

    public final native boolean hasDefaultValue() /*-{
      return this.hasOwnProperty("defaultValue");
    }-*/;

    @Override
    public final native java.lang.String getNamespace() /*-{
      return this["namespace"];
    }-*/;

    public final native ConfigurationOptionInfoImpl setNamespace(java.lang.String namespace) /*-{
      this["namespace"] = namespace;
      return this;
    }-*/;

    public final native boolean hasNamespace() /*-{
      return this.hasOwnProperty("namespace");
    }-*/;

    public static native ConfigurationOptionInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ConfigurationOptionRestrictionImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionRestriction {
    protected ConfigurationOptionRestrictionImpl() {}

    @Override
    public final native java.lang.String getPattern() /*-{
      return this["pattern"];
    }-*/;

    public final native ConfigurationOptionRestrictionImpl setPattern(java.lang.String pattern) /*-{
      this["pattern"] = pattern;
      return this;
    }-*/;

    public final native boolean hasPattern() /*-{
      return this.hasOwnProperty("pattern");
    }-*/;

    @Override
    public final native java.lang.String getLabel() /*-{
      return this["label"];
    }-*/;

    public final native ConfigurationOptionRestrictionImpl setLabel(java.lang.String label) /*-{
      this["label"] = label;
      return this;
    }-*/;

    public final native boolean hasLabel() /*-{
      return this.hasOwnProperty("label");
    }-*/;

    public static native ConfigurationOptionRestrictionImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ConfigurationRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationRequest {
    protected ConfigurationRequestImpl() {}

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native ConfigurationRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native ConfigurationRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getEnvironmentName() /*-{
      return this["environmentName"];
    }-*/;

    public final native ConfigurationRequestImpl setEnvironmentName(java.lang.String environmentName) /*-{
      this["environmentName"] = environmentName;
      return this;
    }-*/;

    public final native boolean hasEnvironmentName() /*-{
      return this.hasOwnProperty("environmentName");
    }-*/;

    public static native ConfigurationRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CreateApplicationRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.CreateApplicationRequest {
    protected CreateApplicationRequestImpl() {}

    @Override
    public final native java.lang.String getWar() /*-{
      return this["war"];
    }-*/;

    public final native CreateApplicationRequestImpl setWar(java.lang.String war) /*-{
      this["war"] = war;
      return this;
    }-*/;

    public final native boolean hasWar() /*-{
      return this.hasOwnProperty("war");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native CreateApplicationRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native CreateApplicationRequestImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native CreateApplicationRequestImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native CreateApplicationRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native CreateApplicationRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CreateApplicationVersionRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.CreateApplicationVersionRequest {
    protected CreateApplicationVersionRequestImpl() {}

    @Override
    public final native java.lang.String getWar() /*-{
      return this["war"];
    }-*/;

    public final native CreateApplicationVersionRequestImpl setWar(java.lang.String war) /*-{
      this["war"] = war;
      return this;
    }-*/;

    public final native boolean hasWar() /*-{
      return this.hasOwnProperty("war");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native CreateApplicationVersionRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native CreateApplicationVersionRequestImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native CreateApplicationVersionRequestImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native CreateApplicationVersionRequestImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native CreateApplicationVersionRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native CreateApplicationVersionRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CreateConfigurationTemplateRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.CreateConfigurationTemplateRequest {
    protected CreateConfigurationTemplateRequestImpl() {}

    @Override
    public final native java.lang.String getSolutionStackName() /*-{
      return this["solutionStackName"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setSolutionStackName(java.lang.String solutionStackName) /*-{
      this["solutionStackName"] = solutionStackName;
      return this;
    }-*/;

    public final native boolean hasSolutionStackName() /*-{
      return this.hasOwnProperty("solutionStackName");
    }-*/;

    @Override
    public final native java.lang.String getSourceApplicationName() /*-{
      return this["sourceApplicationName"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setSourceApplicationName(java.lang.String sourceApplicationName) /*-{
      this["sourceApplicationName"] = sourceApplicationName;
      return this;
    }-*/;

    public final native boolean hasSourceApplicationName() /*-{
      return this.hasOwnProperty("sourceApplicationName");
    }-*/;

    @Override
    public final native java.lang.String getSourceTemplateName() /*-{
      return this["sourceTemplateName"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setSourceTemplateName(java.lang.String sourceTemplateName) /*-{
      this["sourceTemplateName"] = sourceTemplateName;
      return this;
    }-*/;

    public final native boolean hasSourceTemplateName() /*-{
      return this.hasOwnProperty("sourceTemplateName");
    }-*/;

    @Override
    public final native java.lang.String getEnvironmentId() /*-{
      return this["environmentId"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setEnvironmentId(java.lang.String environmentId) /*-{
      this["environmentId"] = environmentId;
      return this;
    }-*/;

    public final native boolean hasEnvironmentId() /*-{
      return this.hasOwnProperty("environmentId");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> getOptions() /*-{
      return this["options"];
    }-*/;

    public final native CreateConfigurationTemplateRequestImpl setOptions(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> options) /*-{
      this["options"] = options;
      return this;
    }-*/;

    public final native boolean hasOptions() /*-{
      return this.hasOwnProperty("options");
    }-*/;

    public static native CreateConfigurationTemplateRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CreateEnvironmentRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.CreateEnvironmentRequest {
    protected CreateEnvironmentRequestImpl() {}

    @Override
    public final native java.lang.String getSolutionStackName() /*-{
      return this["solutionStackName"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setSolutionStackName(java.lang.String solutionStackName) /*-{
      this["solutionStackName"] = solutionStackName;
      return this;
    }-*/;

    public final native boolean hasSolutionStackName() /*-{
      return this.hasOwnProperty("solutionStackName");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getEnvironmentName() /*-{
      return this["environmentName"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setEnvironmentName(java.lang.String environmentName) /*-{
      this["environmentName"] = environmentName;
      return this;
    }-*/;

    public final native boolean hasEnvironmentName() /*-{
      return this.hasOwnProperty("environmentName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> getOptions() /*-{
      return this["options"];
    }-*/;

    public final native CreateEnvironmentRequestImpl setOptions(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> options) /*-{
      this["options"] = options;
      return this;
    }-*/;

    public final native boolean hasOptions() /*-{
      return this.hasOwnProperty("options");
    }-*/;

    public static native CreateEnvironmentRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class DeleteApplicationVersionRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.DeleteApplicationVersionRequest {
    protected DeleteApplicationVersionRequestImpl() {}

    @Override
    public final native boolean isDeleteS3Bundle() /*-{
      return this["isDeleteS3Bundle"];
    }-*/;

    public final native DeleteApplicationVersionRequestImpl setIsDeleteS3Bundle(boolean isDeleteS3Bundle) /*-{
      this["isDeleteS3Bundle"] = isDeleteS3Bundle;
      return this;
    }-*/;

    public final native boolean hasIsDeleteS3Bundle() /*-{
      return this.hasOwnProperty("isDeleteS3Bundle");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native DeleteApplicationVersionRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native DeleteApplicationVersionRequestImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    public static native DeleteApplicationVersionRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class DeleteConfigurationTemplateRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.DeleteConfigurationTemplateRequest {
    protected DeleteConfigurationTemplateRequestImpl() {}

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native DeleteConfigurationTemplateRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native DeleteConfigurationTemplateRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    public static native DeleteConfigurationTemplateRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class EnvironmentInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo {
    protected EnvironmentInfoImpl() {}

    @Override
    public final native java.lang.String getSolutionStackName() /*-{
      return this["solutionStackName"];
    }-*/;

    public final native EnvironmentInfoImpl setSolutionStackName(java.lang.String solutionStackName) /*-{
      this["solutionStackName"] = solutionStackName;
      return this;
    }-*/;

    public final native boolean hasSolutionStackName() /*-{
      return this.hasOwnProperty("solutionStackName");
    }-*/;

    @Override
    public final native double getCreated() /*-{
      return this["created"];
    }-*/;

    public final native EnvironmentInfoImpl setCreated(double created) /*-{
      this["created"] = created;
      return this;
    }-*/;

    public final native boolean hasCreated() /*-{
      return this.hasOwnProperty("created");
    }-*/;

    @Override
    public final native java.lang.String getEndpointUrl() /*-{
      return this["endpointUrl"];
    }-*/;

    public final native EnvironmentInfoImpl setEndpointUrl(java.lang.String endpointUrl) /*-{
      this["endpointUrl"] = endpointUrl;
      return this;
    }-*/;

    public final native boolean hasEndpointUrl() /*-{
      return this.hasOwnProperty("endpointUrl");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentHealth getHealth() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentHealth::valueOf(Ljava/lang/String;)(this["health"]);
    }-*/;

    public final native EnvironmentInfoImpl setHealth(com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentHealth health) /*-{
      health = health.@com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentHealth::toString()();
      this["health"] = health;
      return this;
    }-*/;

    public final native boolean hasHealth() /*-{
      return this.hasOwnProperty("health");
    }-*/;

    @Override
    public final native java.lang.String getCname() /*-{
      return this["cname"];
    }-*/;

    public final native EnvironmentInfoImpl setCname(java.lang.String cname) /*-{
      this["cname"] = cname;
      return this;
    }-*/;

    public final native boolean hasCname() /*-{
      return this.hasOwnProperty("cname");
    }-*/;

    @Override
    public final native double getUpdated() /*-{
      return this["updated"];
    }-*/;

    public final native EnvironmentInfoImpl setUpdated(double updated) /*-{
      this["updated"] = updated;
      return this;
    }-*/;

    public final native boolean hasUpdated() /*-{
      return this.hasOwnProperty("updated");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native EnvironmentInfoImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native EnvironmentInfoImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native EnvironmentInfoImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native EnvironmentInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native EnvironmentInfoImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus getStatus() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus::valueOf(Ljava/lang/String;)(this["status"]);
    }-*/;

    public final native EnvironmentInfoImpl setStatus(com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus status) /*-{
      status = status.@com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus::toString()();
      this["status"] = status;
      return this;
    }-*/;

    public final native boolean hasStatus() /*-{
      return this.hasOwnProperty("status");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native EnvironmentInfoImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native EnvironmentInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class EventImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.Event {
    protected EventImpl() {}

    @Override
    public final native double getEventDate() /*-{
      return this["eventDate"];
    }-*/;

    public final native EventImpl setEventDate(double eventDate) /*-{
      this["eventDate"] = eventDate;
      return this;
    }-*/;

    public final native boolean hasEventDate() /*-{
      return this.hasOwnProperty("eventDate");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity getSeverity() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity::valueOf(Ljava/lang/String;)(this["severity"]);
    }-*/;

    public final native EventImpl setSeverity(com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity severity) /*-{
      severity = severity.@com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity::toString()();
      this["severity"] = severity;
      return this;
    }-*/;

    public final native boolean hasSeverity() /*-{
      return this.hasOwnProperty("severity");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native EventImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native EventImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getEnvironmentName() /*-{
      return this["environmentName"];
    }-*/;

    public final native EventImpl setEnvironmentName(java.lang.String environmentName) /*-{
      this["environmentName"] = environmentName;
      return this;
    }-*/;

    public final native boolean hasEnvironmentName() /*-{
      return this.hasOwnProperty("environmentName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native EventImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getMessage() /*-{
      return this["message"];
    }-*/;

    public final native EventImpl setMessage(java.lang.String message) /*-{
      this["message"] = message;
      return this;
    }-*/;

    public final native boolean hasMessage() /*-{
      return this.hasOwnProperty("message");
    }-*/;

    public static native EventImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class EventsListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.EventsList {
    protected EventsListImpl() {}

    @Override
    public final native java.lang.String getNextToken() /*-{
      return this["nextToken"];
    }-*/;

    public final native EventsListImpl setNextToken(java.lang.String nextToken) /*-{
      this["nextToken"] = nextToken;
      return this;
    }-*/;

    public final native boolean hasNextToken() /*-{
      return this.hasOwnProperty("nextToken");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.Event> getEvents() /*-{
      return this["events"];
    }-*/;

    public final native EventsListImpl setEvents(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.Event> events) /*-{
      this["events"] = events;
      return this;
    }-*/;

    public final native boolean hasEvents() /*-{
      return this.hasOwnProperty("events");
    }-*/;

    public static native EventsListImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class InstanceLogImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.InstanceLog {
    protected InstanceLogImpl() {}

    @Override
    public final native java.lang.String getInstanceId() /*-{
      return this["instanceId"];
    }-*/;

    public final native InstanceLogImpl setInstanceId(java.lang.String instanceId) /*-{
      this["instanceId"] = instanceId;
      return this;
    }-*/;

    public final native boolean hasInstanceId() /*-{
      return this.hasOwnProperty("instanceId");
    }-*/;

    @Override
    public final native java.lang.String getLogUrl() /*-{
      return this["logUrl"];
    }-*/;

    public final native InstanceLogImpl setLogUrl(java.lang.String logUrl) /*-{
      this["logUrl"] = logUrl;
      return this;
    }-*/;

    public final native boolean hasLogUrl() /*-{
      return this.hasOwnProperty("logUrl");
    }-*/;

    public static native InstanceLogImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ListEventsRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.ListEventsRequest {
    protected ListEventsRequestImpl() {}

    @Override
    public final native java.lang.String getEnvironmentId() /*-{
      return this["environmentId"];
    }-*/;

    public final native ListEventsRequestImpl setEnvironmentId(java.lang.String environmentId) /*-{
      this["environmentId"] = environmentId;
      return this;
    }-*/;

    public final native boolean hasEnvironmentId() /*-{
      return this.hasOwnProperty("environmentId");
    }-*/;

    @Override
    public final native int getMaxRecords() /*-{
      return this["maxRecords"];
    }-*/;

    public final native ListEventsRequestImpl setMaxRecords(int maxRecords) /*-{
      this["maxRecords"] = maxRecords;
      return this;
    }-*/;

    public final native boolean hasMaxRecords() /*-{
      return this.hasOwnProperty("maxRecords");
    }-*/;

    @Override
    public final native double getEndTime() /*-{
      return this["endTime"];
    }-*/;

    public final native ListEventsRequestImpl setEndTime(double endTime) /*-{
      this["endTime"] = endTime;
      return this;
    }-*/;

    public final native boolean hasEndTime() /*-{
      return this.hasOwnProperty("endTime");
    }-*/;

    @Override
    public final native java.lang.String getNextToken() /*-{
      return this["nextToken"];
    }-*/;

    public final native ListEventsRequestImpl setNextToken(java.lang.String nextToken) /*-{
      this["nextToken"] = nextToken;
      return this;
    }-*/;

    public final native boolean hasNextToken() /*-{
      return this.hasOwnProperty("nextToken");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity getSeverity() /*-{
      return @com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity::valueOf(Ljava/lang/String;)(this["severity"]);
    }-*/;

    public final native ListEventsRequestImpl setSeverity(com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity severity) /*-{
      severity = severity.@com.codenvy.ide.ext.aws.shared.beanstalk.EventsSeverity::toString()();
      this["severity"] = severity;
      return this;
    }-*/;

    public final native boolean hasSeverity() /*-{
      return this.hasOwnProperty("severity");
    }-*/;

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native ListEventsRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native ListEventsRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native ListEventsRequestImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native double getStartTime() /*-{
      return this["startTime"];
    }-*/;

    public final native ListEventsRequestImpl setStartTime(double startTime) /*-{
      this["startTime"] = startTime;
      return this;
    }-*/;

    public final native boolean hasStartTime() /*-{
      return this.hasOwnProperty("startTime");
    }-*/;

    public static native ListEventsRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3ItemImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.S3Item {
    protected S3ItemImpl() {}

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native S3ItemImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native S3ItemImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    public static native S3ItemImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SolutionStackImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.SolutionStack {
    protected SolutionStackImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getPermittedFileTypes() /*-{
      return this["permittedFileTypes"];
    }-*/;

    public final native SolutionStackImpl setPermittedFileTypes(com.codenvy.ide.json.JsonArray<java.lang.String> permittedFileTypes) /*-{
      this["permittedFileTypes"] = permittedFileTypes;
      return this;
    }-*/;

    public final native boolean hasPermittedFileTypes() /*-{
      return this.hasOwnProperty("permittedFileTypes");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native SolutionStackImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    public static native SolutionStackImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SolutionStackConfigurationOptionsRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.SolutionStackConfigurationOptionsRequest {
    protected SolutionStackConfigurationOptionsRequestImpl() {}

    @Override
    public final native java.lang.String getSolutionStackName() /*-{
      return this["solutionStackName"];
    }-*/;

    public final native SolutionStackConfigurationOptionsRequestImpl setSolutionStackName(java.lang.String solutionStackName) /*-{
      this["solutionStackName"] = solutionStackName;
      return this;
    }-*/;

    public final native boolean hasSolutionStackName() /*-{
      return this.hasOwnProperty("solutionStackName");
    }-*/;

    public static native SolutionStackConfigurationOptionsRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class UpdateApplicationRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.UpdateApplicationRequest {
    protected UpdateApplicationRequestImpl() {}

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native UpdateApplicationRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native UpdateApplicationRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native UpdateApplicationRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class UpdateApplicationVersionRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.UpdateApplicationVersionRequest {
    protected UpdateApplicationVersionRequestImpl() {}

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native UpdateApplicationVersionRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native UpdateApplicationVersionRequestImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native UpdateApplicationVersionRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native UpdateApplicationVersionRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class UpdateConfigurationTemplateRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.UpdateConfigurationTemplateRequest {
    protected UpdateConfigurationTemplateRequestImpl() {}

    @Override
    public final native java.lang.String getApplicationName() /*-{
      return this["applicationName"];
    }-*/;

    public final native UpdateConfigurationTemplateRequestImpl setApplicationName(java.lang.String applicationName) /*-{
      this["applicationName"] = applicationName;
      return this;
    }-*/;

    public final native boolean hasApplicationName() /*-{
      return this.hasOwnProperty("applicationName");
    }-*/;

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native UpdateConfigurationTemplateRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native UpdateConfigurationTemplateRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native UpdateConfigurationTemplateRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class UpdateEnvironmentRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.beanstalk.UpdateEnvironmentRequest {
    protected UpdateEnvironmentRequestImpl() {}

    @Override
    public final native java.lang.String getTemplateName() /*-{
      return this["templateName"];
    }-*/;

    public final native UpdateEnvironmentRequestImpl setTemplateName(java.lang.String templateName) /*-{
      this["templateName"] = templateName;
      return this;
    }-*/;

    public final native boolean hasTemplateName() /*-{
      return this.hasOwnProperty("templateName");
    }-*/;

    @Override
    public final native java.lang.String getVersionLabel() /*-{
      return this["versionLabel"];
    }-*/;

    public final native UpdateEnvironmentRequestImpl setVersionLabel(java.lang.String versionLabel) /*-{
      this["versionLabel"] = versionLabel;
      return this;
    }-*/;

    public final native boolean hasVersionLabel() /*-{
      return this.hasOwnProperty("versionLabel");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native UpdateEnvironmentRequestImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> getOptions() /*-{
      return this["options"];
    }-*/;

    public final native UpdateEnvironmentRequestImpl setOptions(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption> options) /*-{
      this["options"] = options;
      return this;
    }-*/;

    public final native boolean hasOptions() /*-{
      return this.hasOwnProperty("options");
    }-*/;

    public static native UpdateEnvironmentRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ImageInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.ImageInfo {
    protected ImageInfoImpl() {}

    @Override
    public final native java.lang.String getOwnerId() /*-{
      return this["ownerId"];
    }-*/;

    public final native ImageInfoImpl setOwnerId(java.lang.String ownerId) /*-{
      this["ownerId"] = ownerId;
      return this;
    }-*/;

    public final native boolean hasOwnerId() /*-{
      return this.hasOwnProperty("ownerId");
    }-*/;

    @Override
    public final native java.lang.String getAmiId() /*-{
      return this["amiId"];
    }-*/;

    public final native ImageInfoImpl setAmiId(java.lang.String amiId) /*-{
      this["amiId"] = amiId;
      return this;
    }-*/;

    public final native boolean hasAmiId() /*-{
      return this.hasOwnProperty("amiId");
    }-*/;

    @Override
    public final native java.lang.String getOwnerAlias() /*-{
      return this["ownerAlias"];
    }-*/;

    public final native ImageInfoImpl setOwnerAlias(java.lang.String ownerAlias) /*-{
      this["ownerAlias"] = ownerAlias;
      return this;
    }-*/;

    public final native boolean hasOwnerAlias() /*-{
      return this.hasOwnProperty("ownerAlias");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.ec2.ImageState getState() /*-{
      return @com.codenvy.ide.ext.aws.shared.ec2.ImageState::valueOf(Ljava/lang/String;)(this["state"]);
    }-*/;

    public final native ImageInfoImpl setState(com.codenvy.ide.ext.aws.shared.ec2.ImageState state) /*-{
      state = state.@com.codenvy.ide.ext.aws.shared.ec2.ImageState::toString()();
      this["state"] = state;
      return this;
    }-*/;

    public final native boolean hasState() /*-{
      return this.hasOwnProperty("state");
    }-*/;

    @Override
    public final native java.lang.String getManifest() /*-{
      return this["manifest"];
    }-*/;

    public final native ImageInfoImpl setManifest(java.lang.String manifest) /*-{
      this["manifest"] = manifest;
      return this;
    }-*/;

    public final native boolean hasManifest() /*-{
      return this.hasOwnProperty("manifest");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonStringMap<java.lang.String> getTags() /*-{
      return this["tags"];
    }-*/;

    public final native ImageInfoImpl setTags(com.codenvy.ide.json.JsonStringMap<java.lang.String> tags) /*-{
      this["tags"] = tags;
      return this;
    }-*/;

    public final native boolean hasTags() /*-{
      return this.hasOwnProperty("tags");
    }-*/;

    public static native ImageInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ImagesListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.ImagesList {
    protected ImagesListImpl() {}

    @Override
    public final native int getTotal() /*-{
      return this["total"];
    }-*/;

    public final native ImagesListImpl setTotal(int total) /*-{
      this["total"] = total;
      return this;
    }-*/;

    public final native boolean hasTotal() /*-{
      return this.hasOwnProperty("total");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.ec2.ImageInfo> getImages() /*-{
      return this["images"];
    }-*/;

    public final native ImagesListImpl setImages(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.ec2.ImageInfo> images) /*-{
      this["images"] = images;
      return this;
    }-*/;

    public final native boolean hasImages() /*-{
      return this.hasOwnProperty("images");
    }-*/;

    @Override
    public final native boolean isHasMore() /*-{
      return this["isHasMore"];
    }-*/;

    public final native ImagesListImpl setIsHasMore(boolean isHasMore) /*-{
      this["isHasMore"] = isHasMore;
      return this;
    }-*/;

    public final native boolean hasIsHasMore() /*-{
      return this.hasOwnProperty("isHasMore");
    }-*/;

    @Override
    public final native int getNextSkip() /*-{
      return this["nextSkip"];
    }-*/;

    public final native ImagesListImpl setNextSkip(int nextSkip) /*-{
      this["nextSkip"] = nextSkip;
      return this;
    }-*/;

    public final native boolean hasNextSkip() /*-{
      return this.hasOwnProperty("nextSkip");
    }-*/;

    @Override
    public final native int getMaxItems() /*-{
      return this["maxItems"];
    }-*/;

    public final native ImagesListImpl setMaxItems(int maxItems) /*-{
      this["maxItems"] = maxItems;
      return this;
    }-*/;

    public final native boolean hasMaxItems() /*-{
      return this.hasOwnProperty("maxItems");
    }-*/;

    public static native ImagesListImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class InstanceInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo {
    protected InstanceInfoImpl() {}

    @Override
    public final native java.lang.String getImageId() /*-{
      return this["imageId"];
    }-*/;

    public final native InstanceInfoImpl setImageId(java.lang.String imageId) /*-{
      this["imageId"] = imageId;
      return this;
    }-*/;

    public final native boolean hasImageId() /*-{
      return this.hasOwnProperty("imageId");
    }-*/;

    @Override
    public final native java.lang.String getPublicDNSName() /*-{
      return this["publicDNSName"];
    }-*/;

    public final native InstanceInfoImpl setPublicDNSName(java.lang.String publicDNSName) /*-{
      this["publicDNSName"] = publicDNSName;
      return this;
    }-*/;

    public final native boolean hasPublicDNSName() /*-{
      return this.hasOwnProperty("publicDNSName");
    }-*/;

    @Override
    public final native java.lang.String getRootDeviceType() /*-{
      return this["rootDeviceType"];
    }-*/;

    public final native InstanceInfoImpl setRootDeviceType(java.lang.String rootDeviceType) /*-{
      this["rootDeviceType"] = rootDeviceType;
      return this;
    }-*/;

    public final native boolean hasRootDeviceType() /*-{
      return this.hasOwnProperty("rootDeviceType");
    }-*/;

    @Override
    public final native java.lang.String getImageType() /*-{
      return this["imageType"];
    }-*/;

    public final native InstanceInfoImpl setImageType(java.lang.String imageType) /*-{
      this["imageType"] = imageType;
      return this;
    }-*/;

    public final native boolean hasImageType() /*-{
      return this.hasOwnProperty("imageType");
    }-*/;

    @Override
    public final native java.lang.String getAvailabilityZone() /*-{
      return this["availabilityZone"];
    }-*/;

    public final native InstanceInfoImpl setAvailabilityZone(java.lang.String availabilityZone) /*-{
      this["availabilityZone"] = availabilityZone;
      return this;
    }-*/;

    public final native boolean hasAvailabilityZone() /*-{
      return this.hasOwnProperty("availabilityZone");
    }-*/;

    @Override
    public final native java.lang.String getKeyName() /*-{
      return this["keyName"];
    }-*/;

    public final native InstanceInfoImpl setKeyName(java.lang.String keyName) /*-{
      this["keyName"] = keyName;
      return this;
    }-*/;

    public final native boolean hasKeyName() /*-{
      return this.hasOwnProperty("keyName");
    }-*/;

    @Override
    public final native double getLaunchTime() /*-{
      return this["launchTime"];
    }-*/;

    public final native InstanceInfoImpl setLaunchTime(double launchTime) /*-{
      this["launchTime"] = launchTime;
      return this;
    }-*/;

    public final native boolean hasLaunchTime() /*-{
      return this.hasOwnProperty("launchTime");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getSetSecurityGroupsNames() /*-{
      return this["setSecurityGroupsNames"];
    }-*/;

    public final native InstanceInfoImpl setSetSecurityGroupsNames(com.codenvy.ide.json.JsonArray<java.lang.String> setSecurityGroupsNames) /*-{
      this["setSecurityGroupsNames"] = setSecurityGroupsNames;
      return this;
    }-*/;

    public final native boolean hasSetSecurityGroupsNames() /*-{
      return this.hasOwnProperty("setSecurityGroupsNames");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native InstanceInfoImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.ec2.InstanceState getState() /*-{
      return @com.codenvy.ide.ext.aws.shared.ec2.InstanceState::valueOf(Ljava/lang/String;)(this["state"]);
    }-*/;

    public final native InstanceInfoImpl setState(com.codenvy.ide.ext.aws.shared.ec2.InstanceState state) /*-{
      state = state.@com.codenvy.ide.ext.aws.shared.ec2.InstanceState::toString()();
      this["state"] = state;
      return this;
    }-*/;

    public final native boolean hasState() /*-{
      return this.hasOwnProperty("state");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonStringMap<java.lang.String> getTags() /*-{
      return this["tags"];
    }-*/;

    public final native InstanceInfoImpl setTags(com.codenvy.ide.json.JsonStringMap<java.lang.String> tags) /*-{
      this["tags"] = tags;
      return this;
    }-*/;

    public final native boolean hasTags() /*-{
      return this.hasOwnProperty("tags");
    }-*/;

    public static native InstanceInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class KeyPairInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.KeyPairInfo {
    protected KeyPairInfoImpl() {}

    @Override
    public final native java.lang.String getFingerprint() /*-{
      return this["fingerprint"];
    }-*/;

    public final native KeyPairInfoImpl setFingerprint(java.lang.String fingerprint) /*-{
      this["fingerprint"] = fingerprint;
      return this;
    }-*/;

    public final native boolean hasFingerprint() /*-{
      return this.hasOwnProperty("fingerprint");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native KeyPairInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    public static native KeyPairInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class RegionInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.RegionInfo {
    protected RegionInfoImpl() {}

    @Override
    public final native java.lang.String getEndpoint() /*-{
      return this["endpoint"];
    }-*/;

    public final native RegionInfoImpl setEndpoint(java.lang.String endpoint) /*-{
      this["endpoint"] = endpoint;
      return this;
    }-*/;

    public final native boolean hasEndpoint() /*-{
      return this.hasOwnProperty("endpoint");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native RegionInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    public static native RegionInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class RunInstanceRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.RunInstanceRequest {
    protected RunInstanceRequestImpl() {}

    @Override
    public final native java.lang.String getImageId() /*-{
      return this["imageId"];
    }-*/;

    public final native RunInstanceRequestImpl setImageId(java.lang.String imageId) /*-{
      this["imageId"] = imageId;
      return this;
    }-*/;

    public final native boolean hasImageId() /*-{
      return this.hasOwnProperty("imageId");
    }-*/;

    @Override
    public final native java.lang.String getAvailabilityZone() /*-{
      return this["availabilityZone"];
    }-*/;

    public final native RunInstanceRequestImpl setAvailabilityZone(java.lang.String availabilityZone) /*-{
      this["availabilityZone"] = availabilityZone;
      return this;
    }-*/;

    public final native boolean hasAvailabilityZone() /*-{
      return this.hasOwnProperty("availabilityZone");
    }-*/;

    @Override
    public final native java.lang.String getKeyName() /*-{
      return this["keyName"];
    }-*/;

    public final native RunInstanceRequestImpl setKeyName(java.lang.String keyName) /*-{
      this["keyName"] = keyName;
      return this;
    }-*/;

    public final native boolean hasKeyName() /*-{
      return this.hasOwnProperty("keyName");
    }-*/;

    @Override
    public final native java.lang.String getInstanceType() /*-{
      return this["instanceType"];
    }-*/;

    public final native RunInstanceRequestImpl setInstanceType(java.lang.String instanceType) /*-{
      this["instanceType"] = instanceType;
      return this;
    }-*/;

    public final native boolean hasInstanceType() /*-{
      return this.hasOwnProperty("instanceType");
    }-*/;

    @Override
    public final native int getNumberOfInstances() /*-{
      return this["numberOfInstances"];
    }-*/;

    public final native RunInstanceRequestImpl setNumberOfInstances(int numberOfInstances) /*-{
      this["numberOfInstances"] = numberOfInstances;
      return this;
    }-*/;

    public final native boolean hasNumberOfInstances() /*-{
      return this.hasOwnProperty("numberOfInstances");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getSecurityGroupsIds() /*-{
      return this["securityGroupsIds"];
    }-*/;

    public final native RunInstanceRequestImpl setSecurityGroupsIds(com.codenvy.ide.json.JsonArray<java.lang.String> securityGroupsIds) /*-{
      this["securityGroupsIds"] = securityGroupsIds;
      return this;
    }-*/;

    public final native boolean hasSecurityGroupsIds() /*-{
      return this.hasOwnProperty("securityGroupsIds");
    }-*/;

    public static native RunInstanceRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SecurityGroupInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.ec2.SecurityGroupInfo {
    protected SecurityGroupInfoImpl() {}

    @Override
    public final native java.lang.String getOwnerId() /*-{
      return this["ownerId"];
    }-*/;

    public final native SecurityGroupInfoImpl setOwnerId(java.lang.String ownerId) /*-{
      this["ownerId"] = ownerId;
      return this;
    }-*/;

    public final native boolean hasOwnerId() /*-{
      return this.hasOwnProperty("ownerId");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native SecurityGroupInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native SecurityGroupInfoImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native SecurityGroupInfoImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native SecurityGroupInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class NewS3ObjectImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.NewS3Object {
    protected NewS3ObjectImpl() {}

    @Override
    public final native java.lang.String getVersionId() /*-{
      return this["versionId"];
    }-*/;

    public final native NewS3ObjectImpl setVersionId(java.lang.String versionId) /*-{
      this["versionId"] = versionId;
      return this;
    }-*/;

    public final native boolean hasVersionId() /*-{
      return this.hasOwnProperty("versionId");
    }-*/;

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native NewS3ObjectImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native NewS3ObjectImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    public static native NewS3ObjectImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3AccessControlImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3AccessControl {
    protected S3AccessControlImpl() {}

    @Override
    public final native com.codenvy.ide.ext.aws.shared.s3.S3IdentityType getIdentityType() /*-{
      return @com.codenvy.ide.ext.aws.shared.s3.S3IdentityType::valueOf(Ljava/lang/String;)(this["identityType"]);
    }-*/;

    public final native S3AccessControlImpl setIdentityType(com.codenvy.ide.ext.aws.shared.s3.S3IdentityType identityType) /*-{
      identityType = identityType.@com.codenvy.ide.ext.aws.shared.s3.S3IdentityType::toString()();
      this["identityType"] = identityType;
      return this;
    }-*/;

    public final native boolean hasIdentityType() /*-{
      return this.hasOwnProperty("identityType");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.s3.S3Permission getPermission() /*-{
      return @com.codenvy.ide.ext.aws.shared.s3.S3Permission::valueOf(Ljava/lang/String;)(this["permission"]);
    }-*/;

    public final native S3AccessControlImpl setPermission(com.codenvy.ide.ext.aws.shared.s3.S3Permission permission) /*-{
      permission = permission.@com.codenvy.ide.ext.aws.shared.s3.S3Permission::toString()();
      this["permission"] = permission;
      return this;
    }-*/;

    public final native boolean hasPermission() /*-{
      return this.hasOwnProperty("permission");
    }-*/;

    @Override
    public final native java.lang.String getIdentifier() /*-{
      return this["identifier"];
    }-*/;

    public final native S3AccessControlImpl setIdentifier(java.lang.String identifier) /*-{
      this["identifier"] = identifier;
      return this;
    }-*/;

    public final native boolean hasIdentifier() /*-{
      return this.hasOwnProperty("identifier");
    }-*/;

    public static native S3AccessControlImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3BucketImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3Bucket {
    protected S3BucketImpl() {}

    @Override
    public final native java.lang.Long getCreated() /*-{
      return this["created"];
    }-*/;

    public final native S3BucketImpl setCreated(java.lang.Long created) /*-{
      this["created"] = created;
      return this;
    }-*/;

    public final native boolean hasCreated() /*-{
      return this.hasOwnProperty("created");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native S3BucketImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.s3.S3Owner getOwner() /*-{
      return this["owner"];
    }-*/;

    public final native S3BucketImpl setOwner(com.codenvy.ide.ext.aws.shared.s3.S3Owner owner) /*-{
      this["owner"] = owner;
      return this;
    }-*/;

    public final native boolean hasOwner() /*-{
      return this.hasOwnProperty("owner");
    }-*/;

    public static native S3BucketImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3KeyVersionsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3KeyVersions {
    protected S3KeyVersionsImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getVersions() /*-{
      return this["versions"];
    }-*/;

    public final native S3KeyVersionsImpl setVersions(com.codenvy.ide.json.JsonArray<java.lang.String> versions) /*-{
      this["versions"] = versions;
      return this;
    }-*/;

    public final native boolean hasVersions() /*-{
      return this.hasOwnProperty("versions");
    }-*/;

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native S3KeyVersionsImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    public static native S3KeyVersionsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3ObjectImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3Object {
    protected S3ObjectImpl() {}

    @Override
    public final native java.lang.String getETag() /*-{
      return this["eTag"];
    }-*/;

    public final native S3ObjectImpl setETag(java.lang.String eTag) /*-{
      this["eTag"] = eTag;
      return this;
    }-*/;

    public final native boolean hasETag() /*-{
      return this.hasOwnProperty("eTag");
    }-*/;

    @Override
    public final native java.lang.String getStorageClass() /*-{
      return this["storageClass"];
    }-*/;

    public final native S3ObjectImpl setStorageClass(java.lang.String storageClass) /*-{
      this["storageClass"] = storageClass;
      return this;
    }-*/;

    public final native boolean hasStorageClass() /*-{
      return this.hasOwnProperty("storageClass");
    }-*/;

    @Override
    public final native double getUpdated() /*-{
      return this["updated"];
    }-*/;

    public final native S3ObjectImpl setUpdated(double updated) /*-{
      this["updated"] = updated;
      return this;
    }-*/;

    public final native boolean hasUpdated() /*-{
      return this.hasOwnProperty("updated");
    }-*/;

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native S3ObjectImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native S3ObjectImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    @Override
    public final native double getSize() /*-{
      return this["size"];
    }-*/;

    public final native S3ObjectImpl setSize(double size) /*-{
      this["size"] = size;
      return this;
    }-*/;

    public final native boolean hasSize() /*-{
      return this.hasOwnProperty("size");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.s3.S3Owner getOwner() /*-{
      return this["owner"];
    }-*/;

    public final native S3ObjectImpl setOwner(com.codenvy.ide.ext.aws.shared.s3.S3Owner owner) /*-{
      this["owner"] = owner;
      return this;
    }-*/;

    public final native boolean hasOwner() /*-{
      return this.hasOwnProperty("owner");
    }-*/;

    public static native S3ObjectImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3ObjectVersionImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3ObjectVersion {
    protected S3ObjectVersionImpl() {}

    @Override
    public final native java.lang.String getVersionId() /*-{
      return this["versionId"];
    }-*/;

    public final native S3ObjectVersionImpl setVersionId(java.lang.String versionId) /*-{
      this["versionId"] = versionId;
      return this;
    }-*/;

    public final native boolean hasVersionId() /*-{
      return this.hasOwnProperty("versionId");
    }-*/;

    @Override
    public final native java.lang.Long getLastModifiedDate() /*-{
      return this["lastModifiedDate"];
    }-*/;

    public final native S3ObjectVersionImpl setLastModifiedDate(java.lang.Long lastModifiedDate) /*-{
      this["lastModifiedDate"] = lastModifiedDate;
      return this;
    }-*/;

    public final native boolean hasLastModifiedDate() /*-{
      return this.hasOwnProperty("lastModifiedDate");
    }-*/;

    @Override
    public final native java.lang.String getS3Key() /*-{
      return this["s3Key"];
    }-*/;

    public final native S3ObjectVersionImpl setS3Key(java.lang.String s3Key) /*-{
      this["s3Key"] = s3Key;
      return this;
    }-*/;

    public final native boolean hasS3Key() /*-{
      return this.hasOwnProperty("s3Key");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native S3ObjectVersionImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    @Override
    public final native java.lang.Long getSize() /*-{
      return this["size"];
    }-*/;

    public final native S3ObjectVersionImpl setSize(java.lang.Long size) /*-{
      this["size"] = size;
      return this;
    }-*/;

    public final native boolean hasSize() /*-{
      return this.hasOwnProperty("size");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.aws.shared.s3.S3Owner getOwner() /*-{
      return this["owner"];
    }-*/;

    public final native S3ObjectVersionImpl setOwner(com.codenvy.ide.ext.aws.shared.s3.S3Owner owner) /*-{
      this["owner"] = owner;
      return this;
    }-*/;

    public final native boolean hasOwner() /*-{
      return this.hasOwnProperty("owner");
    }-*/;

    public static native S3ObjectVersionImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3ObjectsListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList {
    protected S3ObjectsListImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.s3.S3Object> getObjects() /*-{
      return this["objects"];
    }-*/;

    public final native S3ObjectsListImpl setObjects(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.s3.S3Object> objects) /*-{
      this["objects"] = objects;
      return this;
    }-*/;

    public final native boolean hasObjects() /*-{
      return this.hasOwnProperty("objects");
    }-*/;

    @Override
    public final native double getMaxKeys() /*-{
      return this["maxKeys"];
    }-*/;

    public final native S3ObjectsListImpl setMaxKeys(double maxKeys) /*-{
      this["maxKeys"] = maxKeys;
      return this;
    }-*/;

    public final native boolean hasMaxKeys() /*-{
      return this.hasOwnProperty("maxKeys");
    }-*/;

    @Override
    public final native java.lang.String getNextMarker() /*-{
      return this["nextMarker"];
    }-*/;

    public final native S3ObjectsListImpl setNextMarker(java.lang.String nextMarker) /*-{
      this["nextMarker"] = nextMarker;
      return this;
    }-*/;

    public final native boolean hasNextMarker() /*-{
      return this.hasOwnProperty("nextMarker");
    }-*/;

    @Override
    public final native java.lang.String getS3Bucket() /*-{
      return this["s3Bucket"];
    }-*/;

    public final native S3ObjectsListImpl setS3Bucket(java.lang.String s3Bucket) /*-{
      this["s3Bucket"] = s3Bucket;
      return this;
    }-*/;

    public final native boolean hasS3Bucket() /*-{
      return this.hasOwnProperty("s3Bucket");
    }-*/;

    @Override
    public final native java.lang.String getPrefix() /*-{
      return this["prefix"];
    }-*/;

    public final native S3ObjectsListImpl setPrefix(java.lang.String prefix) /*-{
      this["prefix"] = prefix;
      return this;
    }-*/;

    public final native boolean hasPrefix() /*-{
      return this.hasOwnProperty("prefix");
    }-*/;

    public static native S3ObjectsListImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class S3OwnerImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.S3Owner {
    protected S3OwnerImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native S3OwnerImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native S3OwnerImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native S3OwnerImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class UpdateAccessControlRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.aws.shared.s3.UpdateAccessControlRequest {
    protected UpdateAccessControlRequestImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.s3.S3AccessControl> getS3AccessControlsToAdd() /*-{
      return this["s3AccessControlsToAdd"];
    }-*/;

    public final native UpdateAccessControlRequestImpl setS3AccessControlsToAdd(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.s3.S3AccessControl> s3AccessControlsToAdd) /*-{
      this["s3AccessControlsToAdd"] = s3AccessControlsToAdd;
      return this;
    }-*/;

    public final native boolean hasS3AccessControlsToAdd() /*-{
      return this.hasOwnProperty("s3AccessControlsToAdd");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.s3.S3AccessControl> getS3AccessControlsToDelete() /*-{
      return this["s3AccessControlsToDelete"];
    }-*/;

    public final native UpdateAccessControlRequestImpl setS3AccessControlsToDelete(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.aws.shared.s3.S3AccessControl> s3AccessControlsToDelete) /*-{
      this["s3AccessControlsToDelete"] = s3AccessControlsToDelete;
      return this;
    }-*/;

    public final native boolean hasS3AccessControlsToDelete() /*-{
      return this.hasOwnProperty("s3AccessControlsToDelete");
    }-*/;

    public static native UpdateAccessControlRequestImpl make() /*-{
      return {

      };
    }-*/;  }

}