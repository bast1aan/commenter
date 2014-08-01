package bast1aan.commenter;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 156398977450815869L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/redirect.jsp");
		if (rd != null) {
			request.setAttribute("var", "value");
			rd.forward(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "redirect.jsp not found");
		}
	}
}
