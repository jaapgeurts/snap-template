package blog;

import snap.annotations.RouteOptions;
import snap.http.HttpMethod;
import snap.http.RequestContext;
import snap.http.RequestResult;
import snap.views.TemplateView;

public class HomeController {
	@RouteOptions(methods = { HttpMethod.GET })
	public RequestResult showHome(RequestContext context) {
		TemplateView view = new TemplateView("index.html");
		view.addParameter("greeting", "Congratulations. It's working");
		return view;
	}
}
