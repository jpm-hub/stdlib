package jpm;

import java.util.Optional;
import java.util.Stack;

public class jpm {
    
    public static ClassExecuter require(String fullClassName) {
        try {
            return new ClassExecuter(fullClassName);
        } catch (Exception e) {
            System.err.println("Class " + fullClassName + " not found: " + e);
            return null;
        }
    }

    public static ClassExecuter requireFatal(String fullClassName, String errorMessage) {
        try {
            return new ClassExecuter(fullClassName);
        } catch (Exception e) {
            System.err.println(errorMessage + "\n" + e);
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static Thread run(Runnable r){
        return Thread.ofVirtual().start(r);
    }

    public static void println(Object... o) {
        for (Object obj : o) {
            System.out.print(obj + " ");
        }
        System.out.println();
    }

    private static boolean isPrimitive(Object o) {
        return o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof Character;
    }

    static int indent = 0;
    static Stack<String> classes = new Stack<String>();
    public static void printDebug(Object o) {
        if (indent > 100) {
            throw new RuntimeException("Object nesting too deep");
        }
        if (o == null) {
            System.out.println("null");
        } else if (o instanceof String || o instanceof Character) {
            System.out.println("\"" + o + "\"");
        } else if (o instanceof Number || o instanceof Boolean) {
            System.out.println(o);
        } else if (o instanceof Iterable) {
            indent++;
            boolean i = false;
            Object last = null;
            for (Object item : (Iterable<?>) o) {
                if (isPrimitive(item)) {
                    if (!i) {
                        System.out.print("[");
                        i = true;
                    }
                    if (item instanceof String) {
                        System.out.print("\"" + item + "\",");
                    } else {
                        System.out.print(item+",");
                    }
                } else {
                    if (!i) {
                        System.out.println("[");
                        i = true;
                    }
                    System.out.print("  ".repeat(indent));
                    printDebug(item);
                }
                last = item;
            }
            indent--;
            if (isPrimitive(last)) {
                System.out.println("]");
            } else {
                System.out.print("  ".repeat(indent));
                System.out.println("]");
            }
        } else if (o instanceof java.util.Map) {
            System.out.println("Map {");
            indent++;
            for (Object entry : ((java.util.Map<?, ?>) o).entrySet()) {
                System.out.print("  ".repeat(indent));
                printDebug(entry);
            }
            indent--;
            System.out.print("  ".repeat(indent));
            System.out.println("}");
        } else if (o.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(o);
            boolean i = false;
            indent++;
            int j;
            for ( j = 0; j < len; j++) {
                Object item = java.lang.reflect.Array.get(o, j);
                if (isPrimitive(item)) {
                    if (!i) {
                        System.out.print("[");
                        i = true;
                    }
                    if (j == len - 1) {
                        if (item instanceof String) {
                            System.out.println("\"" + item + "\"]");
                        } else {
                            System.out.println(item + "]");
                        }
                    } else {
                        if (item instanceof String) {
                            System.out.print("\"" + item + "\",");
                        } else {
                            System.out.print(item + ",");
                        }
                    }
                } else {
                    if (!i) {
                        System.out.println("[");
                        i = true;
                    }
                    System.out.print("  ".repeat(indent));
                    printDebug(item);
                }
            }
            indent--;
            if (!isPrimitive(java.lang.reflect.Array.get(o, j - 1))) {
                System.out.print("  ".repeat(indent));
                System.out.println("]");
            }
        } else if (o instanceof java.util.Map.Entry) {
            java.util.Map.Entry<?, ?> entry = (java.util.Map.Entry<?, ?>) o;
            System.out.print("\"" + entry.getKey() + "\"" + " : ");
            printDebug(entry.getValue());
        } else if (o instanceof Class<?>) {
            Class<?> clazz = (Class<?>) o;
            System.out.println("Class: " + clazz.getName());
        } else if (o instanceof Enum<?>) {
            Enum<?> enumValue = (Enum<?>) o;
            System.out.println("Enum: " + enumValue.getClass().getName() + "." + enumValue.name());
        } else {
            Class<?> clazz = o.getClass();
            System.out.println(clazz.getName() + " {");
            if (classes.contains(clazz.getName())) {
                System.out.print("  ".repeat(indent + 1));
                System.out.println(clazz.getName() + " (circular reference) ...");
                System.out.print("  ".repeat(indent));
                System.out.println("}");
                return;
            }
            classes.push(clazz.getName());
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            indent++;
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(o);
                    System.out.print("  ".repeat(indent));
                    System.out.print(field.getName() + " = ");
                    printDebug(value);
                } catch (Exception e) {
                    System.out.print("  ".repeat(indent));
                    System.out.println(field.getName() + " = <unable to access>");
                }
            }
            indent--;
            classes.pop();
            System.out.print("  ".repeat(indent));
            System.out.println("}");
        }
    }
}