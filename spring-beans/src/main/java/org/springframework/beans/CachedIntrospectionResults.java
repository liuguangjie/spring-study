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

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Internal class that caches JavaBeans {@link PropertyDescriptor}
 * information for a Java class. Not intended for direct use by application code.
 *
 * <p>Necessary for own caching of descriptors within the application's
 * ClassLoader, rather than rely on the JDK's system-wide BeanInfo cache
 * (in order to avoid leaks on ClassLoader shutdown).
 *
 * <p>Information is cached statically, so we don't need to create new
 * objects of this class for every JavaBean we manipulate. Hence, this class
 * implements the factory design pattern, using a private constructor and
 * a static {@link #forClass(Class)} factory method to obtain instances.
 * ******************************************************************************
 * ~$ 内部类缓存javabean {@link PropertyDescriptor }一个Java类的信息.不能直接使用的应用程序代码.
 *
 * <p>所需的缓存应用程序的类加载器内的描述符,而不是依赖JDK的系统范围的BeanInfo缓存(为了避免泄漏类加载器关闭).
 *
 * <p>缓存的静态信息,所以我们不需要为每个JavaBean创建这个类的新对象我们操作.
 *    因此,这个类实现工厂设计模式,使用一个私有构造函数和静态{@link #forClass(Class)}工厂方法获得实例.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 05 May 2001
 * @see #acceptClassLoader(ClassLoader)
 * @see #clearClassLoader(ClassLoader)
 * @see #forClass(Class)
 */
public class CachedIntrospectionResults {

	private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);

	/**
	 * Set of ClassLoaders that this CachedIntrospectionResults class will always
	 * accept classes from, even if the classes do not qualify as cache-safe.
	 * ****************************************************************************
	 * ~$ 组这个CachedIntrospectionResults类的类加载器总是接受类,即使cache-safe类不符合.
	 */
	static final Set<ClassLoader> acceptedClassLoaders = Collections.synchronizedSet(new HashSet<ClassLoader>());

	/**
	 * Map keyed by class containing CachedIntrospectionResults.
	 * Needs to be a WeakHashMap with WeakReferences as values to allow
	 * for proper garbage collection in case of multiple class loaders.
	 * *****************************************************************
	 * ~$ Map由包含CachedIntrospectionResults类.需要与weakreference WeakHashMap值允许适当的垃圾收集多个类加载器.
	 */
	static final Map<Class, Object> classCache = Collections.synchronizedMap(new WeakHashMap<Class, Object>());


	/**
	 * Accept the given ClassLoader as cache-safe, even if its classes would
	 * not qualify as cache-safe in this CachedIntrospectionResults class.
	 * <p>This configuration method is only relevant in scenarios where the Spring
	 * classes reside in a 'common' ClassLoader (e.g. the system ClassLoader)
	 * whose lifecycle is not coupled to the application. In such a scenario,
	 * CachedIntrospectionResults would by default not cache any of the application's
	 * classes, since they would create a leak in the common ClassLoader.
	 * <p>Any <code>acceptClassLoader</code> call at application startup should
	 * be paired with a {@link #clearClassLoader} call at application shutdown.
	 * ******************************************************************************
	 * ~$ 接受给定的类加载器cache-safe,即使在这类不会成为cache-safe CachedIntrospectionResults类.
	 * <p>这种配置方法只在场景相关的Spring类驻留在一个常见的类加载器(例如系统类加载器)的生命周期不耦合的应用程序.
	 *    在这种情况下,CachedIntrospectionResults默认情况下不会缓存任何应用程序的类,因为它们将创建一个泄漏的常见的类加载器.
	 * <p>任何acceptClassLoader调用应用程序启动时应搭配{@link #clearClassLoader }调用在应用程序关闭.
	 * @param classLoader the ClassLoader to accept
	 */
	public static void acceptClassLoader(ClassLoader classLoader) {
		if (classLoader != null) {
			acceptedClassLoaders.add(classLoader);
		}
	}

	/**
	 * Clear the introspection cache for the given ClassLoader, removing the
	 * introspection results for all classes underneath that ClassLoader,
	 * and deregistering the ClassLoader (and any of its children) from the
	 * acceptance list.
	 * *********************************************************************
	 * ~$ 明确自检缓存为给定的类加载器,删除所有类在类加载器内省的结果,并注销类加载器(and any of its children)验收清单.
	 * @param classLoader the ClassLoader to clear the cache for
	 */
	public static void clearClassLoader(ClassLoader classLoader) {
		if (classLoader == null) {
			return;
		}
		synchronized (classCache) {
			for (Iterator<Class> it = classCache.keySet().iterator(); it.hasNext();) {
				Class beanClass = it.next();
				if (isUnderneathClassLoader(beanClass.getClassLoader(), classLoader)) {
					it.remove();
				}
			}
		}
		synchronized (acceptedClassLoaders) {
			for (Iterator<ClassLoader> it = acceptedClassLoaders.iterator(); it.hasNext();) {
				ClassLoader registeredLoader = it.next();
				if (isUnderneathClassLoader(registeredLoader, classLoader)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Create CachedIntrospectionResults for the given bean class.
	 * <P>We don't want to use synchronization here. Object references are atomic,
	 * so we can live with doing the occasional unnecessary lookup at startup only.
	 * ****************************************************************************
	 * ~$ 为给定的bean类创建CachedIntrospectionResults.
	 * <p> 我们不想使用同步.对象引用是原子,所以我们可以忍受做偶尔只在启动时不必要的查找.
	 * @param beanClass the bean class to analyze
	 * @return the corresponding CachedIntrospectionResults
	 * @throws BeansException in case of introspection failure
	 */
	static CachedIntrospectionResults forClass(Class beanClass) throws BeansException {
		CachedIntrospectionResults results;
		Object value = classCache.get(beanClass);
		if (value instanceof Reference) {
			Reference ref = (Reference) value;
			results = (CachedIntrospectionResults) ref.get();
		}
		else {
			results = (CachedIntrospectionResults) value;
		}
		if (results == null) {
			// On JDK 1.5 and higher, it is almost always safe to cache the bean class...
			/** 在JDK 1.5和更高版本,它几乎总是安全缓存bean类...*/
			// The sole exception is a custom BeanInfo class being provided in a non-safe ClassLoader.
			/** 唯一的例外是一个定制的BeanInfo类提供non-safe类加载器.*/
			boolean fullyCacheable =
					ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) ||
					isClassLoaderAccepted(beanClass.getClassLoader());
			if (fullyCacheable || !ClassUtils.isPresent(beanClass.getName() + "BeanInfo", beanClass.getClassLoader())) {
				results = new CachedIntrospectionResults(beanClass, fullyCacheable);
				classCache.put(beanClass, results);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
				}
				results = new CachedIntrospectionResults(beanClass, true);
				classCache.put(beanClass, new WeakReference<CachedIntrospectionResults>(results));
			}
		}
		return results;
	}

	/**
	 * Check whether this CachedIntrospectionResults class is configured
	 * to accept the given ClassLoader.
	 * ******************************************************************
	 * ~$ 检查是否这个CachedIntrospectionResults类配置为接受给定的类加载器.
	 * @param classLoader the ClassLoader to check
	 * @return whether the given ClassLoader is accepted
	 * @see #acceptClassLoader
	 */
	private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
		// Iterate over array copy in order to avoid synchronization for the entire
		/** 为了避免迭代数组副本同步整个 */
		// ClassLoader check (avoiding a synchronized acceptedClassLoaders Iterator).
		/** 类加载器检查(避免同步acceptedClassLoaders迭代器).*/
		ClassLoader[] acceptedLoaderArray =
				acceptedClassLoaders.toArray(new ClassLoader[acceptedClassLoaders.size()]);
		for (ClassLoader registeredLoader : acceptedLoaderArray) {
			if (isUnderneathClassLoader(classLoader, registeredLoader)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given ClassLoader is underneath the given parent,
	 * that is, whether the parent is within the candidate's hierarchy.
	 * *******************************************************************
	 * ~$ 检查给定的类加载器是否在给定的家长,也就是说,父母是否在候选人的层次结构.
	 * @param candidate the candidate ClassLoader to check
	 * @param parent the parent ClassLoader to check for
	 */
	private static boolean isUnderneathClassLoader(ClassLoader candidate, ClassLoader parent) {
		if (candidate == null) {
			return false;
		}
		if (candidate == parent) {
			return true;
		}
		ClassLoader classLoaderToCheck = candidate;
		while (classLoaderToCheck != null) {
			classLoaderToCheck = classLoaderToCheck.getParent();
			if (classLoaderToCheck == parent) {
				return true;
			}
		}
		return false;
	}


	/** The BeanInfo object for the introspected bean class */
	/** BeanInfo进行自检bean类的对象*/
	private final BeanInfo beanInfo;

	/** PropertyDescriptor objects keyed by property name String */
	/** PropertyDescriptor键控的对象属性名的字符串 */
	private final Map<String, PropertyDescriptor> propertyDescriptorCache;


	/**
	 * Create a new CachedIntrospectionResults instance for the given class.
	 * *********************************************************************
	 * ~$ 创建一个新的CachedIntrospectionResults为给定的类实例.
	 * @param beanClass the bean class to analyze
	 * @throws BeansException in case of introspection failure
	 */
	private CachedIntrospectionResults(Class beanClass, boolean cacheFullMetadata) throws BeansException {
		try {
			if (logger.isTraceEnabled()) {
				logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
			}
			this.beanInfo = new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass));

			// Immediately remove class from Introspector cache, to allow for proper
			/** 立即删除从内省缓存类,允许适当的 */
			// garbage collection on class loader shutdown - we cache it here anyway,
			/** 垃圾收集在类装入器关闭缓存——我们这里不管怎样,*/
			// in a GC-friendly manner. In contrast to CachedIntrospectionResults,
			/** GC-friendly的方式。CachedIntrospectionResults相比,*/
			// Introspector does not use WeakReferences as values of its WeakHashMap!
			/** 内省不使用weakreference引用的值WeakHashMap! */
			Class classToFlush = beanClass;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			}
			while (classToFlush != null);

			if (logger.isTraceEnabled()) {
				logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
			}
			this.propertyDescriptorCache = new LinkedHashMap<String, PropertyDescriptor>();

			// This call is slow so we do it once.
			/**  这个调用是缓慢的,所以我们做一次 */
			PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (Class.class.equals(beanClass) && "classLoader".equals(pd.getName())) {
					// Ignore Class.getClassLoader() method - nobody needs to bind to that
					/** 忽略Class.getClassLoader()方法——没有人需要绑定 */
					continue;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Found bean property '" + pd.getName() + "'" +
							(pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") +
							(pd.getPropertyEditorClass() != null ?
									"; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
				}
				if (cacheFullMetadata) {
					pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
				}
				this.propertyDescriptorCache.put(pd.getName(), pd);
			}
		}
		catch (IntrospectionException ex) {
			throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
		}
	}

	BeanInfo getBeanInfo() {
		return this.beanInfo;
	}

	Class getBeanClass() {
		return this.beanInfo.getBeanDescriptor().getBeanClass();
	}

	PropertyDescriptor getPropertyDescriptor(String name) {
		PropertyDescriptor pd = this.propertyDescriptorCache.get(name);
		if (pd == null && StringUtils.hasLength(name)) {
			// Same lenient fallback checking as in PropertyTypeDescriptor...
			/** 同样宽大后备检查如PropertyTypeDescriptor...*/
			pd = this.propertyDescriptorCache.get(name.substring(0, 1).toLowerCase() + name.substring(1));
			if (pd == null) {
				pd = this.propertyDescriptorCache.get(name.substring(0, 1).toUpperCase() + name.substring(1));
			}
		}
		return (pd == null || pd instanceof GenericTypeAwarePropertyDescriptor ? pd :
				buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd));
	}

	PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
		int i = 0;
		for (PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
			pds[i] = (pd instanceof GenericTypeAwarePropertyDescriptor ? pd :
					buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd));
			i++;
		}
		return pds;
	}

	private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class beanClass, PropertyDescriptor pd) {
		try {
			return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(),
					pd.getWriteMethod(), pd.getPropertyEditorClass());
		}
		catch (IntrospectionException ex) {
			throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
		}
	}

}
