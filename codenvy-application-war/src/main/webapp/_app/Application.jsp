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
    <title>IDE</title>

     <%!
      public String genIdeStaticResourceUrl(HttpServletRequest request, String name) {
        return request.getContextPath() + "/" + request.getAttribute("ws") + "/_app/" + name;
      }
     %>

    <script type="text/javascript" language="javascript">
	var ide_base_path = '<%=request.getContextPath() + "/" + request.getAttribute("ws") + "/_app/"%>';

        var appConfig = {
            "context": "/ide/rest/",
            "websocketContext": "/ide/websocket/"
        };

        var hiddenFiles = ".*";
        var ws = "<%= request.getAttribute("ws")%>";
        var project = <%= request.getAttribute("project") != null ? "\"" + request.getAttribute("project")  + "\"" : null%>;
        var path = <%= request.getAttribute("path") != null ? "\"" + request.getAttribute("path")  + "\"" : null%>;
        var startUpParams = <%= request.getAttribute("startUpParams") != null ? "\"?" + request.getAttribute("startUpParams")  + "\"" : null%>;
        var authorizationContext = "/rest";
        var authorizationErrorPageURL = "/ide/ide/error_oauth.html";
        var securityCheckURL = "/ide/j_security_check";
        var isTargetWindow = false;
        var uuid;
        var buildMenu = function () {
        };

        window.onload = function () {
            buildMenu();
            uuid = generate();
            sendSessionStatus("ide", uuid, "start");
        }

        window.onbeforeunload = function () {
            sendSessionStatus("ide", uuid, "stop");
        }

        window.onfocus = function () {
            if (isTargetWindow == true) {
                isTargetWindow = false;
                uuid = generate();
                sendSessionStatus("ide", uuid, "start");
            }
            return false;
        }

        window.onblur = function () {
            if (isTargetWindow == false) {
                isTargetWindow = true;
                sendSessionStatus("ide", uuid, "stop");
            }
            return false;
        }
    </script>

    <link rel="shortcut icon" href="/images/favicon.ico"/>

    <script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "greeting.js")%>'></script>

    <link type="text/css" rel="stylesheet" href='<%= genIdeStaticResourceUrl(request, "top-menu.css")%>' media="all"/>
    <link href='<%= genIdeStaticResourceUrl(request, "css/ide01.css")%>' media="screen" rel="stylesheet" type="text/css"/>
</head>

<body>

<script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "browserNotSupported.js")%>'></script>

<div id="ide-preloader" style="position: absolute; left:0px; right: 0px; top:0px; bottom: 0px; background-color: #FFFFFF; z-index: 900100;">
	<div style="position:absolute; width:230px; height:90px; background-image: url('<%= genIdeStaticResourceUrl(request, "loader-background-element.png")%>'); left:50%; top:50%; margin-left:-115px; margin-top:-45px; text-align: center;">
		<img src='<%= genIdeStaticResourceUrl(request, "ajax-loader-new.gif")%>' style="margin-top: 20px; margin-bottom: 10px;" />
		<br>
		<span style="font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 11px; text-align: center; color: #222222;">Loading IDE...</span>
	</div>
</div>

<script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "_app.nocache.js")%>'></script>

<div id="ide-menu-additions" align="right" class="ideMenuAdditions">
    <table cellspacing="0" cellpadding="0" border="0"
           class="ideMenuAdditionsTable">
        <tr id="ide-menu-additions-rows">
        </tr>
    </table>
</div>

<script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "session.js")%>'></script>
<script type="text/javascript" language="javascript" src='<%= genIdeStaticResourceUrl(request, "cloud_menu.js")%>'></script>


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

<!-- Google Analytics: -->
<script>
  var _gaq = _gaq || [];
  if (window.location.hostname == 'localhost')
    {//patch for tracking localhost in chrome
    _gaq.push(['_setDomainName', 'none']); }
    _gaq.push(
      ['_setAccount', "UA-37306001-1"], // codenvy account
      ['_trackPageview'],
      ['exo._setAccount', "UA-1292368-18"], // eXo account
      ['exo._trackPageview']
  );
   (function(d,t)
    {var g=d.createElement(t),s=d.getElementsByTagName(t)[0]; g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js'; s.parentNode.insertBefore(g,s)}
    (document,'script'));
</script>

</body>

</html>
