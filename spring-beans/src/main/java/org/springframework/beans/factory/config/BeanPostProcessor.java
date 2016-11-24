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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * Factory hook that allows for custom modification of new bean instances,
 * e.g. checking for marker interfaces or wrapping them with proxies.
 *
 * <p>ApplicationContexts can autodetect BeanPostProcessor beans in their
 * bean definitions and apply them to any beans subsequently created.
 * Plain bean factories allow for programmatic registration of post-processors,
 * applying to all beans created through this factory.
 *
 * <p>Typically, post-processors that populate beans via marker interfaces
 * or the like will implement {@link #postProcessBeforeInitialization},
 * while post-processors that wrap beans with proxies will normally
 * implement {@link #postProcessAfterInitialization}.
 *
 * ****************************************************************************
 * ~$ 工厂钩,允许自定义修改新的bean实例,如检查标记接口或包装代理.
 *
 * <p>applicationcontext可以自动侦测BeanPostProcessor bean的bean定义并将它们应用于任何bean随后创建.
 *    平原bean工厂允许编程登记后处理器,通过这个工厂申请创建的所有bean.
 *
 * <p>通常,后处理器,通过标记接口或类似的填充bean将实现{@link #postProcessBeforeInitialization },
 *    后处理器,将bean与代理通常会实现{@link #postProcessAfterInitialization }.
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
	 * initialization callbacks (like InitializingBean's <code>afterPropertiesSet</code>
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 *
	 * *********************************************************************************
	 * ~$ 应用这个BeanPostProcessor给定新的bean实例bean初始化之前回调(如InitializingBean afterPropertiesSet或一个自定义的init方法).
	 *   bean已经填充属性值. 返回的bean实例可能是原始的包装器.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one; if
	 * <code>null</code>, no subsequent BeanPostProcessors will be invoked
	 * @throws BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
	 * initialization callbacks (like InitializingBean's <code>afterPropertiesSet</code>
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
	 * instance and the objects created by the FactoryBean (as of Spring 2.0). The
	 * post-processor can decide whether to apply to either the FactoryBean or created
	 * objects or both through corresponding <code>bean instanceof FactoryBean</code> checks.
	 * <p>This callback will also be invoked after a short-circuiting triggered by a
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
	 * in contrast to all other BeanPostProcessor callbacks.
	 * **************************************************************************************
	 * ~$ 应用这个BeanPostProcessor给定新的bean实例任何bean初始化后回调(如InitializingBean afterPropertiesSet或一个自定义的init方法).
	 *    bean已经填充属性值.返回的bean实例可能是原始的包装器。
	 * <p>对于FactoryBean,这个回调将FactoryBean实例和调用创建的对象FactoryBean Spring的(2.0).
	 *    后处理器才能决定是否适用于FactoryBean或创建的对象或通过相应的bean实例FactoryBean检查.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one; if
	 * <code>null</code>, no subsequent BeanPostProcessors will be invoked
	 * @throws BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

}
