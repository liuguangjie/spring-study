/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.transaction.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.util.ClassUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * Simple implementation of TransactionAttributeSource that
 * allows attributes to be stored per method in a map.
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 24.04.2003
 * @see #isMatch
 * @see NameMatchTransactionAttributeSource
 */
public class MethodMapTransactionAttributeSource
		implements TransactionAttributeSource, BeanClassLoaderAware, InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	/** Map from method name to attribute value */
	private Map methodMap;

	/** Map from Method to TransactionAttribute */
	private Map transactionAttributeMap = new HashMap();

	/** Map from Method to name pattern used for registration */
	private Map methodNameMap = new HashMap();

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();


	/**
	 * Set a name/attribute map, consisting of "FQCN.method" method names
	 * (e.g. "com.mycompany.mycode.MyClass.myMethod") and TransactionAttribute
	 * instances (or Strings to be converted to TransactionAttribute instances).
	 * <p>Intended for configuration via setter injection, typically within
	 * a Spring bean factory. Relies on <code>afterPropertiesSet()</code>
	 * being called afterwards.
	 * @see TransactionAttribute
	 * @see TransactionAttributeEditor
	 */
	public void setMethodMap(Map methodMap) {
		this.methodMap = methodMap;
	}

	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

	/**
	 * Eagerly initializes the specified "methodMap", if any.
	 * @see #initMethodMap()
	 */
	public void afterPropertiesSet() {
		initMethodMap();
	}

	/**
	 * Initialize the specified "methodMap", if any.
	 * @see #setMethodMap
	 */
	protected synchronized void initMethodMap() {
		if (this.methodMap != null) {
			Iterator it = this.methodMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String name = (String) entry.getKey();
				// Check whether we need to convert from String to TransactionAttribute.
				TransactionAttribute attr = null;
				if (entry.getValue() instanceof TransactionAttribute) {
					attr = (TransactionAttribute) entry.getValue();
				}
				else {
					TransactionAttributeEditor editor = new TransactionAttributeEditor();
					editor.setAsText(entry.getValue().toString());
					attr = (TransactionAttribute) editor.getValue();
				}
				addTransactionalMethod(name, attr);
			}
		}
	}


	/**
	 * Add an attribute for a transactional method.
	 * Method names can end or start with "*" for matching multiple methods.
	 * @param name class and method name, separated by a dot
	 * @param attr attribute associated with the method
	 */
	public void addTransactionalMethod(String name, TransactionAttribute attr) {
		int lastDotIndex = name.lastIndexOf(".");
		if (lastDotIndex == -1) {
			throw new TransactionUsageException("'" + name + "' is not a valid method name: format is FQN.methodName");
		}
		String className = name.substring(0, lastDotIndex);
		String methodName = name.substring(lastDotIndex + 1);
		try {
			Class clazz = ClassUtils.forName(className, this.beanClassLoader);
			addTransactionalMethod(clazz, methodName, attr);
		}
		catch (ClassNotFoundException ex) {
			throw new TransactionUsageException("Class '" + className + "' not found");
		}
	}

	/**
	 * Add an attribute for a transactional method.
	 * Method names can end or start with "*" for matching multiple methods.
	 * @param clazz target interface or class
	 * @param mappedName mapped method name
	 * @param attr attribute associated with the method
	 */
	public void addTransactionalMethod(Class clazz, String mappedName, TransactionAttribute attr) {
		String name = clazz.getName() + '.'  + mappedName;

		// TODO address method overloading? At present this will
		// simply match all methods that have the given name.
		// Consider EJB syntax (int, String) etc.?
		Method[] methods = clazz.getDeclaredMethods();
		List matchingMethods = new ArrayList();
		for (int i = 0; i < methods.length; i++) {
			if (isMatch(methods[i].getName(), mappedName)) {
				matchingMethods.add(methods[i]);
			}
		}
		if (matchingMethods.isEmpty()) {
			throw new TransactionUsageException(
					"Couldn't find method '" + mappedName + "' on class [" + clazz.getName() + "]");
		}

		// register all matching methods
		for (Iterator it = matchingMethods.iterator(); it.hasNext();) {
			Method method = (Method) it.next();
			String regMethodName = (String) this.methodNameMap.get(method);
			if (regMethodName == null || (!regMethodName.equals(name) && regMethodName.length() <= name.length())) {
				// No already registered method name, or more specific
				// method name specification now -> (re-)register method.
				if (logger.isDebugEnabled() && regMethodName != null) {
					logger.debug("Replacing attribute for transactional method [" + method + "]: current name '" +
							name + "' is more specific than '" + regMethodName + "'");
				}
				this.methodNameMap.put(method, name);
				addTransactionalMethod(method, attr);
			}
			else {
				if (logger.isDebugEnabled() && regMethodName != null) {
					logger.debug("Keeping attribute for transactional method [" + method + "]: current name '" +
							name + "' is not more specific than '" + regMethodName + "'");
				}
			}
		}
	}

	/**
	 * Add an attribute for a transactional method.
	 * @param method the method
	 * @param attr attribute associated with the method
	 */
	public void addTransactionalMethod(Method method, TransactionAttribute attr) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding transactional method [" + method + "] with attribute [" + attr + "]");
		}
		this.transactionAttributeMap.put(method, attr);
	}

	/**
	 * Return if the given method name matches the mapped name.
	 * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
	 * as well as direct equality. Can be overridden in subclasses.
	 * @param methodName the method name of the class
	 * @param mappedName the name in the descriptor
	 * @return if the names match
	 * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
	 */
	protected boolean isMatch(String methodName, String mappedName) {
		return PatternMatchUtils.simpleMatch(mappedName, methodName);
	}


	public TransactionAttribute getTransactionAttribute(Method method, Class targetClass) {
		if (this.methodMap != null) {
			initMethodMap();
		}
		return (TransactionAttribute) this.transactionAttributeMap.get(method);
	}

}
