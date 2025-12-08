package jpm;
import java.lang.reflect.InvocationTargetException;


public class ClassExecuter {
	private final String className;
	private final Class<?> clazz;
	private Object instance;

	public ClassExecuter(String className) {
		this.className = className;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class " + className + " not found: " + e);
		}
	}

	public ClassExecuter construct(Object... typesAndArgs) {
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
			System.err.println("Error constructing class " + className + ": " + e);
			System.err.println("Is your constructor private? no need to call construct() if so.");
			throw new RuntimeException(e);
		}
	}

	public ClassExecuter construct(Object instance) { this.instance = instance; return this;}

	public Object call(String methodName, Object... typesAndArgs) {
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
			System.err.println("Error calling method " + methodName + " on class " + className + ": " + e);
			throw new RuntimeException(e);
		}
	}

	public ClassExecuter factory(String methodName, Object... typesAndArgs) {
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
			System.err.println("Error calling method " + methodName + " on class " + className + ": " + e);
			throw new RuntimeException(e);
		}
	}

	public ClassExecuter build(String methodName, Object... typesAndArgs) {
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
			localInstance.getDeclaredMethod(methodName, types).invoke(localInstance, args);
			return this;
		} catch (Exception e) {
			System.err.println("Error calling method " + methodName + " on class " + className + ": " + e);
			throw new RuntimeException(e);
		}
	}
}