/**
 * Creado: Sep 2, 2021 4:35:34 PM
 */
package mx.uam.azc.comunidad00.celulares.data;

import java.io.Serializable;

/**
 * @author vekz
 *
 */
public class BancoDTO implements Serializable {
	private String _id;
	private String _nombre;

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getNombre() {
		return _nombre;
	}

	public void setNombre(String nombre) {
		_nombre = nombre;
	}

}
