# Codenvy IDE 3.0

### How to run Codenvy IDE 3 demo application:

1) Build all IDE 3 modules from the root of the project:
```mvn clean install```

2) Go to **codenvy-packaging-standalone-tomcat/target/tomcat-ide** and start Tomcat

3) Open in browser **http://localhost:8080**

### Running in DevMode (with Intellij IDEA):

1) Build all IDE 3 modules from the root of the project:
```mvn clean install```

2) Go to **codenvy-packaging-standalone-tomcat/target/tomcat-ide** and start Tomcat

3) In Inteliji Idea open **Run Configuration -> GWT Configuration**

4) In pop-up select:

      Module: **codenvy-ide-client**
      
      GWT Module Onload: **com.codenvy.ide.client**
      
      VM Options: **-Xmx1024m**
      
      Dev Mode Parameters:  **-noserver -port 8080**
      
      Start Page: **ide/default**

Open in browser * http://127.0.0.1:8080/ide/default?gwt.codesvr=127.0.0.1:9997

### Running in DevMode (with Eclipse and GWT plugins):

1) Build all IDE 3 modules from the root of the project:
```mvn clean install```

2) Go to **codenvy-packaging-standalone-tomcat/target/tomcat-ide** and start Tomcat

3) In Eclipse, right click on **codenvy-application-war** project, **Properties -> Java Build Path -> codenvy-application-war/src/main/reources -> Excluded -> Remove**.

4) Right click on **codenvy-application-war** project and select **Debug as -> Debug Configurations**

5) In **Web Application**, Create a new configuration:

      **Server** tab: untick **Run built-in server**

      **GWT** tab:
      
          URL: **ide/default**, 
          Available modules: keep only **IDE - com.codenvy.ide**

      **Arguments** tab:

          Program arguments: add **-port 8080**
          VM arguments: **-Xmx1024m -XX:MaxPermSize=1024m**


6) Click **Debug** and Open in browser http://127.0.0.1:8080/ide/default?gwt.codesvr=127.0.0.1:9997

