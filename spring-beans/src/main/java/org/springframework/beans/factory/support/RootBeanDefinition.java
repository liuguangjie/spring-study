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

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.util.Assert;

/**
 * A root bean definition represents the merged bean definition that backs
 * a specific bean in a Spring BeanFactory at runtime. It might have been created
 * from multiple original bean definitions that inherit from each other,
 * typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 *
 * <p>Root bean definitions may also be used for registering individual bean definitions
 * in the configuration phase. However, since Spring 2.5, the preferred way to register
 * bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition has the advantage that it allows to dynamically define
 * parent dependencies, not 'hard-coding' the role as a root bean definition.
 * *************************************************************************************
 * ~$ 根bean定义表示支持一个特定的合并后的bean定义bean Spring BeanFactory在运行时.
 * 	  它可能来自多个原始的创建bean定义继承对方,通常注册为{@link GenericBeanDefinition GenericBeanDefinitions}.
 * 	  根bean定义实质上是“统一”在运行时视图bean定义
 *
 * <p>根bean定义也可以用于注册个人bean定义在配置阶段.
 * 	  然而,由于Spring 2.5中,注册的首选bean定义编程是{@link GenericBeanDefinition }类
 * 	  GenericBeanDefinition的优点是它允许动态地定义父依赖关系,不是“硬编码”作为根bean定义
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
public class RootBeanDefinition extends AbstractBeanDefinition {

	private final Set<Member> externallyManagedConfigMembers = Collections.synchronizedSet(new HashSet<Member>(0));

	private final Set<String> externallyManagedInitMethods = Collections.synchronizedSet(new HashSet<String>(0));

	private final Set<String> externallyManagedDestroyMethods = Collections.synchronizedSet(new HashSet<String>(0));

	private BeanDefinitionHolder decoratedDefinition;

	boolean isFactoryMethodUnique;

	/** Package-visible field for caching the resolved constructor or factory method */
	/** Package-visible字段缓存解决构造函数或工厂方法*/
	Object resolvedConstructorOrFactoryMethod;

	/** Package-visible field that marks the constructor arguments as resolved */
	/** Package-visible标志着构造函数参数作为解决的领域 */
	boolean constructorArgumentsResolved = false;

	/** Package-visible field for caching fully resolved constructor arguments */
	/** Package-visible字段缓存完全解决构造函数参数 */
	Object[] resolvedConstructorArguments;

	/** Package-visible field for caching partly prepared constructor arguments */
	/** Package-visible字段缓存部分准备的构造函数参数 */
	Object[] preparedConstructorArguments;

	final Object constructorArgumentLock = new Object();

	/** Package-visible field that indicates a before-instantiation post-processor having kicked in */
	/** Package-visible字段表明before-instantiation后处理器在踢 */
	volatile Boolean beforeInstantiationResolved;

	/** Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied */
	/** Package-visible字段表明MergedBeanDefinitionPostProcessor应用 */
	boolean postProcessed = false;

	final Object postProcessingLock = new Object();


	/**
	 * Create a new RootBeanDefinition, to be configured through its bean
	 * properties and configuration methods.
	 * ******************************************************************
	 * ~$ 创建一个新的RootBeanDefinition,通过其配置bean属性和配置方法
	 * @see #setBeanClass
	 * @see #setBeanClassName
	 * @see #setScope
	 * @see #setAutowireMode
	 * @see #setDependencyCheck
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public RootBeanDefinition() {
		super();
	}

	/**
	 * Create a new RootBeanDefinition for a singleton.
	 * ************************************************
	 * ~$ 创建一个新的RootBeanDefinition singleton
	 * @param beanClass the class of the bean to instantiate
	 */
	public RootBeanDefinition(Class beanClass) {
		super();
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition with the given singleton status.
	 * ****************************************************************
	 * ~$ 创建一个新的RootBeanDefinition用给定的独立地位
	 * @param beanClass the class of the bean to instantiate
	 *                  ~$ 要实例化bean的类
	 * @param singleton the singleton status of the bean
	 *                  ~$ 单例bean的状态
	 * @deprecated since Spring 2.5, in favor of {@link #setScope}
	 */
	@Deprecated
	public RootBeanDefinition(Class beanClass, boolean singleton) {
		super();
		setBeanClass(beanClass);
		setSingleton(singleton);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * using the given autowire mode.
	 * ************************************************
	 * ~$ 创建一个新的RootBeanDefinition单,
	 * 	  使用给定的自动装配模式
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @deprecated as of Spring 3.0, in favor of {@link #setAutowireMode} usage
	 */
	@Deprecated
	public RootBeanDefinition(Class beanClass, int autowireMode) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * using the given autowire mode.
	 * ************************************************
	 * ~$ 创建一个新的RootBeanDefinition单,
	 *    使用给定的自动装配模式。
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 * (not applicable to autowiring a constructor, thus ignored there)
	 */
	public RootBeanDefinition(Class beanClass, int autowireMode, boolean dependencyCheck) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
		if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
			setDependencyCheck(RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS);
		}
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing property values.
	 * ************************************************
	 * ~$ 创建一个新的RootBeanDefinition单,
	 * 	  提供属性值
	 * @param beanClass the class of the bean to instantiate
	 * @param pvs the property values to apply
	 * @deprecated as of Spring 3.0, in favor of {@link #getPropertyValues} usage
	 */
	@Deprecated
	public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs) {
		super(null, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition with the given singleton status,
	 * providing property values.
	 * ****************************************************************
	 * ~$ 用给定的单例创建一个新的RootBeanDefinition状态,提供属性值
	 * @param beanClass the class of the bean to instantiate
	 * @param pvs the property values to apply
	 * @param singleton the singleton status of the bean
	 * @deprecated since Spring 2.5, in favor of {@link #setScope}
	 */
	@Deprecated
	public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs, boolean singleton) {
		super(null, pvs);
		setBeanClass(beanClass);
		setSingleton(singleton);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * ****************************************************
	 * ~$ 创建一个新的RootBeanDefinition单,
	 *    提供构造函数参数和属性值。
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(Class beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * ****************************************************
	 * ~$ 创建一个新的RootBeanDefinition单,
	 *    提供构造函数参数和属性值。
	 * <P> 需要一个bean类名称,以避免立即加载bean类。
	 * @param beanClassName the name of the class to instantiate
	 */
	public RootBeanDefinition(String beanClassName) {
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * *********************************************************
	 * ~$ 创建一个新的RootBeanDefinition深拷贝的bean定义
	 * @param original the original bean definition to copy from
	 */
	public RootBeanDefinition(RootBeanDefinition original) {
		this((BeanDefinition) original);
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * *********************************************************
	 * ~$ 创建一个新的RootBeanDefinition深拷贝的bean定义
	 * @param original the original bean definition to copy from
	 */
	RootBeanDefinition(BeanDefinition original) {
		super(original);
		if (original instanceof RootBeanDefinition) {
			RootBeanDefinition originalRbd = (RootBeanDefinition) original;
			this.decoratedDefinition = originalRbd.decoratedDefinition;
			this.isFactoryMethodUnique = originalRbd.isFactoryMethodUnique;
		}
	}


	public String getParentName() {
		return null;
	}

	public void setParentName(String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
		}
	}

	/**
	 * Specify a factory method name that refers to a non-overloaded method.
	 * ********************************************************************
	 * ~$ 指定一个工厂方法的名称,是指一个non-overloaded方法
	 */
	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		this.isFactoryMethodUnique = true;
	}

	/**
	 * Check whether the given candidate qualifies as a factory method.
	 * ********************************************************************
     * ~$ 检查给定的候选人是否有资格作为一个工厂方法
	 */
	public boolean isFactoryMethod(Method candidate) {
		return (candidate != null && candidate.getName().equals(getFactoryMethodName()));
	}

	/**
	 * Return the resolved factory method as a Java Method object, if available.
	 * **************************************************************************
	 * ~$ 返回解析后的工厂方法作为Java对象方法,如果可用
	 * @return the factory method, or <code>null</code> if not found or not resolved yet
	 */
	public Method getResolvedFactoryMethod() {
		synchronized (this.constructorArgumentLock) {
			Object candidate = this.resolvedConstructorOrFactoryMethod;
			return (candidate instanceof Method ? (Method) candidate : null);
		}
	}


	public void registerExternallyManagedConfigMember(Member configMember) {
		this.externallyManagedConfigMembers.add(configMember);
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		return this.externallyManagedConfigMembers.contains(configMember);
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		this.externallyManagedInitMethods.add(initMethod);
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		return this.externallyManagedInitMethods.contains(initMethod);
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		this.externallyManagedDestroyMethods.add(destroyMethod);
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		return this.externallyManagedDestroyMethods.contains(destroyMethod);
	}

	public void setDecoratedDefinition(BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	public BeanDefinitionHolder getDecoratedDefinition() {
		return this.decoratedDefinition;
	}


	@Override
	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Root bean: " + super.toString();
	}

}
