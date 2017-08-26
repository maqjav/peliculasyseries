package es.pys.dao;

public interface IBaseDao<T> {

	/**
	 * Inserta la instancia en la base de datos
	 * @param instancia Instancia a insertar
	 */
	public abstract void persist(T instancia);

	/**
	 * Elimina la instancia de la base de datos
	 * @param instancia	Instancia a eliminar
	 */
	public abstract void remove(T instancia);

	/**
	 * Fuerza la transacción
	 */
	public abstract void flush();

	/**
	 * Limpia la cache de transacciones
	 */
	public abstract void clear();

	/**
	 * Actualiza la instancia en la base de datos
	 * @param instancia	Instancia a actualizar
	 * @return Instancia actualizada
	 */
	public abstract T merge(T instancia);

}