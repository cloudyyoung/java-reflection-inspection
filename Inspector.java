import java.lang.reflect.*;

/**
 * CPSC 501 Inspector class
 *
 * @author Yunfan Yang
 */
public class Inspector {

    /**
     * Inspects an object
     * 
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     */
    public void inspect(Object obj, boolean recursive) {
        if (obj.getClass().isArray()) {
            this.inspectArray(obj, recursive, 0);
        } else {
            this.inspectClass(obj, recursive, 0);
        }
    }

    /**
     * Print a string by indentation depth
     * 
     * @param string the string to print
     * @param depth  the number of identation
     */
    private void print(String string, int depth) {
        System.out.println("    ".repeat(depth) + string);
    }

    /**
     * Inspects the class of an object
     * 
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectClass(Object obj, boolean recursive, int depth) {
        this.inspectClass(obj.getClass(), obj, recursive, depth);
    }

    /**
     * Inspects a specified class and by an object
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectClass(Class<?> c, Object obj, boolean recursive, int depth) {
        if (c != null) {
            this.print("Name: " + c.getName(), depth);

            this.inspectSuperClass(c, obj, recursive, depth);
            this.inspectInterfaces(c, obj, recursive, depth);
            this.inspectConstructors(c, obj, recursive, depth);
            this.inspectMethods(c, obj, recursive, depth);
            this.inspectFields(c, obj, recursive, depth);
        }
    }

    /**
     * Inspects the super class of a class
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectSuperClass(Class<?> c, Object obj, boolean recursive, int depth) {
        if (c != null && c.getSuperclass() != null) {
            this.print("Superclass (" + c.getName() + ") -> ", depth);
            this.print("SUPERCLASS (" + c.getName() + ")", depth + 1);
            this.inspectClass(c.getSuperclass(), obj, recursive, depth + 2);
        } else {
            this.print("Superclass (" + c.getName() + "): NONE", depth);
        }
    }

    /**
     * Inspects the interfaces of a class
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectInterfaces(Class<?> c, Object obj, boolean recursive, int depth) {
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
    }

    /**
     * Inspects the constructors of a class
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectConstructors(Class<?> c, Object obj, boolean recursive, int depth) {
        Constructor<?>[] constructors = c.getConstructors();
        if (constructors != null && constructors.length != 0) {
            this.print("Constructors (" + c.getName() + ") -> ", depth);
            for (Constructor<?> constructor : constructors) {
                this.print("CONSTRUCTOR (" + c.getName() + ")", depth + 1);
                this.inspectExecutable(constructor, obj, recursive, depth + 2);
            }
        } else {
            this.print("Constructors (" + c.getName() + "): NONE", depth);
        }
    }

    /**
     * Inspects the methods of a class
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectMethods(Class<?> c, Object obj, boolean recursive, int depth) {
        Method[] methods = c.getDeclaredMethods();
        if (methods != null && methods.length != 0) {
            this.print("Methods (" + c.getName() + ") -> ", depth);
            for (Method method : methods) {
                this.print("METHOD (" + c.getName() + ")", depth + 1);
                this.inspectExecutable(method, obj, recursive, depth + 2);
            }
        } else {
            this.print("Methods (" + c.getName() + "): NONE", depth);
        }
    }

    /**
     * Inspects the fields of a class
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectFields(Class<?> c, Object obj, boolean recursive, int depth) {
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

    /**
     * Inspects a field of a class
     * 
     * @param c         the class to inspect
     * @param field     the fgield to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
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

    /**
     * Inspects the value by an object
     * 
     * @param c         the class to inspect
     * @param obj       the object to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
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

    /**
     * Inspects all the values in an array
     * 
     * @param array     the array to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
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

    /**
     * Inspects an array
     * 
     * @param array     the array to inspect
     * @param recursive if recursively inspect
     * @param depth     the indentation depth
     */
    private void inspectArray(Object array, boolean recursive, int depth) {
        Class<?> c = array.getClass();
        this.print("Name: " + c.getName(), depth);
        this.print("Type name: " + c.getTypeName(), depth);
        this.print("Modifiers: " + Modifier.toString(c.getModifiers()), depth);
        this.inspectArrayValues(array, recursive, depth);
    }

    /**
     * Inspects an executable
     * 
     * @param executable the executable to inspect
     * @param obj        the object to inspect
     * @param recursive  if recursively inspect
     * @param depth      the indentation depth
     */
    // https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Executable.html
    private void inspectExecutable(Executable executable, Object obj, boolean recursive, int depth) {
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

    /**
     * Returns the hash signature of an object
     * 
     * @param obj the object to get hash signature
     * @return the hash signature
     */
    private String getObjectHashSignature(Object obj) {
        return "" + obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode());
    }

    /**
     * Returns if a class is a wrapper type
     * 
     * @param clazz the class to check
     * @return if true, the class is a wrapper class of a primitive type; else, it
     *         is not
     */
    // https://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
    private boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(Integer.class) || clazz.equals(Character.class)
                || clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Double.class)
                || clazz.equals(Long.class) || clazz.equals(Float.class);
    }
}
