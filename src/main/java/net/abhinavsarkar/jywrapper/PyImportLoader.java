package net.abhinavsarkar.jywrapper;

import static net.abhinavsarkar.jywrapper.Messages._;

import java.util.concurrent.ConcurrentHashMap;

import net.abhinavsarkar.jywrapper.exception.PythonImportNotFoundException;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PySystemState;

/**
 * @author AbhinavSarkar
 *
 */
public final class PyImportLoader {
	
	private static final PyObject importer = new PySystemState().getBuiltins()
		.__getitem__(Py.newString("__import__")); //$NON-NLS-1$

   private static final ConcurrentHashMap<String, PyObject> loadedPyImports = 
		new ConcurrentHashMap<String, PyObject>();

   private PyImportLoader() {
	}
   
   /**
     * @param fullImportName
     * @return
     * @throws PythonImportNotFoundException
     */
    public static PyObject loadPyImport(String fullImportName)
            throws PythonImportNotFoundException {
    	if (!loadedPyImports.containsKey(fullImportName)) {
    		int i = fullImportName.lastIndexOf('.');
            String errorMsg = _("PyImportLoader.1", fullImportName); //$NON-NLS-1$
            PyObject pyImport;
            if (i == -1) {
                String pyModuleName = fullImportName;
                try {
                	pyImport = importer.__call__(Py.newString(pyModuleName));
                } catch (PyException pye) {
                    throw new PythonImportNotFoundException(errorMsg, pye);
                }
            } else {
                String pyModuleName = fullImportName.substring(0, i);
                String pyClassName = fullImportName.substring(i + 1);

                try {
                	pyImport = importer.__call__(Py.newString(pyModuleName))
                		.__getattr__(pyClassName);
                } catch (PyException pye) {
                    throw new PythonImportNotFoundException(errorMsg, pye);
                }
            }
			loadedPyImports.putIfAbsent(fullImportName, pyImport);
    	}
		return loadedPyImports.get(fullImportName);
    }
}
