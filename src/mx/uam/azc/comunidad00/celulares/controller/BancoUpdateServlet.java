package mx.uam.azc.comunidad00.celulares.controller;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import mx.uam.azc.comunidad00.celulares.data.BancoDTO;

/**
 * Servlet implementation class BancoUpdateServlet
 */

/**
 * @author vekz
 *
 *         Equipo comunidad00
 */
/**
 * @author vekz
 *
 */
@WebServlet(name = "BancoUpdate", urlPatterns = { "/BancoUpdate" })
public class BancoUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public BancoUpdateServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log("Actualizando Información del Banco");

		try {
			updateBanco(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}

		String base = request.getContextPath();
		response.sendRedirect(base + "/BancoUpdateForm");

		/*
		 * RequestDispatcher dispatcher =
		 * request.getRequestDispatcher("/bancos_update_form.jsp");
		 * dispatcher.forward(request, response);
		 * 
		 * response.setContentType("text/html");
		 * 
		 * Writer writer = response.getWriter();
		 * writer.write("<html><body><p>Hola Mundo !!!</p></body></html>");
		 */
	}

	/**
	 * @param request
	 * @param response
	 * @throws NamingException
	 * @throws SQLException
	 */
	private void updateBanco(HttpServletRequest request, HttpServletResponse response)
			throws NamingException, SQLException {
		// leer parametros de forma
		BancoDTO banco = getBanco(request);
		
		// Obtener conexion
		Context context = new InitialContext();
		DataSource source = (DataSource) context.lookup("java:comp/env/jdbc/TestDS");
		Connection connection = source.getConnection();

		try {
			// Ejecutar peticion
			updateBanco(connection, banco);
		} finally {
			connection.close();
		}
	}

	
	
	/**
	 * @param request
	 * @return
	 */
	private BancoDTO getBanco(HttpServletRequest request) {
		String idBanco = request.getParameter("id");
		String nombreBanco = request.getParameter("nombre");

		BancoDTO banco = new BancoDTO();
		banco.setId(idBanco);
		banco.setNombre(nombreBanco);
		return banco;
	}

	
	/**
	 * @param connection
	 * @param banco
	 * @throws SQLException
	 */
	private void updateBanco(Connection connection, BancoDTO banco) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("UPDATE banco SET nombre_banco=? WHERE id_banco=?;");
		try {
			statement.setString(1, banco.getNombre());
			statement.setString(2, banco.getId());
			statement.executeUpdate();
		} finally {
			statement.close();
		}
	}

}
