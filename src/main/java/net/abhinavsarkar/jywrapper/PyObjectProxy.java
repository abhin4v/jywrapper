/**
 *
 */
package net.abhinavsarkar.jywrapper;

import static net.abhinavsarkar.jywrapper.Messages._;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;

import net.abhinavsarkar.jywrapper.annotation.PyAttribute;
import net.abhinavsarkar.jywrapper.annotation.PyMethod;
import net.abhinavsarkar.jywrapper.exception.PythonImportInstantiationError;

import org.python.core.Py;
import org.python.core.PyClass;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyModule;
import org.python.core.PyObject;
import org.python.core.PyProperty;
import org.python.core.PyProxy;
import org.python.core.PyType;

final class PyObjectProxy implements InvocationHandler {

	private static final String JAVA_COMPARE_METHOD_NAME = "compareTo";  //$NON-NLS-1$

	private static final String INIT_METHOD_NAME = "initialize";  //$NON-NLS-1$

	private static final String GETTER_METHOD_PREFIX = "get";  //$NON-NLS-1$

	private static final String SETTER_METHOD_PREFIX = "set";  //$NON-NLS-1$

	private static final String CONST_METHOD_PREFIX = "const";  //$NON-NLS-1$

	private static final EnumSet<MemberType> NON_ANNOTATED_MEMBERS =
		EnumSet.of(MemberType.COMPARE, MemberType.SPECIAL, MemberType.PYPROXY_INTERFACE);

	private static enum JavaSpecialMethod {
		equals("__eq__"), hashCode("__hash__"), toString("__str__");	//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		private final String pyEquiv;

		private JavaSpecialMethod(final String pyEquiv) {
			this.pyEquiv = pyEquiv;
		}

		public static Object invoke(final PyObject pyObject, final Method method,
				final Object[] args) throws NoSuchMethodException {
			try {
				return invokePyMethod(pyObject, method.getReturnType(),
						JavaSpecialMethod.valueOf(method.getName()).pyEquiv, args);
			} catch (final IllegalArgumentException ex) {
				throw new NoSuchMethodException();
			}
		}
	}

	private static enum NumericMethod {
		add					("__add__"),   //$NON-NLS-1$
		subtract			("__sub__"),   //$NON-NLS-1$
		multiply			("__mul__"),   //$NON-NLS-1$
		divide				("__div__"),   //$NON-NLS-1$
		divideAndRemainder	("__divmod__"),   //$NON-NLS-1$
		remainder			("__mod__"),   //$NON-NLS-1$
		pow					("__pow__"),   //$NON-NLS-1$
		negate				("__neg__"),   //$NON-NLS-1$
		plus				("__pos__"),   //$NON-NLS-1$
		abs					("__abs__"),  //$NON-NLS-1$
		and					("__and__"),   //$NON-NLS-1$
		or					("__or__"),   //$NON-NLS-1$
		xor					("__xor__"),   //$NON-NLS-1$
		invert				("__invert__"),  //$NON-NLS-1$
		intValue			("__int__"),   //$NON-NLS-1$
		longValue			("__long__"),   //$NON-NLS-1$
		floatValue			("__float__");  //$NON-NLS-1$

		private final String pyEquiv;

		private NumericMethod(final String pyEquiv) {
			this.pyEquiv = pyEquiv;
		}

		public static Object invoke(final PyObject pyObject, final Method method,
				final Object[] args) throws NoSuchMethodException {
			try {
				return findAndInvokePyMethod(
						pyObject, method.getReturnType(),
						NumericMethod.valueOf(method.getName()).pyEquiv, args);
			} catch (final IllegalArgumentException ex) {
				throw new NoSuchMethodException();
			}
		}
	}

	private static enum PyProxyInterfaceMethod {
		_setPyInstance, _setPySystemState, __initProxy__, _getPySystemState,
		_getPyInstance {
			@Override
			public Object invoke(final PyObject pyObject) {
				return new Throwable().getStackTrace()[4].getClassName()
						.startsWith("org.python") ? pyObject : null;
			}
		};

		public Object invoke(final PyObject pyObject) {
			return null;
		}
	}

	static enum MemberType {
		INIT {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				final PyMethod annotation = method.getAnnotation(PyMethod.class);

				if (annotation != null && annotation.type() == PyMethodType.INIT) {
					synchronized (pyObject) {
						return initialize(pyObject, pyImportName, javaClass, args);
					}
				}

				final String methodName = method.getName();
				if (INIT_METHOD_NAME.equals(methodName)) {
					synchronized (pyObject) {
						return initialize(pyObject, pyImportName, javaClass, args);
					}
				}

				throw new NoSuchMethodException(
						_("JyWrapper.15", "initialization method",  //$NON-NLS-1$ //$NON-NLS-2$
								methodName, pyImportName));
			}
		},
		CALL {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				if (javaClass.equals(PyCallable.class)
						&& pyObject instanceof PyFunction
						&& "call".equals(method.getName())) {  //$NON-NLS-1$
					return invokePyMethod(pyObject, method.getReturnType(),
							"__call__", (Object[]) args[0]);  //$NON-NLS-1$
				}
				throw new NoSuchMethodException();
			}
		},
		DIRECT {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				String methodName = method.getName();

				final PyMethod annotation = method.getAnnotation(PyMethod.class);
				if (annotation != null && annotation.type() == PyMethodType.DIRECT) {
					final String name = annotation.method();
					if (!"".equals(name)) {  //$NON-NLS-1$
						methodName = name;
					}
				}
				try {
					return findAndInvokePyMethod(
							pyObject, method.getReturnType(), methodName, args);
				} catch (final NoSuchMethodException e) {
					throw new NoSuchMethodException(
							_("JyWrapper.15", "direct method",  //$NON-NLS-1$ //$NON-NLS-2$
									methodName, pyImportName));
				}
			}
		},
		UNDERSCORED {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				try {
					return findAndInvokePyMethod(
							pyObject, method.getReturnType(),
							Util.camelCase2UnderScore(method.getName()), args);
				} catch (final NoSuchMethodException e) {
					throw new NoSuchMethodException(
							_("JyWrapper.15", "underscored method",  //$NON-NLS-1$ //$NON-NLS-2$
									method.getName(), pyImportName));
				}
			}
		},
		COMPARE {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				for (final RichComparisonOperator compOp : RichComparisonOperator.values()) {
					try {
						if ((Boolean) findAndInvokePyMethod(
								pyObject, Boolean.class, compOp.name(), args)) {
							return compOp.returnValue();
						}
					} catch (final PyException e) {
						if (e.type.equals(Py.NotImplementedError)) {
							// the comparison operator is not implemented. move on.
							continue;
						}
						throw e;
					}
				}

				try {
					return findAndInvokePyMethod(
							pyObject, Integer.class, "__cmp__", args);  //$NON-NLS-1$
				} catch (final PyException e) {
					if (!e.type.equals(Py.NotImplementedError)) {
						throw e;
					}
				}
				throw new NoSuchMethodException(_("JyWrapper.11"));  //$NON-NLS-1$
			}
		},
		SPECIAL {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				return JavaSpecialMethod.invoke(pyObject, method, args);
			}
		},
		PYPROXY_INTERFACE {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				try {
					return PyProxyInterfaceMethod.valueOf(method.getName())
							.invoke(pyObject);
				} catch (final IllegalArgumentException ex) {
					throw new NoSuchMethodException();
				}
			}
		},
		GETTER {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				final PyAttribute annotation = method.getAnnotation(PyAttribute.class);
				final String methodName = method.getName();
				PyObject attrValue = null;
				String attrName = methodName;

				if (annotation != null && annotation.type() == PyAttributeType.GETTER) {
					attrName = annotation.attribute();
					if (!"".equals(attrName)) {  //$NON-NLS-1$
						synchronized (pyObject) {
							attrValue = pyObject.__findattr__(attrName);
						}
					}
				}

				if (attrValue == null && methodName.startsWith(GETTER_METHOD_PREFIX)) {
					attrName = Util.camelCase2UnderScore(methodName)
						.substring(GETTER_METHOD_PREFIX.length() + 1);
					synchronized (pyObject) {
						attrValue = pyObject.__findattr__(attrName);
					}
				}

				if (attrValue != null) {
					if (PyProperty.class.isAssignableFrom(attrValue.getClass())) {
						throw new IllegalArgumentException(_("JyWrapper.12", attrName));  //$NON-NLS-1$
					}
					return Util.py2Java(attrValue, method.getReturnType());
				}

				throw new NoSuchMethodException(
						_("JyWrapper.15", "instance attribute",  //$NON-NLS-1$ //$NON-NLS-2$
								methodName, pyImportName));
			}
		},
		SETTER {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException, IllegalAccessException {
				final String methodName = method.getName();
				final PyAttribute annotation = method.getAnnotation(PyAttribute.class);
				String attrName = null;

				if (annotation != null && annotation.type() == PyAttributeType.SETTER) {
					attrName = annotation.attribute();
					if ("".equals(attrName)) {  //$NON-NLS-1$
						attrName = null;
					}
				}

				if (attrName == null
						&& methodName.startsWith(SETTER_METHOD_PREFIX)
						&& args.length == 1) {
					attrName = Util.camelCase2UnderScore(methodName)
						.substring(SETTER_METHOD_PREFIX.length() + 1);
				}

				if (attrName != null) {
					PyObject attrValue;
					synchronized (pyObject) {
						attrValue = pyObject.__findattr__(attrName);
					}
					if (attrValue != null) {
						if (PyProperty.class.isAssignableFrom(attrValue.getClass())) {
							throw new IllegalArgumentException(
									_("JyWrapper.14", attrName));  //$NON-NLS-1$
						}
					}
					try {
						final PyObject pyArgs = Py.java2py(args[0]);
						synchronized (pyObject) {
							pyObject.__setattr__(attrName, pyArgs);
						}
						return Void.TYPE;
					} catch (final PyException e) {
						if (e.type.equals(Py.AttributeError)
								&& e.value.toString().equals( "can't set attribute")) {  //$NON-NLS-1$
							throw new IllegalAccessException(
									_("JyWrapper.13", attrName));  //$NON-NLS-1$
						}
						throw e;
					}
				}

				throw new NoSuchMethodException(
						_("JyWrapper.15", "instance attribute",  //$NON-NLS-1$ //$NON-NLS-2$
								methodName, pyImportName));
			}
		},
		CONST {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				final PyAttribute annotation = method.getAnnotation(PyAttribute.class);
				final String methodName = method.getName();
				PyObject attrValue = null;

				if (annotation != null
						&& annotation.type() == PyAttributeType.CONST) {
					final String attrName = annotation.attribute();
					if (!"".equals(attrName)) {  //$NON-NLS-1$
						synchronized (pyObject) {
							attrValue = pyObject.__findattr__(attrName);
						}
					} else {
						synchronized (pyObject) {
							attrValue = pyObject.__findattr__(methodName);
						}
					}
				}

				if (attrValue == null
						&& methodName.startsWith(CONST_METHOD_PREFIX)) {
					final String attrName = methodName
							.substring(CONST_METHOD_PREFIX.length());
					synchronized (pyObject) {
						attrValue = pyObject.__findattr__(attrName);
					}
				}

				if (attrValue != null) {
					return Util.py2Java(attrValue, method.getReturnType());
				}

				throw new NoSuchMethodException(
						_("JyWrapper.15", "constant",  //$NON-NLS-1$ //$NON-NLS-2$
								methodName, pyImportName));
			}
		},
		NUMERIC {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				try {
					return NumericMethod.invoke(pyObject, method, args);
				} catch (final NoSuchMethodException e) {
					throw new NoSuchMethodException(
							_("JyWrapper.15", "eqivalent numeric method",  //$NON-NLS-1$ //$NON-NLS-2$
									method.getName(), pyImportName));
				}
			}
		},
		NO_SUCH_METHOD {
			@Override
			public Object invoke(final PyObject pyObject, final Class<?> javaClass,
					final Method method, final Object[] args, final String pyImportName)
				throws NoSuchMethodException {
				throw new NoSuchMethodException(
						_("JyWrapper.15", "method", method.getName(), pyImportName));   //$NON-NLS-1$ //$NON-NLS-2$
			}
		};

		public abstract Object invoke(PyObject pyObject, Class<?> javaClass,
				Method method, Object[] args, String pyImportName)
			throws NoSuchMethodException, IllegalAccessException;
	}

	private static enum RichComparisonOperator {
		__eq__(0), __lt__(-1), __gt__(1);

		private int returnValue;

		public int returnValue() { return returnValue; }

		private RichComparisonOperator(final int returnValue) {
			this.returnValue = returnValue;
		}
	}

	private final PyObject pyObject;

	private final Class<?> javaClass;

	private final String pyImportName;

	private static final ConcurrentHashMap<Method, MemberType> METHOD_JUMP_TABLE =
		new ConcurrentHashMap<Method, MemberType>();

	private PyObjectProxy(final PyObject pyObject, final Class<?> javaClass) {
		this.pyObject = pyObject;
		this.javaClass = javaClass;
		this.pyImportName = Util.getPyImportName(this.pyObject);
	}

	public static <T> T newInstance(final PyObject pyObject,
			final Class<T> javaClass) {
		if (javaClass.isInterface()) {
			ClassLoader classLoader = javaClass.getClassLoader();
			if (classLoader == null) {
				classLoader = ClassLoader.getSystemClassLoader();
			}

			@SuppressWarnings("unchecked")
			final T newProxyInstance = (T) Proxy.newProxyInstance(
					classLoader,
					new Class[] { javaClass, PyProxy.class },
					new PyObjectProxy(pyObject, javaClass));
			return newProxyInstance;
		} else {
			throw new IllegalArgumentException(_("JyWrapper.8", javaClass.getName())); //$NON-NLS-1$
		}
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args)
			throws NoSuchMethodException, IllegalAccessException {
		final PyMethod pyMethodAnnotation = method.getAnnotation(PyMethod.class);
		if (pyMethodAnnotation != null) {
			return pyMethodAnnotation.type().getMemberType()
				.invoke(pyObject, javaClass, method, args, pyImportName);
		}

		final PyAttribute pyAttributeAnnotation = method.getAnnotation(PyAttribute.class);
		if (pyAttributeAnnotation != null) {
			return pyAttributeAnnotation.type().getMemberType()
				.invoke(pyObject, javaClass, method, args, pyImportName);
		}

		final String methodName = method.getName();
		if (!METHOD_JUMP_TABLE.containsKey(method)) {
			//check if the method is "compareTo"
			if (Comparable.class.isAssignableFrom(javaClass)
					&& JAVA_COMPARE_METHOD_NAME.equals(methodName)) {
				METHOD_JUMP_TABLE.putIfAbsent(method, MemberType.COMPARE);
			} else {
				//check if the method is one of the special methods
				try {
					JavaSpecialMethod.valueOf(methodName);
					METHOD_JUMP_TABLE.putIfAbsent(method, MemberType.SPECIAL);
				} catch (final IllegalArgumentException ex) {
					//the method is not one of the special methods. move on.

					//check if the method is one of the PyProxy interface methods
					try {
						PyProxyInterfaceMethod.valueOf(methodName);
						METHOD_JUMP_TABLE.putIfAbsent(method, MemberType.PYPROXY_INTERFACE);
					} catch (final IllegalArgumentException e) {
						//the method is not one of the PyProxy interface methods.
						//move on.
					}
				}
			}
		}

		//check if the method has already been registered in the jump table
		//if so invoke it using the method type registered in the table
		if (METHOD_JUMP_TABLE.containsKey(method)) {
			return METHOD_JUMP_TABLE.get(method)
					.invoke(pyObject, javaClass, method, args, pyImportName);
		}

		// try each method types one by one to guess the correct one
		for (final MemberType methodType : MemberType.values()) {
			if (NON_ANNOTATED_MEMBERS.contains(methodType)) {
				continue;
			}

			try {
				final Object retVal = methodType.invoke(
						pyObject, javaClass, method, args, pyImportName);
				METHOD_JUMP_TABLE.putIfAbsent(method, methodType);
				return retVal;
			} catch (final NoSuchMethodException ex) {
				continue;
			}
		}

		//all checks failed. throw NoSuchMethodException
		METHOD_JUMP_TABLE.putIfAbsent(method, MemberType.NO_SUCH_METHOD);
		throw new NoSuchMethodException(
				_("JyWrapper.15", "method", methodName, pyImportName));   //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @param args	Arguments to initialize the underlying Python class.
	 * @param pyImport
	 * @param pyImportName
	 * @param javaClass
	 * @return	An initialized (wrapped) Python class instance, ready to be used.
	 */
	private static <T> T initialize(final PyObject pyImport, final String pyImportName,
			final Class<T> javaClass, final Object... args) {
		if (javaClass == null) {
			throw new IllegalStateException(_("JyWrapper.6", "javaClass"));   //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (pyImportName == null) {
			throw new IllegalArgumentException(_("JyWrapper.6", "pyImportName"));   //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (pyImport instanceof PyModule) {
			throw new PythonImportInstantiationError(_("JyWrapper.4", pyImportName));  //$NON-NLS-1$
		}
		if (!(pyImport instanceof PyType || pyImport instanceof PyClass)) {
			throw new PythonImportInstantiationError(_("JyWrapper.9"));  //$NON-NLS-1$
		}

		try {
			return Util.py2Java(pyImport.__call__(Util.convertArgs(args)), javaClass);
		} catch (final PyException e) {
			if (e.type.equals(Py.TypeError)) {
				throw new PythonImportInstantiationError(
						_("JyWrapper.3", pyImportName), e);  //$NON-NLS-1$
			}
			throw e;
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					_("JyWrapper.2", pyImportName, javaClass.getName()));  //$NON-NLS-1$
		}
	}

	private static Object findAndInvokePyMethod(final PyObject pyObject,
			final Class<?> javaReturnType, final String methodName, final Object[] args)
		throws NoSuchMethodException {
		PyObject pyAttr;
		synchronized (pyObject) {
			pyAttr = pyObject.__findattr__(methodName);
		}
		if (pyAttr instanceof org.python.core.PyMethod || pyAttr instanceof PyFunction) {
			return invokePyMethod(pyObject, javaReturnType, methodName, args);
		} else {
			throw new NoSuchMethodException();
		}
	}

	private static Object invokePyMethod(final PyObject pyObject,
			final Class<?> javaReturnType, final String methodName, final Object[] args) {
		PyObject pyReturnValue;
		try {
			final PyObject[] pyArgs = Util.convertArgs(args);
			synchronized (pyObject) {
				pyReturnValue = pyObject.invoke(methodName, pyArgs);
			}
		} catch (final PyException e) {
			if (e.type.equals(Py.TypeError)) {
				throw new IllegalArgumentException(
						_("JyWrapper.10", methodName), e);  //$NON-NLS-1$
			}
			throw e;
		}

		if (pyReturnValue == Py.None) {
			return "void".equals(javaReturnType.getName()) ? Void.TYPE : null; //$NON-NLS-1$
		}
		return Util.py2Java(pyReturnValue, javaReturnType);
	}

}