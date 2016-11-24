/*
 * Copyright 2002-2008 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 *
 * <p>This subinterface of {@link org.springframework.beans.factory.BeanFactory}
 * is not meant to be used in normal application code: Stick to
 * {@link org.springframework.beans.factory.BeanFactory} or
 * {@link ListableBeanFactory} for typical
 * use cases. This interface is just meant to allow for framework-internal
 * plug'n'play even when needing access to bean factory configuration methods.
 *
 * ****************************************************************************
 * ~$ 配置接口是由最有助于实现bean工厂。
 * 除了{ @link ConfigurableBeanFactory },它提供了工具来分析和修改bean定义并pre-instantiate单例。
 *
 * <p>这个子接口的{@link org.springframework.beans.factory.BeanFactory }并不意味着在正常的应用程序代码中使用:
 *    坚持{@link org.springframework.beans.factory.BeanFactory }或{@link ListableBeanFactory }为典型的用例.
 *    这个接口是为了允许framework-internal塞’“娱乐即使需要访问bean工厂配置方法。
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 */
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * Ignore the given dependency type for autowiring:
	 * for example, String. Default is none.
	 * *************************************************
	 * ~$ 忽略自动装配的给定依赖类型:例如,字符串。默认是没有的.
	 * @param type the dependency type to ignore
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * Ignore the given dependency interface for autowiring.
	 * <p>This will typically be used by application contexts to register
	 * dependencies that are resolved in other ways, like BeanFactory through
	 * BeanFactoryAware or ApplicationContext through ApplicationContextAware.
	 * <p>By default, only the BeanFactoryAware interface is ignored.
	 * For further types to ignore, invoke this method for each type.
	 * ***********************************************************************
	 * ~$ 忽略给定界面自动装配的依赖.
	 * <p>这通常会使用应用程序上下文注册依赖项解析在其他方面,
	 *   像BeanFactory通过BeanFactoryAware或通过ApplicationContextAware ApplicationContext。
	 * <p>默认情况下,只有BeanFactoryAware接口将被忽略。为进一步忽略类型,每种类型的调用此方法。
	 *
	 * @param ifc the dependency interface to ignore
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * Register a special dependency type with corresponding autowired value.
	 * <p>This is intended for factory/context references that are supposed
	 * to be autowirable but are not defined as beans in the factory:
	 * e.g. a dependency of type ApplicationContext resolved to the
	 * ApplicationContext instance that the bean is living in.
	 * <p>Note: There are no such default types registered in a plain BeanFactory,
	 * not even for the BeanFactory interface itself.
	 * **************************************************************************
	 * ~$ 注册一个特别依赖类型与相应的autowired的值。
	 * <p> 这是用于工厂/上下文的引用应该是autowirable但不定义为bean工厂:
	 * 如依赖ApplicationContext决心的ApplicationContext实例类型的bean是生活在。
	 * <p>注意:没有这样的默认类型注册一个普通BeanFactory,即使是BeanFactory接口本身。
	 *
	 * @param dependencyType the dependency type to register. This will typically
	 * be a base interface such as BeanFactory, with extensions of it resolved
	 * as well if declared as an autowiring dependency (e.g. ListableBeanFactory),
	 * as long as the given value actually implements the extended interface.
	 * 					~$ 依赖类型注册。这通常是一个基接口BeanFactory等与扩展的解决如果声明为一个自动装配依赖(例如ListableBeanFactory),只要给定值实际上实现了扩展接口.
	 * @param autowiredValue the corresponding autowired value. This may also be an
	 * implementation of the {@link org.springframework.beans.factory.ObjectFactory}
	 * interface, which allows for lazy resolution of the actual target value.
	 *                  ~$相应的autowired的价值。这也可能是一个实现{ @link org.springframework.beans.factory.ObjectFactory }接口,它允许懒惰解决实际的目标价值。
	 */
	void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue);

	/**
	 * Determine whether the specified bean qualifies as an autowire candidate,
	 * to be injected into other beans which declare a dependency of matching type.
	 * <p>This method checks ancestor factories as well.
	 * ****************************************************************************
	 * ~$ 确定指定的bean有资格作为一个自动装配的候选人,注入其他bean声明一个依赖的匹配类型。
	 * <p>该方法检查祖先工厂。
	 * @param beanName the name of the bean to check
	 * @param descriptor the descriptor of the dependency to resolve
	 *                   ~$ 描述符的依赖关系来解决
	 * @return whether the bean should be considered as autowire candidate
	 * 			~$ bean是否应该被视为自动装配的候选人
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * Return the registered BeanDefinition for the specified bean, allowing access
	 * to its property values and constructor argument value (which can be
	 * modified during bean factory post-processing).
	 * <p>A returned BeanDefinition object should not be a copy but the original
	 * definition object as registered in the factory. This means that it should
	 * be castable to a more specific implementation type, if necessary.
	 * <p><b>NOTE:</b> This method does <i>not</i> consider ancestor factories.
	 * It is only meant for accessing local bean definitions of this factory.
	 * *****************************************************************************
	 * ~$ 返回指定bean注册BeanDefinition,允许访问其属性值和构造函数参数值(可以修改在bean工厂后处理)。
	 * <p>返回BeanDefinition对象不应该复制而原始定义对象注册的工厂。这意味着它应该是可塑的更具体的实现类型,如果必要的.
	 * <p> 注意:这个方法没有考虑祖先工厂.它只是意味着用于访问本地bean定义的工厂.
	 * @param beanName the name of the bean
	 * @return the registered BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * defined in this factory
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Freeze all bean definitions, signalling that the registered bean definitions
	 * will not be modified or post-processed any further.
	 * <p>This allows the factory to aggressively cache bean definition metadata.
	 * *****************************************************************************
	 * ~$ 冻结所有bean定义,表明注册的bean定义将不会被修改或进一步进行后期处理。
	 * <p>这允许工厂积极缓存bean定义元数据。
	 */
	void freezeConfiguration();

	/**
	 * Return whether this factory's bean definitions are frozen,
	 * i.e. are not supposed to be modified or post-processed any further.
	 * *******************************************************************
	 * ~$返回这个工厂的bean定义是否冻结,即不应该被修改或进一步进行后期处理。
	 * @return <code>true</code> if the factory's configuration is considered frozen
	 * 				~$ 如果工厂的配置是冻结
	 */
	boolean isConfigurationFrozen();

	/**
	 * *
	 * ****************************************************************************
	 * ~$ 确保所有non-lazy-init单例实例化时,也考虑{ @link org.springframework.beans.factory.FactoryBean FactoryBeans }.
	 *    通常调用结束时,工厂设置,如果需要的话.@throws BeansException如果其中一个不能创建单例bean.
	 *    注意:这可能已经离开了工厂和一些豆子已经初始化!调用{@link #destroySingletons()}完全清理。
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;

}
