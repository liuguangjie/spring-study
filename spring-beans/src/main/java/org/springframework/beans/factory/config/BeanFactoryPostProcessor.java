/*
 * Copyright 2002-2005 the original author or authors.
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

/**
 * Allows for custom modification of an application context's bean definitions,
 * adapting the bean property values of the context's underlying bean factory.
 *
 * <p>Application contexts can auto-detect BeanFactoryPostProcessor beans in
 * their bean definitions and apply them before any other beans get created.
 *
 * <p>Useful for custom config files targeted at system administrators that
 * override bean properties configured in the application context.
 *
 * <p>See PropertyResourceConfigurer and its concrete implementations
 * for out-of-the-box solutions that address such configuration needs.
 *
 * <p>A BeanFactoryPostProcessor may interact with and modify bean
 * definitions, but never bean instances. Doing so may cause premature bean
 * instantiation, violating the container and causing unintended side-effects.
 * If bean instance interaction is required, consider implementing
 * {@link BeanPostProcessor} instead.
 *
 * ****************************************************************************
 * ~$ 允许自定义修改应用程序上下文的bean定义,适应上下文bean属性值的潜在bean工厂.
 *
 * <p>应用程序上下文可以自动检测BeanFactoryPostProcessor bean的bean定义和创建的任何其他bean之前应用它们.
 *
 * <p>针对系统管理员用于自定义配置文件覆盖bean属性配置应用程序上下文.
 *
 * <p>看到PropertyResourceConfigurer及其具体实现开箱即用的解决方案,解决这些配置的需求.
 *
 * <p>BeanFactoryPostProcessor可能相互作用和修改bean定义,但从未bean实例.
 *    这样做可能会导致过早的bean实例化,违反了容器,导致意想不到的副作用.
 *    如果需要bean实例交互,考虑实现{@link BeanPostProcessor }.
 *
 * @author Juergen Hoeller
 * @since 06.07.2003
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 */
public interface BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for overriding or adding
	 * properties even to eager-initializing beans.
	 * **************************************************************************
	 * ~$ 修改应用程序上下文的内部bean工厂标准后初始化.
	 *    所有bean定义将被加载,但没有实例化bean将.
	 *    这允许覆盖或添加属性甚至eager-initializing bean.
	 * @param beanFactory the bean factory used by the application context
	 * @throws BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
