package jpm;

import java.lang.reflect.InvocationTargetException;

public class ClassExecuter {
	private final String className;
	private Class<?> clazz;
	private Object instance;

	public ClassExecuter(String className, boolean fatal) {
		this.className = className;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("Class " + className + " not found: " + e);
			clazz = null;
			if (fatal) {
				throw new RuntimeException(e);
			}
		}
	}

	public ClassExecuter construct(Object... typesAndArgs) {
		if (clazz == null) {
			return this;
		}
		if (typesAndArgs.length % 2 != 0) {
			throw new IllegalArgumentException("typesAndArgs must be in pairs of (Class, Object)");
		}
		try {
			Class<?>[] types = new Class<?>[typesAndArgs.length / 2];
			Object[] args = new Object[typesAndArgs.length / 2];
			for (int i = 0; i < typesAndArgs.length; i += 2) {
				types[i / 2] = (Class<?>) typesAndArgs[i];
				args[i / 2] = typesAndArgs[i + 1];
			}
			instance = clazz.getDeclaredConstructor(types).newInstance(args);
			return this;
		} catch (Exception e) {
			System.err.println("Error constructing class " + className);
			System.err.println();
			System.err.println("Available constructors :");
			boolean found = false;
			for (var method : clazz.getDeclaredConstructors()) {
				found = true;
				System.err.print("  " + method.getName() + "(");
					var params = method.getParameterTypes();
					for (int i = 0; i < params.length; i++) {
						System.err.print(params[i].getSimpleName());
						if (i < params.length - 1) {
							System.err.print(", ");
						}
					}
					System.err.println(")");
			}
			if (!found) {
				System.err.println("Is your constructor private? no need to call construct() if so.");
			}
			System.err.println();
			System.err.println();
			throw new IllegalArgumentException(e);
		}
	}

	public ClassExecuter overrideInstance(Object instance) {
		if (clazz == null) {
			return this;
		}
		this.instance = instance;
		return this;
	}

	public Object call(String methodName, Object... typesAndArgs) {
		if (clazz == null) {
			return null;
		}
		if (typesAndArgs.length % 2 != 0) {
			throw new IllegalArgumentException("typesAndArgs must be in pairs of (Class, Object)");
		}
		Class<?> localInstance;
		if (instance == null) {
			localInstance = clazz;
		} else {
			localInstance = instance.getClass();
		}
		try {
			Class<?>[] types = new Class<?>[typesAndArgs.length / 2];
			Object[] args = new Object[typesAndArgs.length / 2];
			for (int i = 0; i < typesAndArgs.length; i += 2) {
				types[i / 2] = (Class<?>) typesAndArgs[i];
				args[i / 2] = typesAndArgs[i + 1];
			}
			return localInstance.getDeclaredMethod(methodName, types).invoke(instance, args);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			System.err.println("Error calling method " + methodName + " on class " + className);
			System.err.println();
			System.err.println("Available methods for " + methodName);
			boolean found = false;
			for (var method : localInstance.getDeclaredMethods()) {
				found = true;
				if (method.getName().equals(methodName)) {
					System.err.print("  " + method.getName() + "(");
					var params = method.getParameterTypes();
					for (int i = 0; i < params.length; i++) {
						System.err.print(params[i].getSimpleName());
						if (i < params.length - 1) {
							System.err.print(", ");
						}
					}
					System.err.println(")");
				}
			}
			if (!found) {
				System.err.println("  No methods found with the name " + methodName);
			}
			System.err.println();
			System.err.println();
			throw new IllegalArgumentException(e);
		}
	}

	public ClassExecuter factory(String methodName, Object... typesAndArgs) {
		if (clazz == null) {
			return this;
		}
		if (typesAndArgs.length % 2 != 0) {
			throw new IllegalArgumentException("typesAndArgs must be in pairs of (Class, Object)");
		}
		try {
			Class<?>[] types = new Class<?>[typesAndArgs.length / 2];
			Object[] args = new Object[typesAndArgs.length / 2];
			for (int i = 0; i < typesAndArgs.length; i += 2) {
				types[i / 2] = (Class<?>) typesAndArgs[i];
				args[i / 2] = typesAndArgs[i + 1];
			}
			instance = clazz.getDeclaredMethod(methodName, types).invoke(instance, args);
			return this;
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			System.err.println("Error calling method " + methodName + " on class " + className);
			System.err.println();
			System.err.println("Available methods for '" + methodName+"'");
			boolean found = false;
			for (var method : clazz.getDeclaredMethods()) {
				found = true;
				if (method.getName().equals(methodName)) {
					System.err.print("  " + method.getName() + "(");
					var params = method.getParameterTypes();
					for (int i = 0; i < params.length; i++) {
						System.err.print(params[i].getSimpleName());
						if (i < params.length - 1) {
							System.err.print(", ");
						}
					}
					System.err.println(")");
				}
			}
			if (!found) {
				System.err.println("  No methods found with the name " + methodName);
			}
			System.err.println("\n");
			throw new IllegalArgumentException(e);
		}
	}

	public ClassExecuter build(String methodName, Object... typesAndArgs) {
		if (clazz == null) {
			return this;
		}
		call(methodName, typesAndArgs);
		return this;
	}
}