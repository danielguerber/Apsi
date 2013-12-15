package ch.fhnw.guerbereggenschwiler.apsi.lab2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.guerbereggenschwiler.apsi.lab2.model.Company;
import ch.fhnw.guerbereggenschwiler.apsi.lab2.model.Utils;

public class Controller {

	private static String REGISTER = "register.jsp";
	private static String SUCCESS = "success.jsp";
	private static String LOGIN = "login.jsp";
	private static String INDEX = "index.jsp";
	private static String OVERVIEW = "overview.jsp";

	public void indexPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(INDEX).forward(request, response);
	}

	public void overviewPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("Login");
		} else {
			List<String> messages = new ArrayList<>();
			request.setAttribute("messages", messages);
			//TODO: Quote
			request.setAttribute("quote","Quotes are useless");
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		}
	}
	
	public void doChange(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("Login");
		} else {
			List<String> messages = new ArrayList<>();
			
			//TODO: Quote
			request.setAttribute("quote","Quotes are useless");
			
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
	
	public void registerPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.setAttribute("firma", "");
		request.setAttribute("address", "");
		request.setAttribute("plz", "");
		request.setAttribute("town", "");
		request.setAttribute("mail", "");
		request.getRequestDispatcher(REGISTER).forward(request, response);
	}

	public void doRegister(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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
		
		//TODO: generate username / password/ make overload
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

	public void loginPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.setAttribute("username", "");
		request.getRequestDispatcher(LOGIN).forward(request, response);
	}
	
	public void doLogin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
			
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
