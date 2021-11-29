import java.lang.reflect.*;

/**
 * CPSC 501
 * Inspector starter class
 *
 * @author Jonathan Hudson
 */
public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        if (obj.getClass().isArray()) {
            this.inspectArray(obj, recursive, 0);
        } else {
            this.inspectClass(obj, recursive, 0);
        }
    }

    private void print(String string, int depth) {
        System.out.println("    ".repeat(depth) + string);
    }

    private void inspectClass(Object obj, boolean recursive, int depth) {
        this.inspectClass(obj.getClass(), obj, recursive, depth);
    }

    private void inspectClass(Class<?> c, Object obj, boolean recursive, int depth) {
        if (c != null) {
            // note: depth will be needed to capture the output indentation level
            this.print("Name: " + c.getName(), depth);

            if (c != null && c.getSuperclass() != null) {
                this.print("Superclass (" + c.getName() + ") -> ", depth);
                this.print("SUPERCLASS (" + c.getName() + ")", depth + 1);
                this.inspectClass(c.getSuperclass(), obj, recursive, depth + 2);
            } else {
                this.print("Superclass (" + c.getName() + "): NONE", depth);
            }

            Class<?>[] interfaces = c.getInterfaces();
            if (interfaces != null && interfaces.length != 0) {
                this.print("Interfaces (" + c.getName() + ") ->", depth);
                for (Class<?> i : interfaces) {
                    this.print("INTERFACE (" + c.getName() + ")", depth + 1);
                    this.inspectClass(i, obj, recursive, depth + 2);
                }
            } else {
                this.print("Interfaces (" + c.getName() + "): NONE", depth);
            }

            Constructor<?>[] constructors = c.getConstructors();
            if (constructors != null && constructors.length != 0) {
                this.print("Constructors (" + c.getName() + ") -> ", depth);
                for (Constructor<?> constructor : constructors) {
                    this.print("CONSTRUCTOR (" + c.getName() + ")", depth + 1);
                    this.ccccc(constructor, obj, recursive, depth + 2);
                }
            } else {
                this.print("Constructors (" + c.getName() + "): NONE", depth);
            }

            Method[] methods = c.getDeclaredMethods();
            if (methods != null && methods.length != 0) {
                this.print("Methods (" + c.getName() + ") -> ", depth);
                for (Method method : methods) {
                    this.print("METHOD (" + c.getName() + ")", depth + 1);
                    this.ccccc(method, obj, recursive, depth + 2);
                }
            } else {
                this.print("Methods (" + c.getName() + "): NONE", depth);
            }

            Field[] fields = c.getDeclaredFields();
            if (fields != null && fields.length != 0) {
                this.print("Fields (" + c.getName() + ") -> ", depth);
                for (Field field : fields) {
                    this.print("FIELD (" + c.getName() + ")", depth + 1);
                    this.inspectField(c, field, obj, recursive, depth + 2);
                }
            } else {
                this.print("Fields (" + c.getName() + "): NONE ", depth);
            }

        }
    }

    private void inspectField(Class<?> c, Field field, Object obj, boolean recursive, int depth) {
        Class<?> fieldType = field.getType();
        boolean isArray = fieldType.isArray();
        Object value;

        try {
            field.setAccessible(true);
            value = field.get(obj);
        } catch (Exception e) {
            this.print("ERROR: " + e.getMessage(), depth);
            this.print("Field: " + field, depth);
            this.print("Object: " + obj, depth);
            this.print("Exception: " + e, depth);
            return;
        }

        this.print("Name: " + field.getName(), depth);
        this.print("Type: " + field.getType().getName(), depth);
        this.print("Modifiers: " + Modifier.toString(field.getModifiers()), depth);

        if (isArray) {
			this.inspectArrayValues(value, false, depth);
        } else {
            this.inspectObjectValue(fieldType, value, recursive, depth);
        }
    }

    private void inspectObjectValue(Class<?> c, Object obj, boolean recursive, int depth) {
        if (c.isPrimitive() || this.isWrapperType(c) || obj == null) {
            this.print("Value: " + obj, depth);
        } else {
            this.print("Value (ref): " + this.getObjectHashSignature(obj), depth);

            if (recursive) {
                this.print("CLASS (" + this.getObjectHashSignature(obj) + ")", depth + 1);
                this.inspectClass(obj.getClass(), obj, recursive, depth + 2);
            }
        }
    }

    private void inspectArrayValues(Object array, boolean recursive, int depth) {
        Class<?> componentType = array.getClass().getComponentType();
        int length = Array.getLength(array);
        this.print("Component type: " + componentType, depth);
        this.print("Length: " + length, depth);

        if (length > 0) {
            this.print("Entries -> ", depth);

            for (int t = 0; t < length; t++) {
                Object object = Array.get(array, t);
                this.inspectObjectValue(componentType, object, recursive, depth + 1);
            }
        } else {
            this.print("Entries: EMPTY", depth);
        }
    }

    private void inspectArray(Object array, boolean recursive, int depth) {
        Class<?> c = array.getClass();
        this.print("Name: " + c.getName(), depth);
        this.print("Type name: " + c.getTypeName(), depth);
        this.print("Modifiers: " + Modifier.toString(c.getModifiers()), depth);
        this.inspectArrayValues(array, recursive, depth);
    }

    // https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Executable.html
    private void ccccc(Executable executable, Object obj, boolean recursive, int depth) {
        this.print("Name: " + executable.getName(), depth);
        this.print("Modifiers: " + Modifier.toString(executable.getModifiers()), depth);

        // Pamameter types
        Class<?>[] params = executable.getParameterTypes();
        if (params != null && params.length != 0) {
            String[] params_names = new String[params.length];
            for (int t = 0; t < params.length; t++) {
                params_names[t] = params[t].getName();
            }
            this.print("Parameter types: [" + String.join(", ", params_names) + "]", depth);
        } else {
            this.print("Parameter types: NONE", depth);
        }

        if (executable instanceof Method) {
            Method method = (Method) executable;
            this.print("Return type: " + method.getReturnType().getName(), depth);
        }

        Class<?>[] exceptions = executable.getExceptionTypes();
        if (exceptions != null && exceptions.length != 0) {
            this.print("Exceptions -> ", depth);
            for (Class<?> exception : exceptions) {
                this.print(exception.getName(), depth + 1);
            }
        } else {
            this.print("Exceptions: NONE", depth);
        }
    }

    private String getObjectHashSignature(Object obj) {
        return "" + obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode());
    }

    // https://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
    private boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(Integer.class) || clazz.equals(Character.class)
                || clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Double.class)
                || clazz.equals(Long.class) || clazz.equals(Float.class);
    }

}
