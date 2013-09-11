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
package com.codenvy.ide.ext.appfog.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "e6f0804aeac5c92f25a8fc2e0255b110d093ac1f";


  public static class AppfogProvisionedServiceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService {
    protected AppfogProvisionedServiceImpl() {}

    @Override
    public final native java.lang.String getVendor() /*-{
      return this["vendor"];
    }-*/;

    public final native AppfogProvisionedServiceImpl setVendor(java.lang.String vendor) /*-{
      this["vendor"] = vendor;
      return this;
    }-*/;

    public final native boolean hasVendor() /*-{
      return this.hasOwnProperty("vendor");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.Infra getInfra() /*-{
      return this["infra"];
    }-*/;

    public final native AppfogProvisionedServiceImpl setInfra(com.codenvy.ide.ext.appfog.shared.Infra infra) /*-{
      this["infra"] = infra;
      return this;
    }-*/;

    public final native boolean hasInfra() /*-{
      return this.hasOwnProperty("infra");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native AppfogProvisionedServiceImpl setName(java.lang.String name) /*-{
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

    public final native AppfogProvisionedServiceImpl setType(java.lang.String type) /*-{
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

    public final native AppfogProvisionedServiceImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    public static native AppfogProvisionedServiceImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class AppfogSystemServiceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.AppfogSystemService {
    protected AppfogSystemServiceImpl() {}

    @Override
    public final native java.lang.String getVendor() /*-{
      return this["vendor"];
    }-*/;

    public final native AppfogSystemServiceImpl setVendor(java.lang.String vendor) /*-{
      this["vendor"] = vendor;
      return this;
    }-*/;

    public final native boolean hasVendor() /*-{
      return this.hasOwnProperty("vendor");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.Infra getInfra() /*-{
      return this["infra"];
    }-*/;

    public final native AppfogSystemServiceImpl setInfra(com.codenvy.ide.ext.appfog.shared.Infra infra) /*-{
      this["infra"] = infra;
      return this;
    }-*/;

    public final native boolean hasInfra() /*-{
      return this.hasOwnProperty("infra");
    }-*/;

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native AppfogSystemServiceImpl setType(java.lang.String type) /*-{
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

    public final native AppfogSystemServiceImpl setDescription(java.lang.String description) /*-{
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

    public final native AppfogSystemServiceImpl setVersion(java.lang.String version) /*-{
      this["version"] = version;
      return this;
    }-*/;

    public final native boolean hasVersion() /*-{
      return this.hasOwnProperty("version");
    }-*/;

    public static native AppfogSystemServiceImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class FrameworkImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.Framework {
    protected FrameworkImpl() {}

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
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.appfog.shared.Runtime> getRuntimes() /*-{
      return this["runtimes"];
    }-*/;

    public final native FrameworkImpl setRuntimes(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.appfog.shared.Runtime> runtimes) /*-{
      this["runtimes"] = runtimes;
      return this;
    }-*/;

    public final native boolean hasRuntimes() /*-{
      return this.hasOwnProperty("runtimes");
    }-*/;

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

    public static native FrameworkImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class InfraImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.Infra {
    protected InfraImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native InfraImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getProvider() /*-{
      return this["provider"];
    }-*/;

    public final native InfraImpl setProvider(java.lang.String provider) /*-{
      this["provider"] = provider;
      return this;
    }-*/;

    public final native boolean hasProvider() /*-{
      return this.hasOwnProperty("provider");
    }-*/;

    public static native InfraImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SystemInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.SystemInfo {
    protected SystemInfoImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonStringMap<com.codenvy.ide.ext.appfog.shared.Framework> getFrameworks() /*-{
      return this["frameworks"];
    }-*/;

    public final native SystemInfoImpl setFrameworks(com.codenvy.ide.json.JsonStringMap<com.codenvy.ide.ext.appfog.shared.Framework> frameworks) /*-{
      this["frameworks"] = frameworks;
      return this;
    }-*/;

    public final native boolean hasFrameworks() /*-{
      return this.hasOwnProperty("frameworks");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.SystemResources getUsage() /*-{
      return this["usage"];
    }-*/;

    public final native SystemInfoImpl setUsage(com.codenvy.ide.ext.appfog.shared.SystemResources usage) /*-{
      this["usage"] = usage;
      return this;
    }-*/;

    public final native boolean hasUsage() /*-{
      return this.hasOwnProperty("usage");
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
    public final native com.codenvy.ide.ext.appfog.shared.SystemResources getLimits() /*-{
      return this["limits"];
    }-*/;

    public final native SystemInfoImpl setLimits(com.codenvy.ide.ext.appfog.shared.SystemResources limits) /*-{
      this["limits"] = limits;
      return this;
    }-*/;

    public final native boolean hasLimits() /*-{
      return this.hasOwnProperty("limits");
    }-*/;

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

    public static native SystemInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CreateAppfogApplicationRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.CreateAppfogApplicationRequest {
    protected CreateAppfogApplicationRequestImpl() {}

    @Override
    public final native java.lang.String getProjectid() /*-{
      return this["projectid"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setProjectid(java.lang.String projectid) /*-{
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

    public final native CreateAppfogApplicationRequestImpl setWar(java.lang.String war) /*-{
      this["war"] = war;
      return this;
    }-*/;

    public final native boolean hasWar() /*-{
      return this.hasOwnProperty("war");
    }-*/;

    @Override
    public final native java.lang.String getVfsid() /*-{
      return this["vfsid"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setVfsid(java.lang.String vfsid) /*-{
      this["vfsid"] = vfsid;
      return this;
    }-*/;

    public final native boolean hasVfsid() /*-{
      return this.hasOwnProperty("vfsid");
    }-*/;

    @Override
    public final native boolean isNostart() /*-{
      return this["isNostart"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setIsNostart(boolean isNostart) /*-{
      this["isNostart"] = isNostart;
      return this;
    }-*/;

    public final native boolean hasIsNostart() /*-{
      return this.hasOwnProperty("isNostart");
    }-*/;

    @Override
    public final native int getMemory() /*-{
      return this["memory"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setMemory(int memory) /*-{
      this["memory"] = memory;
      return this;
    }-*/;

    public final native boolean hasMemory() /*-{
      return this.hasOwnProperty("memory");
    }-*/;

    @Override
    public final native int getInstances() /*-{
      return this["instances"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setInstances(int instances) /*-{
      this["instances"] = instances;
      return this;
    }-*/;

    public final native boolean hasInstances() /*-{
      return this.hasOwnProperty("instances");
    }-*/;

    @Override
    public final native java.lang.String getInfra() /*-{
      return this["infra"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setInfra(java.lang.String infra) /*-{
      this["infra"] = infra;
      return this;
    }-*/;

    public final native boolean hasInfra() /*-{
      return this.hasOwnProperty("infra");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CreateAppfogApplicationRequestImpl setName(java.lang.String name) /*-{
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

    public final native CreateAppfogApplicationRequestImpl setType(java.lang.String type) /*-{
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

    public final native CreateAppfogApplicationRequestImpl setUrl(java.lang.String url) /*-{
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

    public final native CreateAppfogApplicationRequestImpl setServer(java.lang.String server) /*-{
      this["server"] = server;
      return this;
    }-*/;

    public final native boolean hasServer() /*-{
      return this.hasOwnProperty("server");
    }-*/;

    public static native CreateAppfogApplicationRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ApplicationMetaInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.ApplicationMetaInfo {
    protected ApplicationMetaInfoImpl() {}

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

    public static native ApplicationMetaInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class SystemResourcesImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.SystemResources {
    protected SystemResourcesImpl() {}

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

    public static native SystemResourcesImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class StagingImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.Staging {
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


  public static class RuntimeImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.Runtime {
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


  public static class AppfogApplicationResourcesImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.AppfogApplicationResources {
    protected AppfogApplicationResourcesImpl() {}

    @Override
    public final native int getMemory() /*-{
      return this["memory"];
    }-*/;

    public final native AppfogApplicationResourcesImpl setMemory(int memory) /*-{
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

    public final native AppfogApplicationResourcesImpl setDisk(int disk) /*-{
      this["disk"] = disk;
      return this;
    }-*/;

    public final native boolean hasDisk() /*-{
      return this.hasOwnProperty("disk");
    }-*/;

    public static native AppfogApplicationResourcesImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class AppfogApplicationStatisticsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.AppfogApplicationStatistics {
    protected AppfogApplicationStatisticsImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getUris() /*-{
      return this["uris"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setUris(com.codenvy.ide.json.JsonArray<java.lang.String> uris) /*-{
      this["uris"] = uris;
      return this;
    }-*/;

    public final native boolean hasUris() /*-{
      return this.hasOwnProperty("uris");
    }-*/;

    @Override
    public final native int getCpuCores() /*-{
      return this["cpuCores"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setCpuCores(int cpuCores) /*-{
      this["cpuCores"] = cpuCores;
      return this;
    }-*/;

    public final native boolean hasCpuCores() /*-{
      return this.hasOwnProperty("cpuCores");
    }-*/;

    @Override
    public final native java.lang.String getUptime() /*-{
      return this["uptime"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setUptime(java.lang.String uptime) /*-{
      this["uptime"] = uptime;
      return this;
    }-*/;

    public final native boolean hasUptime() /*-{
      return this.hasOwnProperty("uptime");
    }-*/;

    @Override
    public final native int getMem() /*-{
      return this["mem"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setMem(int mem) /*-{
      this["mem"] = mem;
      return this;
    }-*/;

    public final native boolean hasMem() /*-{
      return this.hasOwnProperty("mem");
    }-*/;

    @Override
    public final native double getCpu() /*-{
      return this["cpu"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setCpu(double cpu) /*-{
      this["cpu"] = cpu;
      return this;
    }-*/;

    public final native boolean hasCpu() /*-{
      return this.hasOwnProperty("cpu");
    }-*/;

    @Override
    public final native int getDisk() /*-{
      return this["disk"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setDisk(int disk) /*-{
      this["disk"] = disk;
      return this;
    }-*/;

    public final native boolean hasDisk() /*-{
      return this.hasOwnProperty("disk");
    }-*/;

    @Override
    public final native int getMemLimit() /*-{
      return this["memLimit"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setMemLimit(int memLimit) /*-{
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

    public final native AppfogApplicationStatisticsImpl setDiskLimit(int diskLimit) /*-{
      this["diskLimit"] = diskLimit;
      return this;
    }-*/;

    public final native boolean hasDiskLimit() /*-{
      return this.hasOwnProperty("diskLimit");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native AppfogApplicationStatisticsImpl setName(java.lang.String name) /*-{
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

    public final native AppfogApplicationStatisticsImpl setState(java.lang.String state) /*-{
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

    public final native AppfogApplicationStatisticsImpl setHost(java.lang.String host) /*-{
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

    public final native AppfogApplicationStatisticsImpl setPort(int port) /*-{
      this["port"] = port;
      return this;
    }-*/;

    public final native boolean hasPort() /*-{
      return this.hasOwnProperty("port");
    }-*/;

    public static native AppfogApplicationStatisticsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class InfraDetailImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.InfraDetail {
    protected InfraDetailImpl() {}

    @Override
    public final native java.lang.String getBase() /*-{
      return this["base"];
    }-*/;

    public final native InfraDetailImpl setBase(java.lang.String base) /*-{
      this["base"] = base;
      return this;
    }-*/;

    public final native boolean hasBase() /*-{
      return this.hasOwnProperty("base");
    }-*/;

    @Override
    public final native java.lang.String getVendor() /*-{
      return this["vendor"];
    }-*/;

    public final native InfraDetailImpl setVendor(java.lang.String vendor) /*-{
      this["vendor"] = vendor;
      return this;
    }-*/;

    public final native boolean hasVendor() /*-{
      return this.hasOwnProperty("vendor");
    }-*/;

    @Override
    public final native java.lang.String getInfra() /*-{
      return this["infra"];
    }-*/;

    public final native InfraDetailImpl setInfra(java.lang.String infra) /*-{
      this["infra"] = infra;
      return this;
    }-*/;

    public final native boolean hasInfra() /*-{
      return this.hasOwnProperty("infra");
    }-*/;

    @Override
    public final native java.lang.String getLocality() /*-{
      return this["locality"];
    }-*/;

    public final native InfraDetailImpl setLocality(java.lang.String locality) /*-{
      this["locality"] = locality;
      return this;
    }-*/;

    public final native boolean hasLocality() /*-{
      return this.hasOwnProperty("locality");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native InfraDetailImpl setName(java.lang.String name) /*-{
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

    public final native InfraDetailImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    public static native InfraDetailImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class AppfogServicesImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.AppfogServices {
    protected AppfogServicesImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService> getAppfogProvisionedService() /*-{
      return this["appfogProvisionedService"];
    }-*/;

    public final native AppfogServicesImpl setAppfogProvisionedService(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService> appfogProvisionedService) /*-{
      this["appfogProvisionedService"] = appfogProvisionedService;
      return this;
    }-*/;

    public final native boolean hasAppfogProvisionedService() /*-{
      return this.hasOwnProperty("appfogProvisionedService");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.appfog.shared.AppfogSystemService> getAppfogSystemService() /*-{
      return this["appfogSystemService"];
    }-*/;

    public final native AppfogServicesImpl setAppfogSystemService(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.appfog.shared.AppfogSystemService> appfogSystemService) /*-{
      this["appfogSystemService"] = appfogSystemService;
      return this;
    }-*/;

    public final native boolean hasAppfogSystemService() /*-{
      return this.hasOwnProperty("appfogSystemService");
    }-*/;

    public static native AppfogServicesImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class InstanceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.Instance {
    protected InstanceImpl() {}

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

    public static native InstanceImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class AppfogApplicationImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.appfog.shared.AppfogApplication {
    protected AppfogApplicationImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getUris() /*-{
      return this["uris"];
    }-*/;

    public final native AppfogApplicationImpl setUris(com.codenvy.ide.json.JsonArray<java.lang.String> uris) /*-{
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

    public final native AppfogApplicationImpl setInstances(int instances) /*-{
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

    public final native AppfogApplicationImpl setRunningInstances(int runningInstances) /*-{
      this["runningInstances"] = runningInstances;
      return this;
    }-*/;

    public final native boolean hasRunningInstances() /*-{
      return this.hasOwnProperty("runningInstances");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.Staging getStaging() /*-{
      return this["staging"];
    }-*/;

    public final native AppfogApplicationImpl setStaging(com.codenvy.ide.ext.appfog.shared.Staging staging) /*-{
      this["staging"] = staging;
      return this;
    }-*/;

    public final native boolean hasStaging() /*-{
      return this.hasOwnProperty("staging");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.ApplicationMetaInfo getMeta() /*-{
      return this["meta"];
    }-*/;

    public final native AppfogApplicationImpl setMeta(com.codenvy.ide.ext.appfog.shared.ApplicationMetaInfo meta) /*-{
      this["meta"] = meta;
      return this;
    }-*/;

    public final native boolean hasMeta() /*-{
      return this.hasOwnProperty("meta");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.Infra getInfra() /*-{
      return this["infra"];
    }-*/;

    public final native AppfogApplicationImpl setInfra(com.codenvy.ide.ext.appfog.shared.Infra infra) /*-{
      this["infra"] = infra;
      return this;
    }-*/;

    public final native boolean hasInfra() /*-{
      return this.hasOwnProperty("infra");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native AppfogApplicationImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.appfog.shared.AppfogApplicationResources getResources() /*-{
      return this["resources"];
    }-*/;

    public final native AppfogApplicationImpl setResources(com.codenvy.ide.ext.appfog.shared.AppfogApplicationResources resources) /*-{
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

    public final native AppfogApplicationImpl setDebug(java.lang.String debug) /*-{
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

    public final native AppfogApplicationImpl setState(java.lang.String state) /*-{
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

    public final native AppfogApplicationImpl setVersion(java.lang.String version) /*-{
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

    public final native AppfogApplicationImpl setServices(com.codenvy.ide.json.JsonArray<java.lang.String> services) /*-{
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

    public final native AppfogApplicationImpl setEnv(com.codenvy.ide.json.JsonArray<java.lang.String> env) /*-{
      this["env"] = env;
      return this;
    }-*/;

    public final native boolean hasEnv() /*-{
      return this.hasOwnProperty("env");
    }-*/;

    public static native AppfogApplicationImpl make() /*-{
      return {

      };
    }-*/;  }

}