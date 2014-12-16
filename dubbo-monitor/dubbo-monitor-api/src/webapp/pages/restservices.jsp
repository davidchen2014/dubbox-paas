<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.alibaba.dubbo.common.Constants" %>
<%@ page import="com.alibaba.dubbo.common.URL" %>
<%@ page import="com.alibaba.dubbo.container.page.Page" %>
<%@ page import="com.alibaba.dubbo.container.page.PageHandler" %>
<%@ page import="com.alibaba.dubbo.monitor.simple.RegistryContainer" %>
<%@ page import="com.alibaba.dubbo.registry.integration.RestConstants" %>
<%@ page import="com.alibaba.dubbo.container.page.PageServlet" %>

<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>book_v2</title> 
    <link rel="shortcut icon" href="http://www.douban.com/favicon.ico" type="image/x-icon" />
    <link rel="stylesheet" href="http://img3.douban.com/f/shire/25fe80f630af51c20b86da942fe012806db64132/css/core.css">
    <link rel="stylesheet" href="http://img3.douban.com/f/shire/86e0773ca9b58697ddaae8e7a3b80ed1f3e909db/css/core/_init_.css">
    <link rel="stylesheet" href="http://img3.douban.com/f/shire/645962960690dcfe678a90ab38dc9778a920e073/css/grids.css">
    <link rel="stylesheet" href="http://img3.douban.com/f/shire/d1c3d122a1fb3d53a67b60fb3b5026a6da752dc8/css/platform/app.css">
    <link rel="stylesheet" href="http://img3.douban.com/f/shire/55ef00473986e7ca5ef23a7941da807608548815/css/platform/markdown.css">
    <script type="text/javascript" src="http://img3.douban.com/f/shire/61dea57acc38f6e00cc187be26176dc7636ee099/js/do.js" data-cfg-corelib="http://img3.douban.com/f/shire/8033e1c2528bb6402c478e00176afb3562abbc21/js/jquery.min.js"></script>
    <script type="text/javascript" src="http://img3.douban.com/f/shire/4ff6b04bf980286a3aef551db4adbf73a937e01b/js/core/cookie.js"></script>
     
</head>
<body>

  <div class="yui3-g">
  <div id="content">


    
<div class="yui3-u-19-24">
    <div class="doc-box">
        <div class="inner">
            <div class="markdown">
                <h1><%=request.getParameter("application")%> Api V2</h1>

<p><a href="#">返回</a></p>
<%
List<List<String>> rows = new ArrayList<List<String>>();
List<URL> providers = RegistryContainer.getInstance().getProvidersByApplication(request.getParameter("application"));
if (providers != null && providers.size() > 0) {
	for (URL u : providers) {
		// 这里row就是每个单元格
		String serviceKey = u.getParameter(Constants.INTERFACE_KEY);
%>
		<h2><a id="get_isbn_book"></a> <font size=3><%=serviceKey%></font></h2>
<%	
				String[] methods = u.getParameter(RestConstants.REST_METHOD_KEY).split(",");
				String[] params = u.getParameter(RestConstants.REST_PARAMS_KEY).split(",");
				String[] results = u.getParameter(RestConstants.REST_RESULTS_KEY).split(",");
				String[] uris = u.getParameter(RestConstants.REST_URI_KEY).split(",");
				String[] status_code = u.getParameter(RestConstants.REST_STATUS_KEY).split(",");
				for (int i=0;i<methods.length;i++) {
						String callee = methods[i].split("::")[1] +  "  http://" + u.getHost() + ":" + u.getPort() + uris[i].split(":")[1];
				%>
						<pre><code><%=callee%></code></pre>
						
						返回status=<%=status_code[i]%>
						<table>
						<tbody>
							<!--
								rest.params=    sayHello:1.firstName.string,             	=== sayHello.firstName.desc
                								businessA:1.param1.string|2.param2.string === businessA.param1.desc
                																													=== businessA.param2.desc-->
						<tr><td><em>参数</em></td><td><em>意义</em></td><td><em>备注</em></td></tr>
						<%
							String[] _params = params[i].split(":")[1].replace('|', '/').split("/");
								for (String _param : _params) {
									String[] _value = _param.replace('.', '/').split("/");
									String _param_desc = params[i].split(":")[0] + "." + _value[1] + ".desc";
						%>
									
										<tr><td><%=_value[1]%></td><td><%=_value[2]%></td><td><%=PageServlet.getMessage(_param_desc)%></td></tr>
						<%
								}
						%>
						
						</tbody>
						</table>
						
				<%
				}
	}
}
%>

</body>
</html>
