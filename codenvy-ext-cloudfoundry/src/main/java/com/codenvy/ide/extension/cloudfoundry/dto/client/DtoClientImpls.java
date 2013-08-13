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
package com.codenvy.ide.extension.cloudfoundry.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "bb7277d9975e8bd4db7e8f077379a6290871270f";


  public static class ApplicationMetaInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.ApplicationMetaInfo {
    protected ApplicationMetaInfoImpl() {}

    @Override
    public final native java.lang.String getDebug() /*-{
      return this["debug"];
    }-*/;

    public final native ApplicationMetaInfoImpl setDebug(java.lang.String debug) /*-{
      this["debug"] = debug;
      return this;
    }-*/;

    public final native boolean hasDebug() /*-{
      return this.hasOwnProperty("debug");
    }-*/;

    @Override
    public final native double getVersion() /*-{
      return this["version"];
    }-*/;

    public final native ApplicationMetaInfoImpl setVersion(double version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    @Override
    public final native java.lang.String getConsole() /*-{
      return this["console"];
    }-*/;

    public final native ApplicationMetaInfoImpl setConsole(java.lang.String console) /*-{
      this["console"] = console;
      return this;
    }-*/;

    public final native boolean hasConsole() /*-{
      return this.hasOwnProperty("console");
    }-*/;

    @Override
    public final native double getCreated() /*-{
      return this["created"];
    }-*/;

    public final native ApplicationMetaInfoImpl setCreated(double created) /*-{
      this["created"] = created;
      return this;
    }-*/;

    public final native boolean hasCreated() /*-{
      return this.hasOwnProperty("created");
    }-*/;

    public static native ApplicationMetaInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CloudFoundryApplicationImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication {
    protected CloudFoundryApplicationImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CloudFoundryApplicationImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplicationResources getResources() /*-{
      return this["resources"];
    }-*/;

    public final native CloudFoundryApplicationImpl setResources(com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplicationResources resources) /*-{
      this["resources"] = resources;
      return this;
    }-*/;

    public final native boolean hasResources() /*-{
      return this.hasOwnProperty("resources");
    }-*/;

    @Override
    public final native java.lang.String getDebug() /*-{
      return this["debug"];
    }-*/;

    public final native CloudFoundryApplicationImpl setDebug(java.lang.String debug) /*-{
      this["debug"] = debug;
      return this;
    }-*/;

    public final native boolean hasDebug() /*-{
      return this.hasOwnProperty("debug");
    }-*/;

    @Override
    public final native java.lang.String getState() /*-{
      return this["state"];
    }-*/;

    public final native CloudFoundryApplicationImpl setState(java.lang.String state) /*-{
      this["state"] = state;
      return this;
    }-*/;

    public final native boolean hasState() /*-{
      return this.hasOwnProperty("state");
    }-*/;

    @Override
    public final native java.lang.String getVersion() /*-{
      return this["version"];
    }-*/;

    public final native CloudFoundryApplicationImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getServices() /*-{
      return this["services"];
    }-*/;

    public final native CloudFoundryApplicationImpl setServices(com.codenvy.ide.json.JsonArray<java.lang.String> services) /*-{
      this["services"] = services;
      return this;
    }-*/;

    public final native boolean hasServices() /*-{
      return this.hasOwnProperty("services");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getEnv() /*-{
      return this["env"];
    }-*/;

    public final native CloudFoundryApplicationImpl setEnv(com.codenvy.ide.json.JsonArray<java.lang.String> env) /*-{
      this["env"] = env;
      return this;
    }-*/;

    public final native boolean hasEnv() /*-{
      return this.hasOwnProperty("env");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getUris() /*-{
      return this["uris"];
    }-*/;

    public final native CloudFoundryApplicationImpl setUris(com.codenvy.ide.json.JsonArray<java.lang.String> uris) /*-{
      this["uris"] = uris;
      return this;
    }-*/;

    public final native boolean hasUris() /*-{
      return this.hasOwnProperty("uris");
    }-*/;

    @Override
    public final native int getInstances() /*-{
      return this["instances"];
    }-*/;

    public final native CloudFoundryApplicationImpl setInstances(int instances) /*-{
      this["instances"] = instances;
      return this;
    }-*/;

    public final native boolean hasInstances() /*-{
      return this.hasOwnProperty("instances");
    }-*/;

    @Override
    public final native int getRunningInstances() /*-{
      return this["runningInstances"];
    }-*/;

    public final native CloudFoundryApplicationImpl setRunningInstances(int runningInstances) /*-{
      this["runningInstances"] = runningInstances;
      return this;
    }-*/;

    public final native boolean hasRunningInstances() /*-{
      return this.hasOwnProperty("runningInstances");
    }-*/;

    @Override
    public final native com.codenvy.ide.extension.cloudfoundry.shared.Staging getStaging() /*-{
      return this["staging"];
    }-*/;

    public final native CloudFoundryApplicationImpl setStaging(com.codenvy.ide.extension.cloudfoundry.shared.Staging staging) /*-{
      this["staging"] = staging;
      return this;
    }-*/;

    public final native boolean hasStaging() /*-{
      return this.hasOwnProperty("staging");
    }-*/;

    @Override
    public final native com.codenvy.ide.extension.cloudfoundry.shared.ApplicationMetaInfo getMeta() /*-{
      return this["meta"];
    }-*/;

    public final native CloudFoundryApplicationImpl setMeta(com.codenvy.ide.extension.cloudfoundry.shared.ApplicationMetaInfo meta) /*-{
      this["meta"] = meta;
      return this;
    }-*/;

    public final native boolean hasMeta() /*-{
      return this.hasOwnProperty("meta");
    }-*/;

    public static native CloudFoundryApplicationImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CloudFoundryApplicationResourcesImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplicationResources {
    protected CloudFoundryApplicationResourcesImpl() {}

    @Override
    public final native int getMemory() /*-{
      return this["memory"];
    }-*/;

    public final native CloudFoundryApplicationResourcesImpl setMemory(int memory) /*-{
      this["memory"] = memory;
      return this;
    }-*/;

    public final native boolean hasMemory() /*-{
      return this.hasOwnProperty("memory");
    }-*/;

    @Override
    public final native int getDisk() /*-{
      return this["disk"];
    }-*/;

    public final native CloudFoundryApplicationResourcesImpl setDisk(int disk) /*-{
      this["disk"] = disk;
      return this;
    }-*/;

    public final native boolean hasDisk() /*-{
      return this.hasOwnProperty("disk");
    }-*/;

    public static native CloudFoundryApplicationResourcesImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CloudFoundryServicesImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices {
    protected CloudFoundryServicesImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.extension.cloudfoundry.shared.SystemService> getSystem() /*-{
      return this["system"];
    }-*/;

    public final native CloudFoundryServicesImpl setSystem(com.codenvy.ide.json.JsonArray<com.codenvy.ide.extension.cloudfoundry.shared.SystemService> system) /*-{
      this["system"] = system;
      return this;
    }-*/;

    public final native boolean hasSystem() /*-{
      return this.hasOwnProperty("system");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService> getProvisioned() /*-{
      return this["provisioned"];
    }-*/;

    public final native CloudFoundryServicesImpl setProvisioned(com.codenvy.ide.json.JsonArray<com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService> provisioned) /*-{
      this["provisioned"] = provisioned;
      return this;
    }-*/;

    public final native boolean hasProvisioned() /*-{
      return this.hasOwnProperty("provisioned");
    }-*/;

    public static native CloudFoundryServicesImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CloudfoundryApplicationStatisticsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics {
    protected CloudfoundryApplicationStatisticsImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getState() /*-{
      return this["state"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setState(java.lang.String state) /*-{
      this["state"] = state;
      return this;
    }-*/;

    public final native boolean hasState() /*-{
      return this.hasOwnProperty("state");
    }-*/;

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    @Override
    public final native int getPort() /*-{
      return this["port"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setPort(int port) /*-{
      this["port"] = port;
      return this;
    }-*/;

    public final native boolean hasPort() /*-{
      return this.hasOwnProperty("port");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getUris() /*-{
      return this["uris"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setUris(com.codenvy.ide.json.JsonArray<java.lang.String> uris) /*-{
      this["uris"] = uris;
      return this;
    }-*/;

    public final native boolean hasUris() /*-{
      return this.hasOwnProperty("uris");
    }-*/;

    @Override
    public final native int getDisk() /*-{
      return this["disk"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setDisk(int disk) /*-{
      this["disk"] = disk;
      return this;
    }-*/;

    public final native boolean hasDisk() /*-{
      return this.hasOwnProperty("disk");
    }-*/;

    @Override
    public final native java.lang.String getUptime() /*-{
      return this["uptime"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setUptime(java.lang.String uptime) /*-{
      this["uptime"] = uptime;
      return this;
    }-*/;

    public final native boolean hasUptime() /*-{
      return this.hasOwnProperty("uptime");
    }-*/;

    @Override
    public final native int getCpuCores() /*-{
      return this["cpuCores"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setCpuCores(int cpuCores) /*-{
      this["cpuCores"] = cpuCores;
      return this;
    }-*/;

    public final native boolean hasCpuCores() /*-{
      return this.hasOwnProperty("cpuCores");
    }-*/;

    @Override
    public final native double getCpu() /*-{
      return this["cpu"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setCpu(double cpu) /*-{
      this["cpu"] = cpu;
      return this;
    }-*/;

    public final native boolean hasCpu() /*-{
      return this.hasOwnProperty("cpu");
    }-*/;

    @Override
    public final native int getMem() /*-{
      return this["mem"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setMem(int mem) /*-{
      this["mem"] = mem;
      return this;
    }-*/;

    public final native boolean hasMem() /*-{
      return this.hasOwnProperty("mem");
    }-*/;

    @Override
    public final native int getMemLimit() /*-{
      return this["memLimit"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setMemLimit(int memLimit) /*-{
      this["memLimit"] = memLimit;
      return this;
    }-*/;

    public final native boolean hasMemLimit() /*-{
      return this.hasOwnProperty("memLimit");
    }-*/;

    @Override
    public final native int getDiskLimit() /*-{
      return this["diskLimit"];
    }-*/;

    public final native CloudfoundryApplicationStatisticsImpl setDiskLimit(int diskLimit) /*-{
      this["diskLimit"] = diskLimit;
      return this;
    }-*/;

    public final native boolean hasDiskLimit() /*-{
      return this.hasOwnProperty("diskLimit");
    }-*/;

    public static native CloudfoundryApplicationStatisticsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CreateApplicationRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.CreateApplicationRequest {
    protected CreateApplicationRequestImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CreateApplicationRequestImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native CreateApplicationRequestImpl setType(java.lang.String type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    @Override
    public final native java.lang.String getUrl() /*-{
      return this["url"];
    }-*/;

    public final native CreateApplicationRequestImpl setUrl(java.lang.String url) /*-{
      this["url"] = url;
      return this;
    }-*/;

    public final native boolean hasUrl() /*-{
      return this.hasOwnProperty("url");
    }-*/;

    @Override
    public final native java.lang.String getServer() /*-{
      return this["server"];
    }-*/;

    public final native CreateApplicationRequestImpl setServer(java.lang.String server) /*-{
      this["server"] = server;
      return this;
    }-*/;

    public final native boolean hasServer() /*-{
      return this.hasOwnProperty("server");
    }-*/;

    @Override
    public final native int getInstances() /*-{
      return this["instances"];
    }-*/;

    public final native CreateApplicationRequestImpl setInstances(int instances) /*-{
      this["instances"] = instances;
      return this;
    }-*/;

    public final native boolean hasInstances() /*-{
      return this.hasOwnProperty("instances");
    }-*/;

    @Override
    public final native int getMemory() /*-{
      return this["memory"];
    }-*/;

    public final native CreateApplicationRequestImpl setMemory(int memory) /*-{
      this["memory"] = memory;
      return this;
    }-*/;

    public final native boolean hasMemory() /*-{
      return this.hasOwnProperty("memory");
    }-*/;

    @Override
    public final native boolean isNostart() /*-{
      return this["isNostart"];
    }-*/;

    public final native CreateApplicationRequestImpl setIsNostart(boolean isNostart) /*-{
      this["isNostart"] = isNostart;
      return this;
    }-*/;

    public final native boolean hasIsNostart() /*-{
      return this.hasOwnProperty("isNostart");
    }-*/;

    @Override
    public final native java.lang.String getVfsid() /*-{
      return this["vfsid"];
    }-*/;

    public final native CreateApplicationRequestImpl setVfsid(java.lang.String vfsid) /*-{
      this["vfsid"] = vfsid;
      return this;
    }-*/;

    public final native boolean hasVfsid() /*-{
      return this.hasOwnProperty("vfsid");
    }-*/;

    @Override
    public final native java.lang.String getProjectid() /*-{
      return this["projectid"];
    }-*/;

    public final native CreateApplicationRequestImpl setProjectid(java.lang.String projectid) /*-{
      this["projectid"] = projectid;
      return this;
    }-*/;

    public final native boolean hasProjectid() /*-{
      return this.hasOwnProperty("projectid");
    }-*/;

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
    public final native java.lang.String getPaasprovider() /*-{
      return this["paasprovider"];
    }-*/;

    public final native CreateApplicationRequestImpl setPaasprovider(java.lang.String paasprovider) /*-{
      this["paasprovider"] = paasprovider;
      return this;
    }-*/;

    public final native boolean hasPaasprovider() /*-{
      return this.hasOwnProperty("paasprovider");
    }-*/;

    public static native CreateApplicationRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CredentialsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.Credentials {
    protected CredentialsImpl() {}

    @Override
    public final native java.lang.String getPassword() /*-{
      return this["password"];
    }-*/;

    public final native CredentialsImpl setPassword(java.lang.String password) /*-{
      this["password"] = password;
      return this;
    }-*/;

    public final native boolean hasPassword() /*-{
      return this.hasOwnProperty("password");
    }-*/;

    @Override
    public final native java.lang.String getEmail() /*-{
      return this["email"];
    }-*/;

    public final native CredentialsImpl setEmail(java.lang.String email) /*-{
      this["email"] = email;
      return this;
    }-*/;

    public final native boolean hasEmail() /*-{
      return this.hasOwnProperty("email");
    }-*/;

    @Override
    public final native java.lang.String getServer() /*-{
      return this["server"];
    }-*/;

    public final native CredentialsImpl setServer(java.lang.String server) /*-{
      this["server"] = server;
      return this;
    }-*/;

    public final native boolean hasServer() /*-{
      return this.hasOwnProperty("server");
    }-*/;

    public static native CredentialsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class FrameworkImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.Framework {
    protected FrameworkImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native FrameworkImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getDisplayName() /*-{
      return this["displayName"];
    }-*/;

    public final native FrameworkImpl setDisplayName(java.lang.String displayName) /*-{
      this["displayName"] = displayName;
      return this;
    }-*/;

    public final native boolean hasDisplayName() /*-{
      return this.hasOwnProperty("displayName");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native FrameworkImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native int getMemory() /*-{
      return this["memory"];
    }-*/;

    public final native FrameworkImpl setMemory(int memory) /*-{
      this["memory"] = memory;
      return this;
    }-*/;

    public final native boolean hasMemory() /*-{
      return this.hasOwnProperty("memory");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.extension.cloudfoundry.shared.Runtime> getRuntimes() /*-{
      return this["runtimes"];
    }-*/;

    public final native FrameworkImpl setRuntimes(com.codenvy.ide.json.JsonArray<com.codenvy.ide.extension.cloudfoundry.shared.Runtime> runtimes) /*-{
      this["runtimes"] = runtimes;
      return this;
    }-*/;

    public final native boolean hasRuntimes() /*-{
      return this.hasOwnProperty("runtimes");
    }-*/;

    public static native FrameworkImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class InstanceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.Instance {
    protected InstanceImpl() {}

    @Override
    public final native java.lang.String getConsoleHost() /*-{
      return this["consoleHost"];
    }-*/;

    public final native InstanceImpl setConsoleHost(java.lang.String consoleHost) /*-{
      this["consoleHost"] = consoleHost;
      return this;
    }-*/;

    public final native boolean hasConsoleHost() /*-{
      return this.hasOwnProperty("consoleHost");
    }-*/;

    @Override
    public final native int getConsolePort() /*-{
      return this["consolePort"];
    }-*/;

    public final native InstanceImpl setConsolePort(int consolePort) /*-{
      this["consolePort"] = consolePort;
      return this;
    }-*/;

    public final native boolean hasConsolePort() /*-{
      return this.hasOwnProperty("consolePort");
    }-*/;

    @Override
    public final native java.lang.String getDebugHost() /*-{
      return this["debugHost"];
    }-*/;

    public final native InstanceImpl setDebugHost(java.lang.String debugHost) /*-{
      this["debugHost"] = debugHost;
      return this;
    }-*/;

    public final native boolean hasDebugHost() /*-{
      return this.hasOwnProperty("debugHost");
    }-*/;

    @Override
    public final native int getDebugPort() /*-{
      return this["debugPort"];
    }-*/;

    public final native InstanceImpl setDebugPort(int debugPort) /*-{
      this["debugPort"] = debugPort;
      return this;
    }-*/;

    public final native boolean hasDebugPort() /*-{
      return this.hasOwnProperty("debugPort");
    }-*/;

    public static native InstanceImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ProvisionedServiceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService {
    protected ProvisionedServiceImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ProvisionedServiceImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native ProvisionedServiceImpl setType(java.lang.String type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    @Override
    public final native java.lang.String getVersion() /*-{
      return this["version"];
    }-*/;

    public final native ProvisionedServiceImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    @Override
    public final native java.lang.String getVendor() /*-{
      return this["vendor"];
    }-*/;

    public final native ProvisionedServiceImpl setVendor(java.lang.String vendor) /*-{
      this["vendor"] = vendor;
      return this;
    }-*/;

    public final native boolean hasVendor() /*-{
      return this.hasOwnProperty("vendor");
    }-*/;

    public static native ProvisionedServiceImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class RuntimeImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.Runtime {
    protected RuntimeImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native RuntimeImpl setName(java.lang.String name) /*-{
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

    public final native RuntimeImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native java.lang.String getVersion() /*-{
      return this["version"];
    }-*/;

    public final native RuntimeImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    public static native RuntimeImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class StagingImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.Staging {
    protected StagingImpl() {}

    @Override
    public final native java.lang.String getStack() /*-{
      return this["stack"];
    }-*/;

    public final native StagingImpl setStack(java.lang.String stack) /*-{
      this["stack"] = stack;
      return this;
    }-*/;

    public final native boolean hasStack() /*-{
      return this.hasOwnProperty("stack");
    }-*/;

    @Override
    public final native java.lang.String getModel() /*-{
      return this["model"];
    }-*/;

    public final native StagingImpl setModel(java.lang.String model) /*-{
      this["model"] = model;
      return this;
    }-*/;

    public final native boolean hasModel() /*-{
      return this.hasOwnProperty("model");
    }-*/;

    public static native StagingImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SystemInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.SystemInfo {
    protected SystemInfoImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native SystemInfoImpl setName(java.lang.String name) /*-{
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

    public final native SystemInfoImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native java.lang.String getVersion() /*-{
      return this["version"];
    }-*/;

    public final native SystemInfoImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    @Override
    public final native java.lang.String getUser() /*-{
      return this["user"];
    }-*/;

    public final native SystemInfoImpl setUser(java.lang.String user) /*-{
      this["user"] = user;
      return this;
    }-*/;

    public final native boolean hasUser() /*-{
      return this.hasOwnProperty("user");
    }-*/;

    @Override
    public final native com.codenvy.ide.extension.cloudfoundry.shared.SystemResources getUsage() /*-{
      return this["usage"];
    }-*/;

    public final native SystemInfoImpl setUsage(com.codenvy.ide.extension.cloudfoundry.shared.SystemResources usage) /*-{
      this["usage"] = usage;
      return this;
    }-*/;

    public final native boolean hasUsage() /*-{
      return this.hasOwnProperty("usage");
    }-*/;

    @Override
    public final native com.codenvy.ide.extension.cloudfoundry.shared.SystemResources getLimits() /*-{
      return this["limits"];
    }-*/;

    public final native SystemInfoImpl setLimits(com.codenvy.ide.extension.cloudfoundry.shared.SystemResources limits) /*-{
      this["limits"] = limits;
      return this;
    }-*/;

    public final native boolean hasLimits() /*-{
      return this.hasOwnProperty("limits");
    }-*/;

    @Override
    public final native java.lang.String getSupport() /*-{
      return this["support"];
    }-*/;

    public final native SystemInfoImpl setSupport(java.lang.String support) /*-{
      this["support"] = support;
      return this;
    }-*/;

    public final native boolean hasSupport() /*-{
      return this.hasOwnProperty("support");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonStringMap<com.codenvy.ide.extension.cloudfoundry.shared.Framework> getFrameworks() /*-{
      return this["frameworks"];
    }-*/;

    public final native SystemInfoImpl setFrameworks(com.codenvy.ide.json.JsonStringMap<com.codenvy.ide.extension.cloudfoundry.shared.Framework> frameworks) /*-{
      this["frameworks"] = frameworks;
      return this;
    }-*/;

    public final native boolean hasFrameworks() /*-{
      return this.hasOwnProperty("frameworks");
    }-*/;

    public static native SystemInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SystemResourcesImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.SystemResources {
    protected SystemResourcesImpl() {}

    @Override
    public final native int getServices() /*-{
      return this["services"];
    }-*/;

    public final native SystemResourcesImpl setServices(int services) /*-{
      this["services"] = services;
      return this;
    }-*/;

    public final native boolean hasServices() /*-{
      return this.hasOwnProperty("services");
    }-*/;

    @Override
    public final native int getMemory() /*-{
      return this["memory"];
    }-*/;

    public final native SystemResourcesImpl setMemory(int memory) /*-{
      this["memory"] = memory;
      return this;
    }-*/;

    public final native boolean hasMemory() /*-{
      return this.hasOwnProperty("memory");
    }-*/;

    @Override
    public final native int getApps() /*-{
      return this["apps"];
    }-*/;

    public final native SystemResourcesImpl setApps(int apps) /*-{
      this["apps"] = apps;
      return this;
    }-*/;

    public final native boolean hasApps() /*-{
      return this.hasOwnProperty("apps");
    }-*/;

    public static native SystemResourcesImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SystemServiceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.cloudfoundry.shared.SystemService {
    protected SystemServiceImpl() {}

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native SystemServiceImpl setType(java.lang.String type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native SystemServiceImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native java.lang.String getVersion() /*-{
      return this["version"];
    }-*/;

    public final native SystemServiceImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    @Override
    public final native java.lang.String getVendor() /*-{
      return this["vendor"];
    }-*/;

    public final native SystemServiceImpl setVendor(java.lang.String vendor) /*-{
      this["vendor"] = vendor;
      return this;
    }-*/;

    public final native boolean hasVendor() /*-{
      return this.hasOwnProperty("vendor");
    }-*/;

    public static native SystemServiceImpl make() /*-{
      return {

      };
    }-*/;  }

}