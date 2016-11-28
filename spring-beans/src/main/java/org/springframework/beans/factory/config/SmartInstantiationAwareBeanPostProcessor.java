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

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;

/**
 * Extension of the {@link InstantiationAwareBeanPostProcessor} interface,
 * adding a callback for predicting the eventual type of a processed bean.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. In general, application-provided
 * post-processors should simply implement the plain {@link BeanPostProcessor}
 * interface or derive from the {@link InstantiationAwareBeanPostProcessorAdapter}
 * class. New methods might be added to this interface even in point releases.
 * ********************************************************************************
 * ~$ 扩展的{@link InstantiationAwareBeanPostProcessor }接口,为预测的最终类型添加一个回调处理bean.
 *
 * <p>注:此接口是一个专用的接口,主要是框架内供内部使用.一般来说,内部后处理器应该简单地实现纯{@link BeanPostProcessor }接口
 *  或源自{@link InstantiationAwareBeanPostProcessorAdapter }类.新方法甚至可能被添加到这个接口的版本.
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * Predict the type of the bean to be eventually returned from this
	 * processor's {@link #postProcessBeforeInstantiation} callback.
	 * *****************************************************************
	 * ~$预测的类型返回的bean是最终从这个处理器的{@link #postProcessBeforeInstantiation }回调.
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the type of the bean, or <code>null</code> if not predictable
	 * @throws BeansException in case of errors
	 */
	Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException;

	/**
	 * Determine the candidate constructors to use for the given bean.
	 * ***************************************************************
	 * ~$ 确定候选人构造函数用于给定的bean.
	 * @param beanClass the raw class of the bean (never <code>null</code>)
	 * @param beanName the name of the bean
	 * @return the candidate constructors, or <code>null</code> if none specified
	 * @throws BeansException in case of errors
	 */
	Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException;

	/**
	 * Obtain a reference for early access to the specified bean,
	 * typically for the purpose of resolving a circular reference.
	 * <p>This callback gives post-processors a chance to expose a wrapper
	 * early - that is, before the target bean instance is fully initialized.
	 * The exposed object should be equivalent to the what
	 * {@link #postProcessBeforeInitialization} / {@link #postProcessAfterInitialization}
	 * would expose otherwise. Note that the object returned by this method will
	 * be used as bean reference unless the post-processor returns a different
	 * wrapper from said post-process callbacks. In other words: Those post-process
	 * callbacks may either eventually expose the same reference or alternatively
	 * return the raw bean instance from those subsequent callbacks (if the wrapper
	 * for the affected bean has been built for a call to this method already,
	 * it will be exposes as final bean reference by default).
	 * ************************************************************************************
	 * ~$获得一个参考早期访问指定的bean,通常为了解决循环引用.
	 * <p>这个回调使后处理器有机会提前暴露一个包装器—也就是说,在目标bean实例完全初始化.暴露对象应该相当于{@link #postProcessBeforeInitialization }
	 *  { @link # postProcessAfterInitialization }将暴露.注意,此方法返回的对象将被用作bean引用,除非后处理器返回一个不同的包装说后处理回调.
	 *  换句话说:后处理回调可以最终暴露或者相同的参考从这些后续的回调返回原始的bean实例(如果包装器为受影响的bean是建立调用这个方法,它将作为最终使bean引用默认情况下)
	 * @param bean the raw bean instance
	 * @param beanName the name of the bean
	 * @return the object to expose as bean reference
	 * (typically with the passed-in bean instance as default)
	 * @throws BeansException in case of errors
	 */
	Object getEarlyBeanReference(Object bean, String beanName) throws BeansException;

}
