/*
 * Copyright 2002-2011 the original author or authors.
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

import static java.lang.String.format;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Decorates a standard {@link BeanInfo} object (likely created created by
 * {@link Introspector#getBeanInfo(Class)}) by including non-void returning setter
 * methods in the collection of {@link #getPropertyDescriptors() property descriptors}.
 * Both regular and
 * <a href="http://download.oracle.com/javase/tutorial/javabeans/properties/indexed.html">
 * indexed properties</a> are fully supported.
 *
 * <p>The wrapped {@code BeanInfo} object is not modified in any way.
 * *****************************************************************************************
 * ~$ 装修标准{@link BeanInfo }对象(可能由创建{@link Introspector#getBeanInfo(Class)}),
 *    包括非void返回setter方法的集合{@link #getPropertyDescriptors() property descriptors}.
 *    定期和< a href = " http://download.oracle.com/javase/tutorial/javabeans/properties/indexed.html" >索引属性是完全支持.
 *
 * <p>包装{@code BeanInfo }对象并不以任何方式修改.
 * @author Chris Beams
 * @since 3.1
 * @see CachedIntrospectionResults
 */
class ExtendedBeanInfo implements BeanInfo {

	private final Log logger = LogFactory.getLog(getClass());

	private final BeanInfo delegate;

	private final SortedSet<PropertyDescriptor> propertyDescriptors =
		new TreeSet<PropertyDescriptor>(new PropertyDescriptorComparator());


	/**
	 * Wrap the given delegate {@link BeanInfo} instance and find any non-void returning
	 * setter methods, creating and adding a {@link PropertyDescriptor} for each.
	 *
	 * <p>Note that the wrapped {@code BeanInfo} is modified by this process.
	 * *********************************************************************************
	 * ~$ 用给定的委托{@link BeanInfo }实例和找到任何非void返回setter方法,创建和添加一个{@link PropertyDescriptor }.
	 *
	 * <p>注意包装{@code BeanInfo }修改了这一过程.
	 * @see #getPropertyDescriptors()
	 * @throws IntrospectionException if any problems occur creating and adding new {@code PropertyDescriptors}
	 */
	public ExtendedBeanInfo(BeanInfo delegate) throws IntrospectionException {
		this.delegate = delegate;

		// PropertyDescriptor instances from the delegate object are never added directly, but always
		/** PropertyDescriptor委托对象的实例没有直接添加,但总是 */
		// copied to the local collection of #propertyDescriptors and returned by calls to
		/** 复制到本地# propertyDescriptors和由调用返回的集合 */
		// #getPropertyDescriptors(). this algorithm iterates through all methods (method descriptors)
		/** # getPropertyDescriptors().该算法遍历所有方法(方法描述符) */
		// in the wrapped BeanInfo object, copying any existing PropertyDescriptor or creating a new
		/** 在包装BeanInfo对象,复制任何现有PropertyDescriptor或创建一个新的 */
		// one for any non-standard setter methods found.
		/** 发现的任何非标准的setter方法.*/

		ALL_METHODS:
		for (MethodDescriptor md : delegate.getMethodDescriptors()) {
			Method method = md.getMethod();

			// bypass non-getter java.lang.Class methods for efficiency
			/** 绕过non-getter java.lang.Class 方法的效率 */
			if (ReflectionUtils.isObjectMethod(method) && !method.getName().startsWith("get")) {
				continue ALL_METHODS;
			}

			// is the method a NON-INDEXED setter? ignore return type in order to capture non-void signatures
			/** 物价setter方法吗?忽略为了捕捉非void返回类型签名 */
			if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
				String propertyName = propertyNameFor(method);
				if(propertyName.length() == 0) {
					continue ALL_METHODS;
				}
				for (PropertyDescriptor pd : delegate.getPropertyDescriptors()) {
					Method readMethod = pd.getReadMethod();
					Method writeMethod = pd.getWriteMethod();
					// has the setter already been found by the wrapped BeanInfo?
					/** setter已经发现了包装BeanInfo吗? */
					if (writeMethod != null
							&& writeMethod.getName().equals(method.getName())) {
						// yes -> copy it, including corresponding getter method (if any -- may be null)
						/** 是的- >复制,包括相应的getter方法(如果有的话,可能是null) */
						this.addOrUpdatePropertyDescriptor(pd, propertyName, readMethod, writeMethod);
						continue ALL_METHODS;
					}
					// has a getter corresponding to this setter already been found by the wrapped BeanInfo?
					/** 有一个相应的getter setter已经发现的包裹BeanInfo吗? */
					if (readMethod != null
							&& readMethod.getName().equals(getterMethodNameFor(propertyName))
							&& readMethod.getReturnType().equals(method.getParameterTypes()[0])) {
						this.addOrUpdatePropertyDescriptor(pd, propertyName, readMethod, method);
						continue ALL_METHODS;
					}
				}
				// the setter method was not found by the wrapped BeanInfo -> add a new PropertyDescriptor for it
				/** 包装的setter方法不存在BeanInfo -> 添加一个新的PropertyDescriptor */
				// no corresponding getter was detected, so the 'read method' parameter is null.
				/** 没有检测到相应的getter,阅读方法的参数是null. */
				this.addOrUpdatePropertyDescriptor(null, propertyName, null, method);
				continue ALL_METHODS;
			}

			// is the method an INDEXED setter? ignore return type in order to capture non-void signatures
			/** 索引setter方法吗?忽略为了捕捉非void返回类型签名 */
			if (method.getName().startsWith("set") && method.getParameterTypes().length == 2 && method.getParameterTypes()[0].equals(int.class)) {
				String propertyName = propertyNameFor(method);
				if(propertyName.length() == 0) {
					continue ALL_METHODS;
				}
				DELEGATE_PD:
				for (PropertyDescriptor pd : delegate.getPropertyDescriptors()) {
					if (!(pd instanceof IndexedPropertyDescriptor)) {
						continue DELEGATE_PD;
					}
					IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor) pd;
					Method readMethod = ipd.getReadMethod();
					Method writeMethod = ipd.getWriteMethod();
					Method indexedReadMethod = ipd.getIndexedReadMethod();
					Method indexedWriteMethod = ipd.getIndexedWriteMethod();
					// has the setter already been found by the wrapped BeanInfo?
					/** setter已经发现了包装BeanInfo吗? */
					if (indexedWriteMethod != null
							&& indexedWriteMethod.getName().equals(method.getName())) {
						// yes -> copy it, including corresponding getter method (if any -- may be null)
						/**  是的 -> 复制,包括相应的getter方法(如果有的话,可能是null) */
						this.addOrUpdatePropertyDescriptor(pd, propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
						continue ALL_METHODS;
					}
					// has a getter corresponding to this setter already been found by the wrapped BeanInfo?
					/** 有一个相应的getter setter已经发现的包裹BeanInfo吗? */
					if (indexedReadMethod != null
							&& indexedReadMethod.getName().equals(getterMethodNameFor(propertyName))
							&& indexedReadMethod.getReturnType().equals(method.getParameterTypes()[1])) {
						this.addOrUpdatePropertyDescriptor(pd, propertyName, readMethod, writeMethod, indexedReadMethod, method);
						continue ALL_METHODS;
					}
				}
				// the INDEXED setter method was not found by the wrapped BeanInfo -> add a new PropertyDescriptor
				/** 未找到索引setter方法的包装BeanInfo -> 添加一个新的PropertyDescriptor */
				// for it. no corresponding INDEXED getter was detected, so the 'indexed read method' parameter is null.
				/** 为它.没有检测到相应的索引getter,索引读方法的参数是null. */
				this.addOrUpdatePropertyDescriptor(null, propertyName, null, null, null, method);
				continue ALL_METHODS;
			}

			// the method is not a setter, but is it a getter?
			/** 不是一个setter方法,但它是一个getter吗? */
			for (PropertyDescriptor pd : delegate.getPropertyDescriptors()) {
				// have we already copied this read method to a property descriptor locally?
				/** 我们已经复制这种阅读方法在本地属性描述符? */
				for (PropertyDescriptor existingPD : this.propertyDescriptors) {
					if (method.equals(pd.getReadMethod())
							&& existingPD.getName().equals(pd.getName())) {
						if (existingPD.getReadMethod() == null) {
							// no -> add it now
							/** 现在没有- >添加*/
							this.addOrUpdatePropertyDescriptor(pd, pd.getName(), method, pd.getWriteMethod());
						}
						// yes -> do not add a duplicate
						/** 是的 -> 不要重复添加 */
						continue ALL_METHODS;
					}
				}
				if (method == pd.getReadMethod()
						|| (pd instanceof IndexedPropertyDescriptor && method == ((IndexedPropertyDescriptor) pd).getIndexedReadMethod())) {
					// yes -> copy it, including corresponding setter method (if any -- may be null)
					/** 是的 -> 复制,包括相应的setter方法(如果有的话,可能是null) */
					if (pd instanceof IndexedPropertyDescriptor) {
						this.addOrUpdatePropertyDescriptor(pd, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(), ((IndexedPropertyDescriptor)pd).getIndexedReadMethod(), ((IndexedPropertyDescriptor)pd).getIndexedWriteMethod());
					} else {
						this.addOrUpdatePropertyDescriptor(pd, pd.getName(), pd.getReadMethod(), pd.getWriteMethod());
					}
					continue ALL_METHODS;
				}
			}
		}
	}

	private void addOrUpdatePropertyDescriptor(PropertyDescriptor pd, String propertyName, Method readMethod, Method writeMethod) throws IntrospectionException {
		addOrUpdatePropertyDescriptor(pd, propertyName, readMethod, writeMethod, null, null);
	}

	private void addOrUpdatePropertyDescriptor(PropertyDescriptor pd, String propertyName, Method readMethod, Method writeMethod, Method indexedReadMethod, Method indexedWriteMethod) throws IntrospectionException {
		Assert.notNull(propertyName, "propertyName may not be null");
		propertyName = pd == null ? propertyName : pd.getName();
		for (PropertyDescriptor existingPD : this.propertyDescriptors) {
			if (existingPD.getName().equals(propertyName)) {
				// is there already a descriptor that captures this read method or its corresponding write method?
				/** 已经有一个描述符,抓住这种阅读方法或其相应的写作方法? */
				if (existingPD.getReadMethod() != null) {
					if (readMethod != null && existingPD.getReadMethod().getReturnType() != readMethod.getReturnType()
							|| writeMethod != null && existingPD.getReadMethod().getReturnType() != writeMethod.getParameterTypes()[0]) {
						// no -> add a new descriptor for it below
						/** 没有- >添加一个新的描述符*/
						break;
					}
				}
				// update the existing descriptor's read method
				/** 更新现有的描述符的阅读方法 */
				if (readMethod != null) {
					try {
						existingPD.setReadMethod(readMethod);
					} catch (IntrospectionException ex) {
						// there is a conflicting setter method present -> null it out and try again
						/** 现在是一个矛盾的setter方法 -> 空出来,再试一次 */
						existingPD.setWriteMethod(null);
						existingPD.setReadMethod(readMethod);
					}
				}

				// is there already a descriptor that captures this write method or its corresponding read method?
				/** 已经有一个描述符,抓住这写方法或其相应的阅读方法? */
				if (existingPD.getWriteMethod() != null) {
					if (readMethod != null && existingPD.getWriteMethod().getParameterTypes()[0] != readMethod.getReturnType()
							|| writeMethod != null && existingPD.getWriteMethod().getParameterTypes()[0] != writeMethod.getParameterTypes()[0]) {
						// no -> add a new descriptor for it below
						/** 没有- >添加一个新的描述符 */
						break;
					}
				}
				// update the existing descriptor's write method
				/** 更新现有的描述符的编写方法 */
				if (writeMethod != null) {
					existingPD.setWriteMethod(writeMethod);
				}

				// is this descriptor indexed?
				/** 这是描述符索引吗? */
				if (existingPD instanceof IndexedPropertyDescriptor) {
					IndexedPropertyDescriptor existingIPD = (IndexedPropertyDescriptor) existingPD;

					// is there already a descriptor that captures this indexed read method or its corresponding indexed write method?
					/** 已经有一个描述符,抓住这个索引读方法或相应的索引写方法? */
					if (existingIPD.getIndexedReadMethod() != null) {
						if (indexedReadMethod != null && existingIPD.getIndexedReadMethod().getReturnType() != indexedReadMethod.getReturnType()
								|| indexedWriteMethod != null && existingIPD.getIndexedReadMethod().getReturnType() != indexedWriteMethod.getParameterTypes()[1]) {
							// no -> add a new descriptor for it below
							/** 没有 -> 添加一个新的描述符 */
							break;
						}
					}
					// update the existing descriptor's indexed read method
					/** 更新现有的描述符的索引读取方法 */
					try {
						if (indexedReadMethod != null) {
							existingIPD.setIndexedReadMethod(indexedReadMethod);
						}
					} catch (IntrospectionException ex) {
						// there is a conflicting indexed setter method present -> null it out and try again
						/** 有一个冲突索引setter方法存在 -> 空出来,再试一次 */
						existingIPD.setIndexedWriteMethod(null);
						existingIPD.setIndexedReadMethod(indexedReadMethod);
					}

					// is there already a descriptor that captures this indexed write method or its corresponding indexed read method?
					/** 已经有一个描述符,抓住这个索引写方法或相应的索引读方法? */
					if (existingIPD.getIndexedWriteMethod() != null) {
						if (indexedReadMethod != null && existingIPD.getIndexedWriteMethod().getParameterTypes()[1] != indexedReadMethod.getReturnType()
								|| indexedWriteMethod != null && existingIPD.getIndexedWriteMethod().getParameterTypes()[1] != indexedWriteMethod.getParameterTypes()[1]) {
							// no -> add a new descriptor for it below
							/** 没有 -> 添加一个新的描述符 */
							break;
						}
					}
					// update the existing descriptor's indexed write method
					/** 更新现有的描述符的索引写方法 */
					if (indexedWriteMethod != null) {
						existingIPD.setIndexedWriteMethod(indexedWriteMethod);
					}
				}

				// the descriptor has been updated -> return immediately
				/** 描述符已经更新- >立即返回*/
				return;
			}
		}

		// we haven't yet seen read or write methods for this property -> add a new descriptor
		/** 我们还没有看到这个属性- >读或写方法添加一个新的描述符 */
		if (pd == null) {
			try {
				if (indexedReadMethod == null && indexedWriteMethod == null) {
					pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
				}
				else {
					pd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
				}
				this.propertyDescriptors.add(pd);
			} catch (IntrospectionException ex) {
				logger.warn(format("Could not create new PropertyDescriptor for readMethod [%s] writeMethod [%s] " +
						"indexedReadMethod [%s] indexedWriteMethod [%s] for property [%s]. Reason: %s",
						readMethod, writeMethod, indexedReadMethod, indexedWriteMethod, propertyName, ex.getMessage()));
				// suppress exception and attempt to continue
			}
		}
		else {
			pd.setReadMethod(readMethod);
			try {
				pd.setWriteMethod(writeMethod);
			} catch (IntrospectionException ex) {
				logger.warn(format("Could not add write method [%s] for property [%s]. Reason: %s",
						writeMethod, propertyName, ex.getMessage()));
				// fall through -> add property descriptor as best we can
			}
			this.propertyDescriptors.add(pd);
		}
	}

	private String propertyNameFor(Method method) {
		return Introspector.decapitalize(method.getName().substring(3,method.getName().length()));
	}

	private Object getterMethodNameFor(String name) {
		return "get" + StringUtils.capitalize(name);
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return delegate.getAdditionalBeanInfo();
	}

	public BeanDescriptor getBeanDescriptor() {
		return delegate.getBeanDescriptor();
	}

	public int getDefaultEventIndex() {
		return delegate.getDefaultEventIndex();
	}

	public int getDefaultPropertyIndex() {
		return delegate.getDefaultPropertyIndex();
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return delegate.getEventSetDescriptors();
	}

	public Image getIcon(int arg0) {
		return delegate.getIcon(arg0);
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return delegate.getMethodDescriptors();
	}

	/**
	 * Return the set of {@link PropertyDescriptor}s from the wrapped {@link BeanInfo}
	 * object as well as {@code PropertyDescriptor}s for each non-void returning setter
	 * method found during construction.
	 * ********************************************************************************
	 * ~$ 返回的集合{@link PropertyDescriptor }年代从包装{@link BeanInfo }对象
	 *    以及{@code PropertyDescriptor }年代为每一个非void的返回setter方法发现在施工期间.
	 * @see #ExtendedBeanInfo(BeanInfo)
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		return this.propertyDescriptors.toArray(new PropertyDescriptor[this.propertyDescriptors.size()]);
	}


	/**
	 * Sorts PropertyDescriptor instances alphanumerically to emulate the behavior of {@link BeanInfo#getPropertyDescriptors()}.
	 * ************************************************************************************************************************
	 * ~$ 各种PropertyDescriptor实例字母数字混合仿真的行为{@link BeanInfo#getPropertyDescriptors()}.
	 * @see ExtendedBeanInfo#propertyDescriptors
	 */
	static class PropertyDescriptorComparator implements Comparator<PropertyDescriptor> {
		public int compare(PropertyDescriptor desc1, PropertyDescriptor desc2) {
			String left = desc1.getName();
			String right = desc2.getName();
			for (int i = 0; i < left.length(); i++) {
				if (right.length() == i) {
					return 1;
				}
				int result = left.getBytes()[i] - right.getBytes()[i];
				if (result != 0) {
					return result;
				}
			}
			return left.length() - right.length();
		}
	}
}