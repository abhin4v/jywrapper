package net.abhinavsarkar.jywrapper;

import static net.abhinavsarkar.jywrapper.Messages._;

import java.util.concurrent.ConcurrentHashMap;

import net.abhinavsarkar.jywrapper.exception.PythonImportNotFoundException;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

/**
 * @author AbhinavSarkar
 *
 */
public final class PyImportLoader {

	private static final PyObject importer =
		Py.getSystemState().getBuiltins().__getitem__(Py.newString("__import__"));

	private static final ConcurrentHashMap<String, PyObject> loadedPyImports =
		new ConcurrentHashMap<String, PyObject>();

	private PyImportLoader() {
	}

   /**
	 * @param fullImportName
	 * @return
	 * @throws PythonImportNotFoundException
	 */
	public static PyObject loadPyImport(final String fullImportName)
			throws PythonImportNotFoundException {
		if (!loadedPyImports.containsKey(fullImportName)) {
			final int i = fullImportName.lastIndexOf('.');
			final String errorMsg = _("PyImportLoader.1", fullImportName, Py.getSystemState().path); //$NON-NLS-1$
			PyObject pyImport;
			if (i == -1) {
				final String pyModuleName = fullImportName;
				try {
					pyImport = importer.__call__(Py.newString(pyModuleName));
				} catch (final PyException pye) {
					throw new PythonImportNotFoundException(errorMsg, pye);
				}
			} else {
				final String pyModuleName = fullImportName.substring(0, i);
				final String pyClassName = fullImportName.substring(i + 1);

				try {
					pyImport = importer.__call__(Py.newString(pyModuleName))
						.__getattr__(pyClassName);
				} catch (final PyException pye) {
					throw new PythonImportNotFoundException(errorMsg, pye);
				}
			}
			loadedPyImports.putIfAbsent(fullImportName, pyImport);
		}
		return loadedPyImports.get(fullImportName);
	}

}
