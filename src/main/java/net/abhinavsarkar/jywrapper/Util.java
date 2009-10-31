package net.abhinavsarkar.jywrapper;

import static net.abhinavsarkar.jywrapper.Messages._;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.python.core.Py;
import org.python.core.PyClass;
import org.python.core.PyFunction;
import org.python.core.PyModule;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyType;

final class Util {

	private Util() {
	}

	static <T> T py2Java(final PyObject pyObject, final Class<T> javaClass) {
		final Object javaWrapper = pyObject.__tojava__(javaClass);
		if (javaWrapper == Py.NoConversion) {
			if (javaClass.isInterface()) {
				return PyObjectProxy.newInstance(pyObject, javaClass);
			} else {
				throw new IllegalArgumentException(
						_("JyWrapper.1", pyObject, javaClass.getName()));  //$NON-NLS-1$
			}
		} else {
			@SuppressWarnings("unchecked")
			final T t = (T) javaWrapper;
			return t;
		}
	}

	static PyObject[] convertArgs(final Object[] args) {
		if (args == null || args.length == 0) {
			return new PyObject[0];
		}
		if (args.length == 1) {
			return new PyObject[] { Py.java2py(args[0]) };
		}

		final PyObject[] pyArgs = new PyObject[args.length];
		for (int i = args.length; --i >= 0;) {
			pyArgs[i] = Py.java2py(args[i]);
		}
		return pyArgs;
	}

	static String getPyImportName(final PyObject pyObject) {
		synchronized (pyObject) {
			if (pyObject instanceof PyType) {
				final PyType pyType = (PyType) pyObject;
				final PyObject module = pyType.getModule();
				if (module instanceof PyString
						&& !module.toString().equals("__builtin__")) {  //$NON-NLS-1$
					return String.format("%s.%s", module.toString(),   //$NON-NLS-1$
							pyType.getName());
				}
				return pyType.getName();
			} else if (pyObject instanceof PyClass) {
				final PyClass pyClass = (PyClass) pyObject;
				final PyObject mod = pyClass.__dict__.__finditem__("__module__");
				String smod;
				if (mod == null || !(mod instanceof PyString)) {
					smod = "<unknown>";
				} else {
					smod = ((PyString) mod).toString();
				}
				return smod + "." + pyClass.__name__;
			} else if (pyObject instanceof PyModule) {
				return ((PyModule) pyObject).toString();
			} else if (pyObject instanceof PyFunction) {
				final PyFunction pyFunction = (PyFunction) pyObject;
				return String.format(
						"%s.%s", pyFunction.__module__, pyFunction.__name__); //$NON-NLS-1$
			} else {
				return getPyImportName(pyObject.getType());
			}
		}
	}

	static String camelCase2UnderScore(final String word) {
		return word.replaceAll("([A-Z])", "_$1").toLowerCase();   //$NON-NLS-1$ //$NON-NLS-2$
	}

	static PyString resolvePath(final URI pythonPath) {
		PyString resolvedPath = null;
		if (pythonPath.getScheme().equals("jar")) {
			final String schemeSpecificPart = pythonPath.getSchemeSpecificPart();
			final int lastIndexOf = schemeSpecificPart.lastIndexOf("!");
			final String jarPath = schemeSpecificPart.substring(5, lastIndexOf);
			final File file = new File(jarPath);
			if (file.exists()) {
				try {
					resolvedPath = Py.newString(file.getCanonicalPath()
						+ schemeSpecificPart.substring(lastIndexOf + 1));
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else if (pythonPath.getScheme().equals("file")) {
			final File file = new File(pythonPath);
			if (file.exists()) {
				try {
					resolvedPath = Py.newString(file.getCanonicalPath());
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return resolvedPath;
	}

}
