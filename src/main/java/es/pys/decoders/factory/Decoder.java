package es.pys.decoders.factory;

import java.util.Map;

public abstract class Decoder {

	public Decoder(DecoderType tipo) {
		Decoder.tipo = tipo;
	}

	private static DecoderType tipo = null;

	public abstract Map<String, String> sacarDatos(String url) throws Exception;

	protected abstract String dameKey(String atributo);

	public static DecoderType getTipo() {
		return tipo;
	}

	/**
	 * Funcion para corregir los textos
	 * 
	 * @param texto
	 *            Texto a corregir
	 * @return Texto corregido
	 */
	protected static String corregirTextos(String texto) {
		// Corregimos caracteres latinos
		texto = texto.replace("&Aacute;", "Á");
		texto = texto.replace("&Eacute;", "É");
		texto = texto.replace("&Iacute;", "Í");
		texto = texto.replace("&Oacute;", "Ó");
		texto = texto.replace("&Uacute;", "Ú");
		texto = texto.replace("&Ntilde;", "Ñ");
		texto = texto.replace("&aacute;", "á");
		texto = texto.replace("&eacute;", "é");
		texto = texto.replace("&iacute;", "í");
		texto = texto.replace("&oacute;", "ó");
		texto = texto.replace("&uacute;", "ú");
		texto = texto.replace("&ntilde;", "ñ");
		texto = texto.replace("&Uuml;", "Ü");
		texto = texto.replace("&uuml;", "ü");
		texto = texto.replace("&quot;", "\"");
		texto = texto.replace("&amp;", "&");

		// Eliminamos espacios dobles
		texto = texto.replaceAll("\\s+", " ").trim();

		// Quitamos textos basura
		texto = eliminarBasura(texto);

		if (!texto.equals(""))
			return texto;
		else
			return "";
	}

	/**
	 * Función encargada de eliminar textos, etiquetas o cualquier otro elemento
	 * que se introduzca en cualquiera de los valores extraidos
	 * 
	 * @param texto
	 *            Texto a tratar
	 * @return Texto limpio
	 */
	protected static String eliminarBasura(String texto) {
		texto = texto.replace("&nbsp;", "");
		texto = texto.replace(" m.", "");
		texto = texto.replace(" (FILMAFFINITY)", "");
		texto = texto.replace("(FILMAFFINITY)", "");
		texto = texto.replace("Animation", "Animación");
		texto = texto.replace(" (TV)", "");
		texto = texto.replace(" min.", "");
		texto = texto.replace("min.", "");

		// Quitamos puntos del final si no termina en min.
		if (texto.endsWith(".") && !texto.endsWith("min."))
			texto = texto.substring(0, texto.length() - 1);

		return texto;
	}
}
