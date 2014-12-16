package com.alibaba.dubbo.monitor.simple.pages;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.container.page.Page;
import com.alibaba.dubbo.container.page.PageHandler;
import com.alibaba.dubbo.monitor.simple.RegistryContainer;
import com.alibaba.dubbo.registry.integration.RestConstants;

//@Menu(name = "公开服务", desc = "对外公开服务.", order = 2001)
public class RestServicesPageHandler implements PageHandler {

	@Override
	public Page handle(URL url) {
		// 列出该服务提供商的所有可以暴露的rest服务
		// String service = url.getParameter("service");
		// String host = url.getParameter("host");
		String application = url.getParameter("application");
		List<List<String>> rows = new ArrayList<List<String>>();
		List<URL> providers = RegistryContainer.getInstance().getProvidersByApplication(application);
		if (providers != null && providers.size() > 0) {
			for (URL u : providers) {
				// 这里row就是每个单元格
				String serviceKey = u.getParameter(Constants.INTERFACE_KEY);

				// 名称
				List<String> row = new ArrayList<String>();
				row.add("<pre><code>" + serviceKey + "</code></pre>");
				// String s = u.toFullString();
				// row.add(s.replace("&", "&amp;"));
				// row.add("<button onclick=\"if(confirm('Confirm unregister provider?')){window.location.href='unregister.html?application="
				// + application + "&provider=" + URL.encode(s) + "';}\">Unregister</button>");
				rows.add(row);
				
				// rest属性
				// 调用方法分类
				
				String[] methods = u.getParameter(RestConstants.REST_METHOD_KEY).split(",");
				String[] params = u.getParameter(RestConstants.REST_PARAMS_KEY).split(",");
				String[] results = u.getParameter(RestConstants.REST_RESULTS_KEY).split(",");
				String[] uris = u.getParameter(RestConstants.REST_URI_KEY).split(",");
				
				for (int i=0;i<methods.length;i++) {
					
					String callee = methods[i].split("::")[1] +  "  http://" + u.getHost() + ":" + u.getPort() + uris[i].split(":")[1];
					row = new ArrayList<String>();
					row.add(callee);
					rows.add(row);
					
					row = new ArrayList<String>();
					StringBuilder params_tables = new StringBuilder();
					params_tables.append("<table>");
					params_tables.append("<tr><td>参数</td><td>类别</td><td>备注</td></tr>");
//					params_tables.append("<tbody>");
					String[] _params = params[i].split(":")[1].replace('|', '/').split("/");
					for (String _param : _params) {
						String[] _value = _param.replace('.', '/').split("/");
						params_tables.append("<tr>");
						params_tables.append("<td>"+_value[1]+"</td>");
						params_tables.append("<td>"+_value[2]+"</td>");
						params_tables.append("<td>{TODO：国际化}</td>");
						params_tables.append("</tr>");
					}
//					params_tables.append("</tbody>");
					params_tables.append("</table>");
					row.add(params_tables.toString());
					rows.add(row);
					
				}
				// sample: 
			}
		}
		// if (service != null && service.length() > 0) {
		// List<List<String>> rows = new ArrayList<List<String>>();
		// List<URL> providers = RegistryContainer.getInstance().getProvidersByService(service);
		// if (providers != null && providers.size() > 0) {
		//
		//
		// }
		// RegistryContainer.getInstance().get
		// List<URL> providers = RegistryContainer.getInstance().getProvidersByService(service);
		// if (providers != null && providers.size() > 0) {
		// for (URL u : providers) {
		// List<String> row = new ArrayList<String>();
		// String s = u.toFullString();
		// row.add(s.replace("&", "&amp;"));// 显示用的数据
		return new Page("Services", "Services (" + 0 + ")", new String[] { "名称:", "内容", "操作" }, rows);
	}
}
