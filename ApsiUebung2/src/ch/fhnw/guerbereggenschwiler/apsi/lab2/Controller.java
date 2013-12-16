package ch.fhnw.guerbereggenschwiler.apsi.lab2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.istack.internal.NotNull;

import ch.fhnw.guerbereggenschwiler.apsi.lab2.model.Company;
import ch.fhnw.guerbereggenschwiler.apsi.lab2.model.Utils;

/**
 * @author Daniel Guerber und Stefan Eggenschwiler
 * This class handles the requests sent in from the servlets
 * performs the needed changes on the model and sends a response.
 */
public final class Controller {
	/**
	 * No instance of this class should be created.
	 */
	private Controller() {}
	
	private final static String REGISTER = "register.jsp";
	private final static String SUCCESS = "success.jsp";
	private final static String LOGIN = "login.jsp";
	private final static String INDEX = "index.jsp";
	private final static String OVERVIEW = "overview.jsp";

	/**
	 * Displays the index page of the website.
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void indexPage(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws  IOException, ServletException {
		request.getRequestDispatcher(INDEX).forward(request, response);
	}

	/**
	 * Displays the overview page of the website.
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void overviewPage(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("Login");
		} else {
			List<String> messages = new ArrayList<>();
			request.setAttribute("messages", messages);
			request.setAttribute("quote", Utils.encodeHTML(Company.getFortuneQuote()));
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		}
	}
	
	/**
	 * Performs a password change and displays the overview page
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void doChange(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("Login");
		} else {
			List<String> messages = new ArrayList<>();
			
			request.setAttribute("quote",Utils.encodeHTML(Company.getFortuneQuote()));
			
			String newPassword = request.getParameter("newpassword");
			String pwMessage = Company.validatePassword(newPassword);
			if (pwMessage != null) {
				messages.add(pwMessage);
			} else {
				try {
					if (Company.changePassword((String)request.getSession().getAttribute("username"), 
							request.getParameter("oldpassword"), 
							newPassword))
						messages.add("Password ge&auml;ndert");
					else
						messages.add("Falsches Passwort");
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					response.sendError(500);
				}
			}
				
			
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		}
	}
	
	/**
	 * Displays the register page of the website.
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void registerPage(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.setAttribute("firma", "");
		request.setAttribute("address", "");
		request.setAttribute("plz", "");
		request.setAttribute("town", "");
		request.setAttribute("mail", "");
		request.getRequestDispatcher(REGISTER).forward(request, response);
	}

	/**
	 * Performs a registration and shows a result.
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void doRegister(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("firma",  Utils.encodeHTML(request.getParameter("firma")));
		request.setAttribute("address",  Utils.encodeHTML(request.getParameter("address")));
		request.setAttribute("plz",  Utils.encodeHTML(request.getParameter("plz")));
		request.setAttribute("town", Utils.encodeHTML(request.getParameter("town")));
		request.setAttribute("mail",  Utils.encodeHTML(request.getParameter("mail")));
		
		int zip = 0;
		
		try {
			zip = Integer.parseInt(request.getParameter("plz"));
		} catch (NumberFormatException e) {
			messages.add("Ungültige PLZ");
		}
		
		Company c = new Company("",
				request.getParameter("firma"),
				request.getParameter("address"),
				zip,
				request.getParameter("town"),
				request.getParameter("mail"));
		messages.addAll(c.validate());
		if (messages.size() > 0) {
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(REGISTER).forward(request, response);
		} else {
			try {
				c.save();
				request.setAttribute("message",
						"Registrierung erfolgreich, bitte warten sie auf die Zugangsdaten per Mail");
				request.getRequestDispatcher(SUCCESS).forward(request, response);
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				response.sendError(500);
			}
			
		}
	}

	/**
	 * Displays the login page of the website.
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void loginPage(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.setAttribute("username", "");
		request.getRequestDispatcher(LOGIN).forward(request, response);
	}
	
	/**
	 * Performs a login and redirects.
	 * @param request request for the website
	 * @param response response sent to the website
	 * @throws ServletException thrown by RequestDispatcher
	 * @throws IOException thrown by RequestDispatcher
	 */
	public static void doLogin(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
			
			List<String> messages = new ArrayList<>();
			try {
				Company c = Company.checkLogin(request.getParameter("username"),
						request.getParameter("password"));
				
				if (c!=null) {
					request.getSession().setAttribute("username", c.getUsername());
			        response.sendRedirect("Overview");
				} else {
					if (messages.size() == 0) {
						messages.add("username oder passwort ist ungültig");
					}
					
					request.setAttribute("messages", messages);
					request.setAttribute("username", Utils.encodeHTML(request.getParameter("username")));
					request.getRequestDispatcher(LOGIN).forward(request, response);
				}
				
			} catch (SQLException e) {
				response.sendError(500);
			}
			
		}
	
		
}
