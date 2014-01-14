


var uvOptions = {};
(function () {
    var uv = document.createElement('script');
    uv.type = 'text/javascript';
    uv.async = true;
    uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/jWE2fqGrmh1pa5tszJtZQA.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(uv, s);
})();



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
