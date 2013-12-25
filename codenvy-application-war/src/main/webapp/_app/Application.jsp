<%--
  ~ CODENVY CONFIDENTIAL
  ~ __________________
  ~
  ~ [2012] - [2013] Codenvy, S.A.
  ~ All Rights Reserved.
  ~
  ~ NOTICE:  All information contained herein is, and remains
  ~ the property of Codenvy S.A. and its suppliers,
  ~ if any.  The intellectual and technical concepts contained
  ~ herein are proprietary to Codenvy S.A.
  ~ and its suppliers and may be covered by U.S. and Foreign Patents,
  ~ patents in process, and are protected by trade secret or copyright law.
  ~ Dissemination of this information or reproduction of this material
  ~ is strictly forbidden unless prior written permission is obtained
  ~ from Codenvy S.A..
  --%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>IDE</title>

    <%!
        public String genIdeStaticResourceUrl(HttpServletRequest request, String name) {
            return request.getContextPath() + "/_app/" + name;
        }
    %>

    <script type="text/javascript" language="javascript">
        var appConfig = {
            "context": "/ide/rest/",
            "websocketContext": "/ide/websocket/"
        }
        var hiddenFiles = ".*";
        var ws = "dev-monit";
        var project = <%= request.getAttribute("project") != null ? "\"" + request.getAttribute("project")  + "\"" : null%>;
        var path = <%= request.getAttribute("path") != null ? "\"" + request.getAttribute("path")  + "\"" : null%>;
    </script>

    <link rel="shortcut icon" href="ide/_app/favicon.ico"/>
</head>

<body>

<script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "browserNotSupported.js")%>'></script>
<script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "_app.nocache.js")%>'></script>
<script type="text/javascript">
    var uvOptions = {};
    (function () {
        var uv = document.createElement('script');
        uv.type = 'text/javascript';
        uv.async = true;
        uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/jWE2fqGrmh1pa5tszJtZQA.js';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(uv, s);
    })();
</script>

</body>

</html>