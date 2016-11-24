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

package org.springframework.beans.factory.config;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-instantiation callback,
 * and a callback after instantiation but before explicit properties are set or
 * autowiring occurs.
 *
 * <p>Typically used to suppress default instantiation for specific target beans,
 * for example to create proxies with special TargetSources (pooling targets,
 * lazily initializing targets, etc), or to implement additional injection strategies
 * such as field injection.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. It is recommended to implement the plain
 * {@link BeanPostProcessor} interface as far as possible, or to derive from
 * {@link InstantiationAwareBeanPostProcessorAdapter} in order to be shielded
 * from extensions to this interface.
 *
 * ***********************************************************************************
 * ~$ 子接口的{@link BeanPostProcessor },添加一个before-instantiation回调,和一个回调实例化后但在显式属性设置或自动装配。
 *
 * <p>通常用于抑制特定目标的默认实例化bean,例如创建代理与特殊TargetSources(池目标,懒洋洋地初始化目标,等),或实现额外注入策略等领域注入。
 *
 * <p>注:此接口是一个专用的接口,主要是框架内供内部使用.建议实现平原{@link BeanPostProcessor }界面尽可能,
 *    或来自{@link InstantiationAwareBeanPostProcessorAdapter }为了免受扩展这个接口.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor <i>before the target bean gets instantiated</i>.
	 * The returned bean object may be a proxy to use instead of the target bean,
	 * effectively suppressing default instantiation of the target bean.
	 * <p>If a non-null object is returned by this method, the bean creation process
	 * will be short-circuited. The only further processing applied is the
	 * {@link #postProcessAfterInitialization} callback from the configured
	 * {@link BeanPostProcessor BeanPostProcessors}.
	 * <p>This callback will only be applied to bean definitions with a bean class.
	 * In particular, it will not be applied to beans with a "factory-method".
	 * <p>Post-processors may implement the extended
	 * {@link SmartInstantiationAwareBeanPostProcessor} interface in order
	 * to predict the type of the bean object that they are going to return here.
	 * *****************************************************************************
	 * ~$ 应用这个BeanPostProcessor之前目标bean被实例化.返回的bean对象可能是一个代理bean使用而不是目标,有效地抑制默认目标bean的实例化.
	 * <p>如果这个方法返回的是一个非空对象,bean创建过程会短路.
	 *    唯一的进一步处理应用是{@link #postProcessAfterInitialization }从配置回调{@link BeanPostProcessor BeanPostProcessors }。
	 * <p>这个回调将仅适用于bean定义的bean类。特别是,它不会被应用到bean与一个"工厂方法".
	 * <p>后处理器可以实现扩展{@link SmartInstantiationAwareBeanPostProcessor }界面为了预测bean对象的类型,他们会返回这里。
	 *
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to expose instead of a default instance of the target bean,
	 * or <code>null</code> to proceed with default instantiation
	 * @throws BeansException in case of errors
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#hasBeanClass
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getFactoryMethodName
	 */
	Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

	/**
	 * Perform operations after the bean has been instantiated, via a constructor or factory method,
	 * but before Spring property population (from explicit properties or autowiring) occurs.
	 * <p>This is the ideal callback for performing field injection on the given bean instance.
	 * See Spring's own {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor}
	 * for a typical example.
	 * ***********************************************************************************************************
	 * ~$ bean实例化后执行操作,通过构造函数或工厂方法,但在春天之前人口属性(从显式属性或自动装配)发生.
	 * <p>这是理想的回调执行领域注入在给定的bean实例。看到春天的{@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor }的一个典型的例子.
	 * @param bean the bean instance created, with properties not having been set yet
	 *             ~$ 创建的bean实例,属性没有被设置.
	 * @param beanName the name of the bean
	 * @return <code>true</code> if properties should be set on the bean; <code>false</code>
	 * if property population should be skipped. Normal implementations should return <code>true</code>.
	 * Returning <code>false</code> will also prevent any subsequent InstantiationAwareBeanPostProcessor
	 * instances being invoked on this bean instance.
	 * 				~$ 如果bean属性应该设置;假 如果属性封装应该跳过.正常的实现应该返回true。
	 *                 返回false还将防止任何后续InstantiationAwareBeanPostProcessor实例调用bean实例。
	 * @throws BeansException in case of errors
	 */
	boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;

	/**
	 * Post-process the given property values before the factory applies them
	 * to the given bean. Allows for checking whether all dependencies have been
	 * satisfied, for example based on a "Required" annotation on bean property setters.
	 * <p>Also allows for replacing the property values to apply, typically through
	 * creating a new MutablePropertyValues instance based on the original PropertyValues,
	 * adding or removing specific values.
	 * ***********************************************************************************
	 * ~$ 后处理给定属性值之前工厂他们适用于给定的bean。允许检查是否所有依赖项都满足,例如基于“Required”注释在bean属性setter。
	 * <p>还允许替换属性值应用,通常是通过创建一个新的基于最初的propertyvalue MutablePropertyValues实例,添加或删除特定的值。
	 * @param pvs the property values that the factory is about to apply (never <code>null</code>)
	 *            ~$    属性值,该工厂将适用
	 * @param pds the relevant property descriptors for the target bean (with ignored
	 * dependency types - which the factory handles specifically - already filtered out)
	 *            ~$ 有关目标bean的属性描述符(忽略依赖类型——工厂专门处理——已经过滤掉)
	 * @param bean the bean instance created, but whose properties have not yet been set
	 *             ~$ 创建的bean实例,但尚未设置的属性
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to to the given bean
	 * (can be the passed-in PropertyValues instance), or <code>null</code>
	 * to skip property population
	 * 		~$ 实际的属性值适用于给定的bean(可以传入propertyvalue实例),或null跳过封装属性
	 * @throws BeansException in case of errors
	 * @see org.springframework.beans.MutablePropertyValues
	 */
	PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException;

}
