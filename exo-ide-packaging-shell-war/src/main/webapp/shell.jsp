<!--

    CODENVY CONFIDENTIAL
    __________________

    [2012] - [2013] Codenvy, S.A.
    All Rights Reserved.

    NOTICE:  All information contained herein is, and remains
    the property of Codenvy S.A. and its suppliers,
    if any.  The intellectual and technical concepts contained
    herein are proprietary to Codenvy S.A.
    and its suppliers and may be covered by U.S. and Foreign Patents,
    patents in process, and are protected by trade secret or copyright law.
    Dissemination of this information or reproduction of this material
    is strictly forbidden unless prior written permission is obtained
    from Codenvy S.A..

-->
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Codenvy Shell</title>

    <%!
      public String genShellStaticResourceUrl(HttpServletRequest request, String name) {
        return request.getContextPath() + "/" + request.getAttribute("wsName") + "/_app/" + name;
      }
     %>

    <script type="text/javascript" language="javascript">
        var appConfig = {
            "context": "/ide/rest/",
            "websocketContext": "/w/websocket/"
        }
        var ws = "<%= request.getAttribute("wsName")%>";
    </script>

    <script>
        var isTargetWindow = false;
        var uuid;

        window.onload = function () {
            uuid = generate();
            sendSessionStatus("shell", uuid, "start");
        }

        window.onunload = function () {
            sendSessionStatus("shell", uuid, "stop");
        }

        window.onfocus = function () {
            if (isTargetWindow == true) {
                isTargetWindow = false;
                uuid = generate();
                sendSessionStatus("shell", uuid, "start");
            }
            return false;
        }

        window.onblur = function () {
            if (isTargetWindow == false) {
                isTargetWindow = true;
                sendSessionStatus("shell", uuid, "stop");
            }
            return false;
        }
    </script>

    <link rel="shortcut icon" href='<%= genShellStaticResourceUrl(request, "favicon.ico")%>'/>
    <script type="text/javascript" language="javascript" src='<%= genShellStaticResourceUrl(request, "session.js")%>'></script>
    <script type="text/javascript" language="javascript" src='<%= genShellStaticResourceUrl(request, "Shell.nocache.js")%>'></script>
</head>

<body>

</body>

</html>
