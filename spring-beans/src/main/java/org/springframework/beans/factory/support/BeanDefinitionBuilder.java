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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.ObjectUtils;

/**
 * Programmatic means of constructing
 * {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinitions}
 * using the builder pattern. Intended primarily for use when implementing Spring 2.0
 * {@link org.springframework.beans.factory.xml.NamespaceHandler NamespaceHandlers}.
 * **********************************************************************************
 * ~$ 编程的方法构建{@link org.springframework.beans.factory.config.BeanDefinition BeanDefinition }使用生成器模式.
 * 目的主要是为实现时使用Spring 2.0 {@link org.springframework.beans.factory.xml.NamespaceHandler NamespaceHandlers }.
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class BeanDefinitionBuilder  {

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link GenericBeanDefinition}.
	 * **************************************************************************************************
	 * ~$ 创建一个新的BeanDefinitionBuilder用来构造一个{@link GenericBeanDefinition }
	 */
	public static BeanDefinitionBuilder genericBeanDefinition() {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new GenericBeanDefinition();
		return builder;
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link GenericBeanDefinition}.
	 * **************************************************************************************************
	 * ~$ 创建一个新的BeanDefinitionBuilder用来构造一个{@link GenericBeanDefinition }
	 * @param beanClass the <code>Class</code> of the bean that the definition is being created for
	 *                  ~$类被创建的bean定义
	 */
	public static BeanDefinitionBuilder genericBeanDefinition(Class beanClass) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new GenericBeanDefinition();
		builder.beanDefinition.setBeanClass(beanClass);
		return builder;
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link GenericBeanDefinition}.
	 * @param beanClassName the class name for the bean that the definition is being created for
	 *                      ~$类名创建的bean定义
	 */
	public static BeanDefinitionBuilder genericBeanDefinition(String beanClassName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new GenericBeanDefinition();
		builder.beanDefinition.setBeanClassName(beanClassName);
		return builder;
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link RootBeanDefinition}.
	 * @param beanClass the <code>Class</code> of the bean that the definition is being created for
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(Class beanClass) {
		return rootBeanDefinition(beanClass, null);
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link RootBeanDefinition}.
	 * @param beanClass the <code>Class</code> of the bean that the definition is being created for
	 * @param factoryMethodName the name of the method to use to construct the bean instance
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(Class beanClass, String factoryMethodName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new RootBeanDefinition();
		builder.beanDefinition.setBeanClass(beanClass);
		builder.beanDefinition.setFactoryMethodName(factoryMethodName);
		return builder;
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link RootBeanDefinition}.
	 * @param beanClassName the class name for the bean that the definition is being created for
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName) {
		return rootBeanDefinition(beanClassName, null);
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link RootBeanDefinition}.
	 * @param beanClassName the class name for the bean that the definition is being created for
	 * @param factoryMethodName the name of the method to use to construct the bean instance
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName, String factoryMethodName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new RootBeanDefinition();
		builder.beanDefinition.setBeanClassName(beanClassName);
		builder.beanDefinition.setFactoryMethodName(factoryMethodName);
		return builder;
	}

	/**
	 * Create a new <code>BeanDefinitionBuilder</code> used to construct a {@link ChildBeanDefinition}.
	 * @param parentName the name of the parent bean
	 */
	public static BeanDefinitionBuilder childBeanDefinition(String parentName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new ChildBeanDefinition(parentName);
		return builder;
	}


	/**
	 * The <code>BeanDefinition</code> instance we are creating.
	 * *********************************************************
	 * ~$ 我们正在创造BeanDefinition实例.
	 */
	private AbstractBeanDefinition beanDefinition;

	/**
	 * Our current position with respect to constructor args.
	 * ******************************************************
	 * ~$ 我们目前的位置对构造函数参数.
	 */
	private int constructorArgIndex;


	/**
	 * Enforce the use of factory methods.
	 * ************************************
	 * ~$ 执行工厂方法的使用.
	 */
	private BeanDefinitionBuilder() {
	}

	/**
	 * Return the current BeanDefinition object in its raw (unvalidated) form.
	 * ***********************************************************************
	 * ~$ 返回当前BeanDefinition对象在其原始形式(用户).
	 * @see #getBeanDefinition()
	 */
	public AbstractBeanDefinition getRawBeanDefinition() {
		return this.beanDefinition;
	}

	/**
	 * Validate and return the created BeanDefinition object.
	 * ******************************************************
	 * ~$ 验证并返回BeanDefinition创建对象.
	 */
	public AbstractBeanDefinition getBeanDefinition() {
		this.beanDefinition.validate();
		return this.beanDefinition;
	}


	/**
	 * Set the name of the parent definition of this bean definition.
	 * **************************************************************
	 * ~$ 设置父的名称定义的bean定义.
	 */
	public BeanDefinitionBuilder setParentName(String parentName) {
		this.beanDefinition.setParentName(parentName);
		return this;
	}

	/**
	 * Set the name of the factory method to use for this definition.
	 * **************************************************************
	 * ~$ 工厂方法的名称设置为使用这个定义.
	 */
	public BeanDefinitionBuilder setFactoryMethod(String factoryMethod) {
		this.beanDefinition.setFactoryMethodName(factoryMethod);
		return this;
	}

	/**
	 * Set the name of the factory bean to use for this definition.
	 * @deprecated since Spring 2.5, in favor of preparing this on the
	 * {@link #getRawBeanDefinition() raw BeanDefinition object}
	 * ***************************************************************
	 * ~$ 工厂bean的名称设置为使用这个定义.
	 *    @deprecated Spring 2.5以来,赞成在做准备
	 *    {@link #getRawBeanDefinition() raw BeanDefinition object}
	 */
	@Deprecated
	public BeanDefinitionBuilder setFactoryBean(String factoryBean, String factoryMethod) {
		this.beanDefinition.setFactoryBeanName(factoryBean);
		this.beanDefinition.setFactoryMethodName(factoryMethod);
		return this;
	}

	/**
	 * Add an indexed constructor arg value. The current index is tracked internally
	 * and all additions are at the present point.
	 * @deprecated since Spring 2.5, in favor of {@link #addConstructorArgValue}
	 * ******************************************************************************
	 * ~$ 添加一个索引构造函数参数值.当前指数跟踪内部和目前所有添加点.
	 *    @deprecated Spring 2.5以来,赞成{@link #addConstructorArgValue }
	 */
	@Deprecated
	public BeanDefinitionBuilder addConstructorArg(Object value) {
		return addConstructorArgValue(value);
	}

	/**
	 * Add an indexed constructor arg value. The current index is tracked internally
	 * and all additions are at the present point.
	 * *****************************************************************************
	 * ~$ 添加一个索引构造函数参数值.当前指数跟踪内部和目前所有添加点.
	 */
	public BeanDefinitionBuilder addConstructorArgValue(Object value) {
		this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(
				this.constructorArgIndex++, value);
		return this;
	}

	/**
	 * Add a reference to a named bean as a constructor arg.
	 * *****************************************************
	 * ~$ 添加一个名为bean的引用作为构造函数参数.
	 * @see #addConstructorArgValue(Object)
	 */
	public BeanDefinitionBuilder addConstructorArgReference(String beanName) {
		this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(
				this.constructorArgIndex++, new RuntimeBeanReference(beanName));
		return this;
	}

	/**
	 * Add the supplied property value under the given name.
	 * *****************************************************
	 * ~$ 提供添加属性值在给定的名称.
	 */
	public BeanDefinitionBuilder addPropertyValue(String name, Object value) {
		this.beanDefinition.getPropertyValues().add(name, value);
		return this;
	}

	/**
	 * Add a reference to the specified bean name under the property specified.
	 * ************************************************************************
	 * ~$ 将一个引用添加到指定bean名称在指定的属性.
	 * @param name the name of the property to add the reference to
	 * @param beanName the name of the bean being referenced
	 */
	public BeanDefinitionBuilder addPropertyReference(String name, String beanName) {
		this.beanDefinition.getPropertyValues().add(name, new RuntimeBeanReference(beanName));
		return this;
	}

	/**
	 * Set the init method for this definition.
	 * ****************************************
	 * ~$ 这个定义的init方法.
	 */
	public BeanDefinitionBuilder setInitMethodName(String methodName) {
		this.beanDefinition.setInitMethodName(methodName);
		return this;
	}

	/**
	 * Set the destroy method for this definition.
	 * *******************************************
	 * ~$ 设置这个定义的销毁方法.
	 */
	public BeanDefinitionBuilder setDestroyMethodName(String methodName) {
		this.beanDefinition.setDestroyMethodName(methodName);
		return this;
	}


	/**
	 * Set the scope of this definition.
	 * ***********************************
	 * 设置范围定义.
	 * @see org.springframework.beans.factory.config.BeanDefinition#SCOPE_SINGLETON
	 * @see org.springframework.beans.factory.config.BeanDefinition#SCOPE_PROTOTYPE
	 */
	public BeanDefinitionBuilder setScope(String scope) {
		this.beanDefinition.setScope(scope);
		return this;
	}

	/**
	 * Set whether or not this definition describes a singleton bean,
	 * as alternative to {@link #setScope}.
	 * @deprecated since Spring 2.5, in favor of {@link #setScope}
	 * *************************************************************
	 * ~$ 设置是否这个定义描述了一个单例bean,替代{@link #setScope }.
	 *    @deprecated Spring 2.5以来,赞成{@link #setScope }
	 */
	@Deprecated
	public BeanDefinitionBuilder setSingleton(boolean singleton) {
		this.beanDefinition.setSingleton(singleton);
		return this;
	}

	/**
	 * Set whether or not this definition is abstract.
	 * ***********************************************
	 * ~$ 设置是否这个定义是抽象的.
	 */
	public BeanDefinitionBuilder setAbstract(boolean flag) {
		this.beanDefinition.setAbstract(flag);
		return this;
	}

	/**
	 * Set whether beans for this definition should be lazily initialized or not.
	 * **************************************************************************
	 * ~$ 为这个定义bean是否应该延迟初始化.
	 */
	public BeanDefinitionBuilder setLazyInit(boolean lazy) {
		this.beanDefinition.setLazyInit(lazy);
		return this;
	}

	/**
	 * Set the autowire mode for this definition.
	 * ******************************************
	 * ~$ 设置自动装配模式的定义.
	 */
	public BeanDefinitionBuilder setAutowireMode(int autowireMode) {
		beanDefinition.setAutowireMode(autowireMode);
		return this;
	}

	/**
	 * Set the depency check mode for this definition.
	 * ***********************************************
	 * ~$ 这个定义设置depency检查模式.
	 */
	public BeanDefinitionBuilder setDependencyCheck(int dependencyCheck) {
		beanDefinition.setDependencyCheck(dependencyCheck);
		return this;
	}

	/**
	 * Append the specified bean name to the list of beans that this definition
	 * depends on.
	 * *************************************************************************
	 * ~$ 将指定的bean的名称附加到bean,这个定义取决于列表.
	 */
	public BeanDefinitionBuilder addDependsOn(String beanName) {
		if (this.beanDefinition.getDependsOn() == null) {
			this.beanDefinition.setDependsOn(new String[] {beanName});
		}
		else {
			String[] added = ObjectUtils.addObjectToArray(this.beanDefinition.getDependsOn(), beanName);
			this.beanDefinition.setDependsOn(added);
		}
		return this;
	}

	/**
	 * Set the role of this definition.
	 * ********************************
	 * ~$ 这个定义的角色.
	 */
	public BeanDefinitionBuilder setRole(int role) {
		this.beanDefinition.setRole(role);
		return this;
	}

	/**
	 * Set the source of this definition.
	 * @deprecated since Spring 2.5, in favor of preparing this on the
	 * {@link #getRawBeanDefinition() raw BeanDefinition object}
	 * ****************************************************************
	 * ~$ 这个定义的来源.@deprecated Spring 2.5以来,赞成准备在
	 * {@link #getRawBeanDefinition() raw BeanDefinition object}
	 */
	@Deprecated
	public BeanDefinitionBuilder setSource(Object source) {
		this.beanDefinition.setSource(source);
		return this;
	}

	/**
	 * Set the description associated with this definition.
	 * @deprecated since Spring 2.5, in favor of preparing this on the
	 * {@link #getRawBeanDefinition() raw BeanDefinition object}
	 * *****************************************************************
	 * ~$ 设置描述与此相关定义.
	 */
	@Deprecated
	public BeanDefinitionBuilder setResourceDescription(String resourceDescription) {
		this.beanDefinition.setResourceDescription(resourceDescription);
		return this;
	}

}
