package ch.fhnw.guerbereggenschwiler.apsi.lab2.servlet;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.guerbereggenschwiler.apsi.lab2.Controller;

/**
 * @author Daniel Guerber & Stefan Eggenschwiler
 * Handles requests for the overview page.
 */
@WebServlet("/RattleBits/Overview")
public class OverviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Displays the overview page.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(@Nonnull HttpServletRequest request,
			@Nonnull HttpServletResponse response) throws ServletException, IOException {
		Controller.overviewPage(request, response);
	}

	/**
	 * Handles password change requests.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(@Nonnull HttpServletRequest request,
			@Nonnull HttpServletResponse response) throws ServletException, IOException {
			Controller.doChange(request, response);
			
	}

}
