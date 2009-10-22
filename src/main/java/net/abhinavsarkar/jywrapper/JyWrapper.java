package net.abhinavsarkar.jywrapper;

import static net.abhinavsarkar.jywrapper.Messages._;


import net.abhinavsarkar.jywrapper.annotation.Wraps;
import net.abhinavsarkar.jywrapper.exception.PythonImportNotFoundException;

import org.python.core.PyFunction;
import org.python.core.PyModule;
import org.python.core.PyObject;
import org.python.core.PyType;

/**
 * @author Abhinav Sarkar <abhinav@abhinavsarkar.net>
 *
 * @param <T>	The type of the java class to wrap the Python class/module with.
 */
public final class JyWrapper {
	
	private JyWrapper() {
	}

	public static <T> T wrap(final Class<T> javaClass) {
		final Wraps annotation = javaClass.getAnnotation(Wraps.class);
		if (annotation == null) {
			throw new PythonImportNotFoundException(_("JyWrapper.7", javaClass));  //$NON-NLS-1$
		}
		
		return wrap(javaClass, annotation.value());
	}

	/**
	 * @param pyImportName	The full import name of the Python class/module
	 * 		to wrap.
	 * @return	An instance of {@link UninitedPyObjectWrapper}, ready to be 
	 * 		initialized.
	 * @throws	IllegalStateException Thrown if the java Class to be used to
	 * 		wrap the Python module/class, has not been supplied by earlier 
	 * 		calling {@link JyWrapper#with(Class)}.
	 * @throws IllegalArgumentException Thrown if the pyImportName parameter
	 * 		is null.	
	 */
	public static <T> T wrap(final Class<T> javaClass, final String pyImportName) {
		if (javaClass == null) {
			throw new IllegalStateException(_("JyWrapper.6", "javaClass"));   //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (pyImportName == null) {
			throw new IllegalArgumentException(_("JyWrapper.6", "pyImportName"));   //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		final PyObject pyImport = PyImportLoader.loadPyImport(pyImportName);
		if (!(pyImport instanceof PyType || pyImport instanceof PyModule)) {
			throw new IllegalArgumentException(_("JyWrapper.5", pyImportName));  //$NON-NLS-1$
		}
		return Util.py2Java(pyImport, javaClass);
	}
	
	/**
	 * @param <T>			The return type of the {@link PyCallable} instance. 
	 * @param pyImportName	The full import name of the Python function to wrap.
	 * @param returnType	The class of the return type.
	 * @return				An instance of {@link PyCallable} which wraps the
	 * 		Python function given in parameter.
	 * @throws	IllegalArgumentException Thrown if the any of the parameters 
	 * 		supplied are null or if the pyImportName parameter supplied does not 
	 * 		correspond to a Python function.
	 */
	public static <T> PyCallable<T> wrapPyFunction(
			final String pyImportName, final Class<T> returnType) {
		if (pyImportName == null) {
			throw new IllegalArgumentException(_("JyWrapper.6", "pyImportName"));   //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (returnType == null) {
			throw new IllegalArgumentException(_("JyWrapper.6", "returnType"));   //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		final PyObject pyImport = PyImportLoader.loadPyImport(pyImportName);
		if (!(pyImport instanceof PyFunction)) {
			throw new IllegalArgumentException(_("JyWrapper.0", pyImportName));  //$NON-NLS-1$
		}
		
		@SuppressWarnings("unchecked")
		final PyCallable<T> newInstance = PyObjectProxy.newInstance(
				pyImport, PyCallable.class);
		return newInstance;
	}
	
}
