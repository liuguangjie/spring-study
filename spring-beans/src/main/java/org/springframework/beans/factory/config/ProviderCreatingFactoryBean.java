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

import java.io.Serializable;
import javax.inject.Provider;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

/**
 * A {@link org.springframework.beans.factory.FactoryBean} implementation that
 * returns a value which is a JSR-330 {@link Provider} that in turn
 * returns a bean sourced from a {@link BeanFactory}.
 *
 * <p>This is basically a JSR-330 compliant variant of Spring's good old
 * {@link ObjectFactoryCreatingFactoryBean}. It can be used for traditional
 * external dependency injection configuration that targets a property or
 * constructor argument of type <code>javax.inject.Provider</code>, as an
 * alternative to JSR-330's <code>@Inject</code> annotation-driven approach.
 *
 * *************************************************************************
 * ~$一个{@link org.springframework.beans.factory.FactoryBean }实现返回一个值是jsr-330{@link Provider}这反过来返回一个bean来自{@link BeanFactory }.
 * <p> 这基本上是一个Spring的美好的jsr-330兼容的变体{@link ObjectFactoryCreatingFactoryBean }.
 *     它可用于传统的外部依赖项注入配置目标属性或javax.inject.Provider类型的构造函数参数.作为替代jsr-330的@inject注解驱动的方法.
 *
 * @author Juergen Hoeller
 * @since 3.0.2
 * @see Provider
 * @see ObjectFactoryCreatingFactoryBean
 */
public class ProviderCreatingFactoryBean extends AbstractFactoryBean<Provider> {

	private String targetBeanName;


	/**
	 * Set the name of the target bean.
	 * <p>The target does not <i>have</> to be a non-singleton bean, but realisticially
	 * always will be (because if the target bean were a singleton, then said singleton
	 * bean could simply be injected straight into the dependent object, thus obviating
	 * the need for the extra level of indirection afforded by this factory approach).
	 * *********************************************************************************
	 * ~$ 设置目标bean的名称.
	 * <p>目标没有单体bean,但realisticially总是会(因为如果目标bean是一个单例对象,单例bean说可以直接注入依赖对象,因而无需本厂提供的额外级别的间接寻址方法).
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(this.targetBeanName, "Property 'targetBeanName' is required");
		super.afterPropertiesSet();
	}


	@Override
	public Class getObjectType() {
		return Provider.class;
	}

	@Override
	protected Provider createInstance() {
		return new TargetBeanProvider(getBeanFactory(), this.targetBeanName);
	}


	/**
	 * Independent inner class - for serialization purposes.
	 * *****************************************************
	 * ~$ 独立的内部类,用于序列化.
	 */
	private static class TargetBeanProvider implements Provider, Serializable {

		private final BeanFactory beanFactory;

		private final String targetBeanName;

		public TargetBeanProvider(BeanFactory beanFactory, String targetBeanName) {
			this.beanFactory = beanFactory;
			this.targetBeanName = targetBeanName;
		}

		public Object get() throws BeansException {
			return this.beanFactory.getBean(this.targetBeanName);
		}
	}

}
