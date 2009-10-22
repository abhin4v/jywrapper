package net.abhinavsarkar.jywrapper;

/**
 * @author AbhinavSarkar
 *
 * @param <T>
 */
public interface PyCallable<T> {
	
	/**
	 * @param args
	 * @return
	 */
	public abstract T call(Object... args); 

}
