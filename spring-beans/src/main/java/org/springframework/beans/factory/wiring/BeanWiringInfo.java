/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.beans.factory.wiring;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/**
 * Holder for bean wiring metadata information about a particular class. Used in
 * conjunction with the {@link org.springframework.beans.factory.annotation.Configurable}
 * annotation and the AspectJ <code>AnnotationBeanConfigurerAspect</code>.
 * **************************************************************************************
 * ~$保持者bean连接一个特定类的元数据信息.
 * 结合使用{@link org.springframework.beans.factory.annotation.Configurable}
 * 注释和AspectJ AnnotationBeanConfigurerAspect.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWiringInfoResolver
 * @see AutowireCapableBeanFactory
 * @see org.springframework.beans.factory.annotation.Configurable
 */
public class BeanWiringInfo {

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * ***********************************************************
	 * ~$常数表明自动装配bean属性的名字.
	 * @see #BeanWiringInfo(int, boolean)
	 * @see AutowireCapableBeanFactory#AUTOWIRE_BY_NAME
	 */
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * ***********************************************************
	 * ~$常数表明自动装配bean属性的类型.
	 * @see #BeanWiringInfo(int, boolean)
	 * @see AutowireCapableBeanFactory#AUTOWIRE_BY_TYPE
	 */
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;


	private String beanName = null;

	private boolean isDefaultBeanName = false;

	private int autowireMode = AutowireCapableBeanFactory.AUTOWIRE_NO;

	private boolean dependencyCheck = false;


	/**
	 * Create a default BeanWiringInfo that suggests plain initialization of
	 * factory and post-processor callbacks that the bean class may expect.
	 * *********************************************************************
	 * ~$创建一个默认BeanWiringInfo表明普通初始化bean类的工厂和后处理器回调可能期望.
	 */
	public BeanWiringInfo() {
	}

	/**
	 * Create a new BeanWiringInfo that points to the given bean name.
	 * ***************************************************************
	 * ~$创建一个新的BeanWiringInfo指向给定的bean的名称.
	 * @param beanName the name of the bean definition to take the property values from
	 * @throws IllegalArgumentException if the supplied beanName is <code>null</code>,
	 * is empty, or consists wholly of whitespace
	 */
	public BeanWiringInfo(String beanName) {
		this(beanName, false);
	}

	/**
	 * Create a new BeanWiringInfo that points to the given bean name.
	 * ***************************************************************
	 * ~$创建一个新的BeanWiringInfo指向给定的bean的名称.
	 * @param beanName the name of the bean definition to take the property values from
	 *                 ~$bean的名称定义的属性值
	 * @param isDefaultBeanName whether the given bean name is a suggested
	 * default bean name, not necessarily matching an actual bean definition
	 *                          ~$给定的bean名称是否建议默认bean的名字,不一定匹配实际的bean定义
	 * @throws IllegalArgumentException if the supplied beanName is <code>null</code>,
	 * is empty, or consists wholly of whitespace
	 */
	public BeanWiringInfo(String beanName, boolean isDefaultBeanName) {
		Assert.hasText(beanName, "'beanName' must not be empty");
		this.beanName = beanName;
		this.isDefaultBeanName = isDefaultBeanName;
	}

	/**
	 * Create a new BeanWiringInfo that indicates autowiring.
	 * ******************************************************
	 * ~$ 创建一个新的BeanWiringInfo表明自动装配.
	 * @param autowireMode one of the constants {@link #AUTOWIRE_BY_NAME} /
	 * {@link #AUTOWIRE_BY_TYPE}
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance (after autowiring)
	 *                        ~$ 是否执行依赖项检查的对象引用bean实例(自动装配之后)
	 * @throws IllegalArgumentException if the supplied <code>autowireMode</code>
	 * is not one of the allowed values
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 */
	public BeanWiringInfo(int autowireMode, boolean dependencyCheck) {
		if (autowireMode != AUTOWIRE_BY_NAME && autowireMode != AUTOWIRE_BY_TYPE) {
			throw new IllegalArgumentException("Only constants AUTOWIRE_BY_NAME and AUTOWIRE_BY_TYPE supported");
		}
		this.autowireMode = autowireMode;
		this.dependencyCheck = dependencyCheck;
	}


	/**
	 * Return whether this BeanWiringInfo indicates autowiring.
	 * ********************************************************
	 * ~$返回这个BeanWiringInfo是否显示自动装配.
	 */
	public boolean indicatesAutowiring() {
		return (this.beanName == null);
	}

	/**
	 * Return the specific bean name that this BeanWiringInfo points to, if any.
	 * *************************************************************************
	 * ~$ 返回特定bean名称BeanWiringInfo指出,如果有.
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return whether the specific bean name is a suggested default bean name,
	 * not necessarily matching an actual bean definition in the factory.
	 * ***********************************************************************
	 * ~$返回特定bean名称是否建议默认bean的名字,不一定匹配实际的bean定义在工厂.
	 */
	public boolean isDefaultBeanName() {
		return this.isDefaultBeanName;
	}

	/**
	 * Return one of the constants {@link #AUTOWIRE_BY_NAME} /
	 * {@link #AUTOWIRE_BY_TYPE}, if autowiring is indicated.
	 * ********************************************************
	 * ~$ 返回一个常量{@link #AUTOWIRE_BY_NAME }/{@link #AUTOWIRE_BY_TYPE },如果自动装配.
	 */
	public int getAutowireMode() {
		return this.autowireMode;
	}

	/**
	 * Return whether to perform a dependency check for object references
	 * in the bean instance (after autowiring).
	 * ******************************************************************
	 * ~$ 返回是否执行依赖项检查的对象引用bean实例(自动装配后).
	 */
	public boolean getDependencyCheck() {
		return this.dependencyCheck;
	}

}
