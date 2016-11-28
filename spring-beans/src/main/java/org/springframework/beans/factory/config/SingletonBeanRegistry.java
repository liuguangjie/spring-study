/*
 * Copyright 2002-2009 the original author or authors.
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

/**
 * Interface that defines a registry for shared bean instances.
 * Can be implemented by {@link org.springframework.beans.factory.BeanFactory}
 * implementations in order to expose their singleton management facility
 * in a uniform manner.
 *
 * <p>The {@link ConfigurableBeanFactory} interface extends this interface.
 *
 * ***************************************************************************
 * ~$ 接口定义了一个注册表共享bean实例.可以实现通过{@link org.springframework.beans.factory.BeanFactory}
 *     实现为了揭露他们的单例以统一的方式管理设施.
 *
 * <p>{@link ConfigurableBeanFactory }接口扩展这个接口.
 * @author Juergen Hoeller
 * @since 2.0
 * @see ConfigurableBeanFactory
 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
 * @see org.springframework.beans.factory.support.AbstractBeanFactory
 */
public interface SingletonBeanRegistry {

	/**
	 * Register the given existing object as singleton in the bean registry,
	 * under the given bean name.
	 * <p>The given instance is supposed to be fully initialized; the registry
	 * will not perform any initialization callbacks (in particular, it won't
	 * call InitializingBean's <code>afterPropertiesSet</code> method).
	 * The given instance will not receive any destruction callbacks
	 * (like DisposableBean's <code>destroy</code> method) either.
	 * <p>When running within a full BeanFactory: <b>Register a bean definition
	 * instead of an existing instance if your bean is supposed to receive
	 * initialization and/or destruction callbacks.</b>
	 * <p>Typically invoked during registry configuration, but can also be used
	 * for runtime registration of singletons. As a consequence, a registry
	 * implementation should synchronize singleton access; it will have to do
	 * this anyway if it supports a BeanFactory's lazy initialization of singletons.
	 * ******************************************************************************
	 * ~$ 鉴于现有对象注册为单例在bean注册表中,根据给定的bean的名称.
	 * <p>给定的实例应该是完全初始化;注册表将不会执行任何初始化回调(特别是,它不会调用InitializingBean afterPropertiesSet方法).
	 *    给定的实例将不会收到任何破坏回调(如DisposableBean destroymethod).
	 * <p>当运行在一个完全BeanFactory:注册一个bean定义而不是现有的如果你的bean实例应该接收回调函数初始化和/或破坏.
	 * <p>在注册中心配置通常调用,但也可以用于运行时注册单例。因此,注册中心实现同步单应该访问;它将不得不这样做无论如何如果它支持BeanFactory单件的延迟初始化.
	 * @param beanName the name of the bean
	 * @param singletonObject the existing singleton object
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.DisposableBean#destroy
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#registerBeanDefinition
	 */
	void registerSingleton(String beanName, Object singletonObject);

	/**
	 * Return the (raw) singleton object registered under the given name.
	 * <p>Only checks already instantiated singletons; does not return an Object
	 * for singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to access manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to access a singleton
	 * defined by a bean definition that already been created, in a raw fashion.
	 * <p><b>NOTE:</b> This lookup method is not aware of FactoryBean prefixes or aliases.
	 * You need to resolve the canonical bean name first before obtaining the singleton instance.
	 * ******************************************************************************************
	 * ~$ 返回(生)单例对象注册名字.
	 * <p>只检查已经实例化的单件,不返回一个单例对象还没有实例化bean定义.
	 * <p>这种方法的主要目的是访问手动注册单例(见{@link #registerSingleton }).也可以用来访问单例由已经创建的bean定义,定义在一个原始的方式.
	 * <p>注意:这个查找方法不知道FactoryBean前缀或别名.你需要解决规范化bean的名字先获取单例实例.
	 * @param beanName the name of the bean to look for
	 * @return the registered singleton object, or <code>null</code> if none found
	 * @see ConfigurableListableBeanFactory#getBeanDefinition
	 */
	Object getSingleton(String beanName);

	/**
	 * Check if this registry contains a singleton instance with the given name.
	 * <p>Only checks already instantiated singletons; does not return <code>true</code>
	 * for singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to check manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to check whether a
	 * singleton defined by a bean definition has already been created.
	 * <p>To check whether a bean factory contains a bean definition with a given name,
	 * use ListableBeanFactory's <code>containsBeanDefinition</code>. Calling both
	 * <code>containsBeanDefinition</code> and <code>containsSingleton</code> answers
	 * whether a specific bean factory contains a local bean instance with the given name.
	 * <p>Use BeanFactory's <code>containsBean</code> for general checks whether the
	 * factory knows about a bean with a given name (whether manually registered singleton
	 * instance or created by bean definition), also checking ancestor factories.
	 * <p><b>NOTE:</b> This lookup method is not aware of FactoryBean prefixes or aliases.
	 * You need to resolve the canonical bean name first before checking the singleton status.
	 * ****************************************************************************************
	 * ~$ 检查是否该注册中心包含一个单例实例的名字.
	 * <p>只检查已经实例化的单例对象,单例bean定义的还真没有没有实例化.
	 * <p>这种方法的主要目的是检查手动注册单例(见{@link #registerSingleton }).
	 *  也可以用来检查是否一个单例定义的bean定义已经被创建.
	 * <p>检查是否一个bean工厂包含bean定义与给定的名称,使用ListableBeanFactory containsBeanDefinition.
	 *   调用containsBeanDefinition和containsSingleton回答一个特定bean工厂是否包含一个给定名称的本地bean实例.
	 * <p>使用BeanFactory containsBean一般检查工厂是否知道一个bean名字(无论是手动注册单例实例或创建的bean定义),同时检查祖先工厂.
	 * <p>注意:这个查找方法不知道FactoryBean前缀或别名.你需要解决规范化bean的名字之前,首先检查独立地位.
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a singleton instance with the given name
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.ListableBeanFactory#containsBeanDefinition
	 * @see org.springframework.beans.factory.BeanFactory#containsBean
	 */
	boolean containsSingleton(String beanName);

	/**
	 * Return the names of singleton beans registered in this registry.
	 * <p>Only checks already instantiated singletons; does not return names
	 * for singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to check manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to check which singletons
	 * defined by a bean definition have already been created.
	 * ******************************************************************************
	 * ~$ 返回单例bean的名称在此注册表注册.
	 * <p>只检查已经实例化的单例对象,单例bean定义不返回名称尚未初始化.
	 * <p>这种方法的主要目的是检查手动注册单例(见{@link #registerSingleton }).也可以用来检查,单例对象定义的bean定义已经被创建.
	 * @return the list of names as a String array (never <code>null</code>)
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionNames
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionNames
	 */
	String[] getSingletonNames();

	/**
	 * Return the number of singleton beans registered in this registry.
	 * <p>Only checks already instantiated singletons; does not count
	 * singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to check manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to count the number of
	 * singletons defined by a bean definition that have already been created.
	 * ******************************************************************************
	 * ~$ 返回单例bean的数量在此注册表注册.
	 * <p>只检查已经实例化的单例对象,不计数单还没有实例化bean定义.
	 * <p>这种方法的主要目的是检查手动注册单例(见{@link #registerSingleton }).也可以用来计算单件的数量由一个已经创建的bean定义.
	 * @return the number of singleton beans
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionCount
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionCount
	 */
	int getSingletonCount();

}
