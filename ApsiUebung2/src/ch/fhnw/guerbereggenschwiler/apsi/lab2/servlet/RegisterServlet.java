package ch.fhnw.guerbereggenschwiler.apsi.lab2.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.guerbereggenschwiler.apsi.lab2.Controller;

import com.sun.istack.internal.NotNull;

/**
 * @author Daniel Guerber & Stefan Eggenschwiler
 * Handles requests for the register page.
 */
@WebServlet("/RattleBits/Register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Displays the register page.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {
		
			Controller.registerPage(request, response);

	}

	/**
	 * Performs the registration.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws ServletException, IOException {

			Controller.doRegister(request, response);

	}

}
