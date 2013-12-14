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

public class Controller {

	private static String REGISTER = "register.jsp";
	private static String SUCCESS = "success.jsp";
	private static String LOGIN = "login.jsp";
	private static String INDEX = "index.jsp";
	private static String OVERVIEW = "overview.jsp";

	private final Connection con;

	public Controller(Connection connection) {
		con = connection;
	}

	public void indexPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(INDEX).forward(request, response);
	}

	public void overviewPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO: overview / passwort change page
	}
	
	public void registerPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.getRequestDispatcher(REGISTER).forward(request, response);
	}

	public void doRegister(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		
		int zip = 0;
		
		try {
			zip = Integer.parseInt(request.getParameter("plz"));
		} catch (NumberFormatException e) {
			messages.add("Ungültige PLZ");
		}
		
		//TODO: generate username / password/ make overload
		Company c = new Company(0, "", "",
				request.getParameter("firma"),
				request.getParameter("address"),
				zip,
				request.getParameter("town"),
				request.getParameter("mail"));
		messages.addAll(c.validate());
		request.setAttribute("firma", c.getName());
		request.setAttribute("address", c.getAddress());
		request.setAttribute("plz", String.valueOf(c.getZip()));
		request.setAttribute("town", c.getTown());
		request.setAttribute("mail", c.getMail());
		if (messages.size() > 0) {
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(REGISTER).forward(request, response);
		} else {
			try {
				c.save();
			} catch (SQLException e) {
				response.sendError(500);
			}
			c.sendLoginData();
			request.setAttribute("message",
					"Registrierung erfolgreich, bitte warten sie auf die Zugangsdaten per Mail");
			request.getRequestDispatcher(SUCCESS).forward(request, response);
		}
	}

	public void loginPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		request.setAttribute("messages", messages);
		request.getRequestDispatcher(LOGIN).forward(request, response);
	}
	
	public void doLogin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

			List<String> messages = new ArrayList<>();
			try {
				Company c = Company.checkLogin(request.getParameter("user"),
						request.getParameter("password"));
				
				if (c!=null) {
					request.getSession().setAttribute("userId", c.getId());
					request.getSession().setAttribute("username", c.getUsername());
			        response.sendRedirect("../Overview");
				} else {
					if (messages.size() == 0) {
						messages.add("username oder passwort ist ungültig");
					}
					
					request.setAttribute("messages", messages);
					request.getRequestDispatcher(LOGIN).forward(request, response);
				}
				
			} catch (SQLException e) {
				response.sendError(500);
			}
			
		}
	
		
}
