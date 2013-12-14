package ch.fhnw.guerbereggenschwiler.apsi.lab2.servlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.guerbereggenschwiler.apsi.lab2.Controller;

/**
 * Servlet implementation class RattleBits
 */
@WebServlet("/RattleBits/Register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Controller controller;

	/**
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() throws SQLException, ClassNotFoundException {
		super();
		Class.forName("com.mysql.jdbc.Driver");
		controller = new Controller(
				DriverManager
						.getConnection("jdbc:mysql://localhost/apsi_lab?user=root"));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
			controller.registerPage(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

			controller.doRegister(request, response);

	}

}
