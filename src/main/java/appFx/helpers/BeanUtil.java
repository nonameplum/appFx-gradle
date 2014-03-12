package appFx.helpers;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * <p>This class implements the ability to get and set properties on a
 * bean.  This included the concept of embedded properties that may
 * be referenced like <code>Bean.property.property.property</code>.</p>
 * <p>
 * Copyright 2002 Sapient
 *
 * @author Greg Hinkle, January 2002
 * @version $Revision: 1.11 $ ($Author: dvoet $)
 * @since carbon 1.0
 */
public final class BeanUtil {
    /**
     * String used to separate multiple properties inside of embedded
     * beans.
     */
    private static final String PROPERTY_SEPARATOR = ".";

    /**
     * An empty class array used for null parameter method reflection
     */
    private static Class[] NO_PARAMETERS_ARRAY = new Class[]{
    };
    /**
     * an empty object array used for null parameter method reflection
     */
    private static Object[] NO_ARGUMENTS_ARRAY = new Object[]{
    };

    /**
     * The constructor is private so that <tt>new</tt> cannot be used.
     */
    private BeanUtil() {
    }

    /**
     * Retreives a property descriptor object for a given property.
     * <p>
     * Uses the classes in <code>java.beans</code> to get back
     * a descriptor for a property.  Read-only and write-only
     * properties will have a slower return time.
     * </p>
     *
     * @param propertyName The programmatic name of the property
     * @param beanClass    The Class object for the target bean.
     *                     For example sun.beans.OurButton.class.
     * @return a PropertyDescriptor for a property that follows the
     * standard Java naming conventions.
     * @throws PropertyNotFoundException indicates that the property
     *                                   could not be found on the bean class.
     */
    private static final PropertyDescriptor getPropertyDescriptor(
            String propertyName,
            Class beanClass) {

        PropertyDescriptor resultPropertyDescriptor = null;


        char[] pNameArray = propertyName.toCharArray();
        pNameArray[0] = Character.toLowerCase(pNameArray[0]);
        propertyName = new String(pNameArray);

        try {
            resultPropertyDescriptor =
                    new PropertyDescriptor(propertyName, beanClass);
        } catch (IntrospectionException e1) {
            // Read-only and write-only properties will throw this
            // exception.  The properties must be looked up using
            // brute force.

            // This will get the list of all properties and iterate
            // through looking for one that matches the property
            // name passed into the method.
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

                PropertyDescriptor[] propertyDescriptors =
                        beanInfo.getPropertyDescriptors();

                for (int i = 0; i < propertyDescriptors.length; i++) {
                    if (propertyDescriptors[i]
                            .getName()
                            .equals(propertyName)) {

                        // If the names match, this this describes the
                        // property being searched for.  Break out of
                        // the iteration.
                        resultPropertyDescriptor = propertyDescriptors[i];
                        break;
                    }
                }
            } catch (IntrospectionException e2) {
                e2.printStackTrace();
            }
        }

        // If no property descriptor was found, then this bean does not
        // have a property matching the name passed in.
        if (resultPropertyDescriptor == null) {
            System.out.println("resultPropertyDescriptor == null");
        }

        return resultPropertyDescriptor;
    }

    /**
     * <p>Gets the specified attribute from the specified object.  For example,
     * <code>getObjectAttribute(o, "address.line1")</code> will return
     * the result of calling <code>o.getAddress().getLine1()</code>.<p>
     * <p>
     * <p>The attribute specified may contain as many levels as you like. If at
     * any time a null reference is acquired by calling one of the successive
     * getter methods, then the return value from this method is also null.</p>
     * <p>
     * <p>When reading from a boolean property the underlying bean introspector
     * first looks for an is&lt;Property&gt; read method, not finding one it will
     * still look for a get&lt;Property&gt; read method.  Not finding either, the
     * property is considered write-only.</p>
     *
     * @param bean          the bean to set the property on
     * @param propertyNames the name of the propertie(s) to retrieve.  If this is
     *                      null or the empty string, then <code>bean</code> will be returned.
     * @return the object value of the bean attribute
     * @throws PropertyNotFoundException indicates the the given property
     *                                   could not be found on the bean
     * @throws NoSuchMethodException     Not thrown
     * @throws InvocationTargetException if a specified getter method throws an
     *                                   exception.
     * @throws IllegalAccessException    if a getter method is
     *                                   not public or property is write-only.
     */
    public static Object getObjectAttribute(Object bean, String propertyNames)
            throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {


        Object result = bean;

        StringTokenizer propertyTokenizer =
                new StringTokenizer(propertyNames, PROPERTY_SEPARATOR);

        // Run through the tokens, calling get methods and
        // replacing result with the new object each time.
        // If the result equals null, then simply return null.
        while (propertyTokenizer.hasMoreElements() && result != null) {
            Class resultClass = result.getClass();
            String currentPropertyName = propertyTokenizer.nextToken();

            PropertyDescriptor propertyDescriptor =
                    getPropertyDescriptor(currentPropertyName, resultClass);

            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                throw new IllegalAccessException(
                        "User is attempting to "
                                + "read from a property that has no read method.  "
                                + " This is likely a write-only bean property.  Caused "
                                + "by property ["
                                + currentPropertyName
                                + "] on class ["
                                + resultClass
                                + "]");
            }

            result = readMethod.invoke(result, NO_ARGUMENTS_ARRAY);
        }

        return result;
    }

    /**
     * <p>Sets the specified attribute on the specified object.  For example,
     * <code>getObjectAttribute(o, "address.line1", value)</code> will call
     * <code>o.getAddress().setLine1(value)</code>.<p>
     * <p>
     * <p>The attribute specified may contain as many levels as you like. If at
     * any time a null reference is acquired by calling one of the successive
     * getter methods, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param bean          the bean to call the getters on
     * @param propertyNames the name of the attribute(s) to set.  If this is
     *                      null or the empty string, then an exception is thrown.
     * @param value         the value of the object to set on the bean property
     * @throws PropertyNotFoundException indicates the the given property
     *                                   could not be found on the bean
     * @throws IllegalArgumentException  if the supplied parameter is not of
     *                                   a valid type
     * @throws NoSuchMethodException     never
     * @throws IllegalAccessException    if a getter or setter method is
     *                                   not public or property is read-only.
     * @throws InvocationTargetException if a specified getter method throws an
     *                                   exception.
     */
    public static void setObjectAttribute(
            Object bean,
            String propertyNames,
            Object value)
            throws
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            NoSuchMethodException {


        Object result = bean;
        String propertyName = propertyNames;

        // Check if this has some embedded properties.  If it does, use the
        // getObjectAttribute to get the proper object to call this on.
        int indexOfLastPropertySeparator =
                propertyName.lastIndexOf(PROPERTY_SEPARATOR);

        if (indexOfLastPropertySeparator >= 0) {
            String embeddedProperties =
                    propertyName.substring(0, indexOfLastPropertySeparator);

            // Grab the final property name after the last property separator
            propertyName =
                    propertyName.substring(
                            indexOfLastPropertySeparator + PROPERTY_SEPARATOR.length());

            result = getObjectAttribute(result, embeddedProperties);
        }

        Class resultClass = result.getClass();

        PropertyDescriptor propertyDescriptor =
                getPropertyDescriptor(propertyName, resultClass);

        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod == null) {
            throw new IllegalAccessException(
                    "User is attempting to write "
                            + "to a property that has no write method.  This is likely "
                            + "a read-only bean property.  Caused by property ["
                            + propertyName
                            + "] on class ["
                            + resultClass
                            + "]");
        }

        writeMethod.invoke(result, new Object[]{value});
    }

    public static Map<String, Object> getNonNullProperties(final Object thingy) {
        final Map<String, Object> nonNullProperties = new TreeMap<String, Object>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(thingy
                    .getClass());
            for (final PropertyDescriptor descriptor : beanInfo
                    .getPropertyDescriptors()) {
                try {
                    final Object propertyValue = descriptor.getReadMethod()
                            .invoke(thingy);
                    if (propertyValue != null) {
                        nonNullProperties.put(descriptor.getName(),
                                propertyValue);
                    }
                } catch (final IllegalArgumentException e) {
                    // handle this please
                } catch (final IllegalAccessException e) {
                    // and this also
                } catch (final InvocationTargetException e) {
                    // and this, too
                }
            }
        } catch (final IntrospectionException e) {
            // do something sensible here
        }
        return nonNullProperties;
    }

    public static Object getValue(Object object, String fieldName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
            if (pd.getReadMethod() != null && !"class".equals(pd.getName()) && pd.getName().equalsIgnoreCase(fieldName)) {
                System.out.println(pd.getReadMethod().invoke(object));
                return pd.getReadMethod().invoke(object);
            }
        }
        return null;
    }

    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <E> E get(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (E) field.get(object);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}