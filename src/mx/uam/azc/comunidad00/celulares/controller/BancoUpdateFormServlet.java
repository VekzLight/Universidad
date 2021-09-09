package mx.uam.azc.comunidad00.celulares.controller;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mx.uam.azc.comunidad00.celulares.data.BancoDTO;

/**
 * Servlet implementation class BancoUpdateFormServlet
 */




/**
 * @author vekz
 *
 */
@WebServlet(name = "BancoUpdateForm", urlPatterns = { "/BancoUpdateForm" })
public class BancoUpdateFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			List<BancoDTO> Bancos = getBanco();
			request.setAttribute("bancos", Bancos);
		} catch( Exception e ) { 
			throw new ServletException(e); 
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/bancos_update_form.jsp");
		dispatcher.forward(request, response);
	}

	private List<BancoDTO> getBanco() throws NamingException, SQLException{
		Context  context =  new InitialContext();
		DataSource source = (DataSource) context.lookup("java:comp/env/jdbc/TestDS");
		
		Connection connection =  source.getConnection();
		try {
			return getBanco(connection);
		} finally {
			connection.close();
		}
	}

	private List<BancoDTO> getBanco(Connection connection) throws SQLException{
		Statement statement = connection.createStatement();
		
		ResultSet cursor =  statement.executeQuery("SELECT id_banco, nombre_banco FROM banco;");
		
		try {
			List<BancoDTO> bancos =  new ArrayList<BancoDTO>();
			while(cursor.next()) {
				BancoDTO banco = new BancoDTO();
				
				banco.setId(cursor.getString(1));
				banco.setNombre(cursor.getString(2));
				
				bancos.add(banco);
			}
			return bancos;
		} finally {
			cursor.close();
		}
	}
	
	

}
