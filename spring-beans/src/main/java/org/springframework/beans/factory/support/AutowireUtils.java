/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.ClassUtils;

/**
 * Utility class that contains various methods useful for
 * the implementation of autowire-capable bean factories.
 * ******************************************************
 * ~$ 实用工具类,它包含各种方法用于autowire-capable bean的实现工厂.
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 1.1.2
 * @see AbstractAutowireCapableBeanFactory
 */
abstract class AutowireUtils {

	/**
	 * Sort the given constructors, preferring public constructors and "greedy" ones with
	 * a maximum number of arguments. The result will contain public constructors first,
	 * with decreasing number of arguments, then non-public constructors, again with
	 * decreasing number of arguments.
	 * ***********************************************************************************
	 * ~$给定的构造函数,而是公共构造函数和"greedy"的最大数量的参数.
	 *   结果将包含公共构造函数首先,减少数量的参数,然后再内幕构造函数,减少数量的参数.
	 * @param constructors the constructor array to sort
	 */
	public static void sortConstructors(Constructor[] constructors) {
		Arrays.sort(constructors, new Comparator<Constructor>() {
			public int compare(Constructor c1, Constructor c2) {
				boolean p1 = Modifier.isPublic(c1.getModifiers());
				boolean p2 = Modifier.isPublic(c2.getModifiers());
				if (p1 != p2) {
					return (p1 ? -1 : 1);
				}
				int c1pl = c1.getParameterTypes().length;
				int c2pl = c2.getParameterTypes().length;
				return (new Integer(c1pl)).compareTo(c2pl) * -1;
			}
		});
	}

	/**
	 * Sort the given factory methods, preferring public methods and "greedy" ones
	 * with a maximum of arguments. The result will contain public methods first,
	 * with decreasing number of arguments, then non-public methods, again with
	 * decreasing number of arguments.
	 * ****************************************************************************
	 * ~$ 给定的工厂方法,而是公共方法和"greedy"的最大的参数.
	 *    结果将包含公共方法首先,减少数量的参数,然后再非公开的方法,以减少数量的参数.
	 * @param factoryMethods the factory method array to sort
	 */
	public static void sortFactoryMethods(Method[] factoryMethods) {
		Arrays.sort(factoryMethods, new Comparator<Method>() {
			public int compare(Method fm1, Method fm2) {
				boolean p1 = Modifier.isPublic(fm1.getModifiers());
				boolean p2 = Modifier.isPublic(fm2.getModifiers());
				if (p1 != p2) {
					return (p1 ? -1 : 1);
				}
				int c1pl = fm1.getParameterTypes().length;
				int c2pl = fm2.getParameterTypes().length;
				return (new Integer(c1pl)).compareTo(c2pl) * -1;
			}
		});
	}

	/**
	 * Determine whether the given bean property is excluded from dependency checks.
	 * <p>This implementation excludes properties defined by CGLIB.
	 * ******************************************************************************
	 * ~$ 确定给定的bean属性依赖项检查排除在外.
	 * <p>这个实现不包括属性定义为CGLIB.
	 * @param pd the PropertyDescriptor of the bean property
	 * @return whether the bean property is excluded
	 */
	public static boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
		Method wm = pd.getWriteMethod();
		if (wm == null) {
			return false;
		}
		if (!wm.getDeclaringClass().getName().contains("$$")) {
			// Not a CGLIB method so it's OK.
			/** 不是CGLIB方法所以没关系.*/
			return false;
		}
		// It was declared by CGLIB, but we might still want to autowire it
		/** 被CGLIB宣布,但我们可能还想自动装配 */
		// if it was actually declared by the superclass.
		/** 如果它是超类声明的.*/
		Class superclass = wm.getDeclaringClass().getSuperclass();
		return !ClassUtils.hasMethod(superclass, wm.getName(), wm.getParameterTypes());
	}

	/**
	 * Return whether the setter method of the given bean property is defined
	 * in any of the given interfaces.
	 * ***********************************************************************
	 * ~$ 返回给定的bean属性的setter方法是否在任何给定接口的定义.
	 * @param pd the PropertyDescriptor of the bean property
	 * @param interfaces the Set of interfaces (Class objects)
	 * @return whether the setter method is defined by an interface
	 */
	public static boolean isSetterDefinedInInterface(PropertyDescriptor pd, Set<Class> interfaces) {
		Method setter = pd.getWriteMethod();
		if (setter != null) {
			Class targetClass = setter.getDeclaringClass();
			for (Class ifc : interfaces) {
				if (ifc.isAssignableFrom(targetClass) &&
						ClassUtils.hasMethod(ifc, setter.getName(), setter.getParameterTypes())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Resolve the given autowiring value against the given required type,
	 * e.g. an {@link ObjectFactory} value to its actual object result.
	 * ******************************************************************
	 * ~$ 解决给定的自动装配所需值对给定的类型,
	 *    例如一个{@link ObjectFactory }其实际价值对象的结果.
	 * @param autowiringValue the value to resolve
	 * @param requiredType the type to assign the result to
	 * @return the resolved value
	 */
	public static Object resolveAutowiringValue(Object autowiringValue, Class requiredType) {
		if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
			ObjectFactory factory = (ObjectFactory) autowiringValue;
			if (autowiringValue instanceof Serializable && requiredType.isInterface()) {
				autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(),
						new Class[] {requiredType}, new ObjectFactoryDelegatingInvocationHandler(factory));
			}
			else {
				return factory.getObject();
			}
		}
		return autowiringValue;
	}


	/**
	 * Reflective InvocationHandler for lazy access to the current target object.
	 * **************************************************************************
	 * ~$ 反射的InvocationHandler懒惰访问当前的目标对象.
	 */
	private static class ObjectFactoryDelegatingInvocationHandler implements InvocationHandler, Serializable {

		private final ObjectFactory objectFactory;

		public ObjectFactoryDelegatingInvocationHandler(ObjectFactory objectFactory) {
			this.objectFactory = objectFactory;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (methodName.equals("equals")) {
				// Only consider equal when proxies are identical.
				/** 时只考虑平等的代理都是相同的.*/
				return (proxy == args[0]);
			}
			else if (methodName.equals("hashCode")) {
				// Use hashCode of proxy.
				/** 使用hashCode代理.*/
				return System.identityHashCode(proxy);
			}
			else if (methodName.equals("toString")) {
				return this.objectFactory.toString();
			}
			try {
				return method.invoke(this.objectFactory.getObject(), args);
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

}
