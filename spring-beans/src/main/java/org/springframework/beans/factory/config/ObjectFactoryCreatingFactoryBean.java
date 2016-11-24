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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;

/**
 * A {@link org.springframework.beans.factory.FactoryBean} implementation that
 * returns a value which is an {@link ObjectFactory}
 * that in turn returns a bean sourced from a {@link BeanFactory}.
 *
 * <p>As such, this may be used to avoid having a client object directly calling
 * {@link BeanFactory#getBean(String)} to get
 * a (typically prototype) bean from a
 * {@link BeanFactory}, which would be a
 * violation of the inversion of control principle. Instead, with the use
 * of this class, the client object can be fed an
 * {@link ObjectFactory} instance as a
 * property which directly returns only the one target bean (again, which is
 * typically a prototype bean).
 *
 * ******************************************************************************
 * ~$ 一个{@link org.springframework.beans.factory.FactoryBean }实现,
 *    返回值是一个{@link ObjectFactory}这反过来返回一个bean来自{@link BeanFactory}.
 *
 * <p>因此,这可能是用于避免客户端对象直接调用{@link BeanFactory #getBean(String)},
 *   (通常是原型)bean从{@link BeanFactory },这是违反控制反转的原理.
 *   相反,使用这个类,客户端对象可以一个{@link ObjectFactory }实例属性直接只返回一个目标bean(再一次,这是典型的原型bean).
 *
 *
 *
 * <p>A sample config in an XML-based
 * {@link BeanFactory} might look as follows:
 * 一个示例配置在一个基于xml的{@link BeanFactory }可能看起来如下:
 *
 * <pre class="code">&lt;beans&gt;
 *
 *   &lt;!-- Prototype bean since we have state --&gt;
 *   &lt;bean id="myService" class="a.b.c.MyService" scope="prototype"/&gt;
 *
 *   &lt;bean id="myServiceFactory"
 *       class="org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean"&gt;
 *     &lt;property name="targetBeanName"&gt;&lt;idref local="myService"/&gt;&lt;/property&gt;
 *   &lt;/bean&gt;
 *
 *   &lt;bean id="clientBean" class="a.b.c.MyClientBean"&gt;
 *     &lt;property name="myServiceFactory" ref="myServiceFactory"/&gt;
 *   &lt;/bean&gt;
 *
 *&lt;/beans&gt;</pre>
 *
 * <p>The attendant <code>MyClientBean</code> class implementation might look
 * something like this:
 *  ~$ 服务员MyClientBean类的实现可能会看起来像这样:
 * <pre class="code">package a.b.c;
 *
 * import org.springframework.beans.factory.ObjectFactory;
 *
 * public class MyClientBean {
 *
 *   private ObjectFactory&lt;MyService&gt; myServiceFactory;
 *
 *   public void setMyServiceFactory(ObjectFactory&lt;MyService&gt; myServiceFactory) {
 *     this.myServiceFactory = myServiceFactory;
 *   }
 *
 *   public void someBusinessMethod() {
 *     // get a 'fresh', brand new MyService instance
 *     MyService service = this.myServiceFactory.getObject();
 *     // use the service object to effect the business logic...
 *   }
 * }</pre>
 *
 * <p>An alternate approach to this application of an object creational pattern
 * would be to use the {@link ServiceLocatorFactoryBean}
 * to source (prototype) beans. The {@link ServiceLocatorFactoryBean} approach
 * has the advantage of the fact that one doesn't have to depend on any
 * Spring-specific interface such as {@link ObjectFactory},
 * but has the disadvantage of requiring runtime class generation. Please do
 * consult the {@link ServiceLocatorFactoryBean ServiceLocatorFactoryBean JavaDoc}
 * for a fuller discussion of this issue.
 * ********************************************************************************
 * ~$ <p>这个应用程序的另一种方法的对象创建型模式是使用{@link ServiceLocatorFactoryBean }源(prototype)bean.
 *   {@link ServiceLocatorFactoryBean }方法的优势这一事实并不需要依赖于任何spring特定接口,如{@link ObjectFactory },但缺点是需要运行时类生成.
 *   请查阅{@link ServiceLocatorFactoryBean ServiceLocatorFactoryBean JavaDoc }更全面的讨论这个问题。
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see ObjectFactory
 * @see ServiceLocatorFactoryBean
 */
public class ObjectFactoryCreatingFactoryBean extends AbstractFactoryBean<ObjectFactory> {

	private String targetBeanName;


	/**
	 * Set the name of the target bean.
	 * <p>The target does not <i>have</> to be a non-singleton bean, but realisticially
	 * always will be (because if the target bean were a singleton, then said singleton
	 * bean could simply be injected straight into the dependent object, thus obviating
	 * the need for the extra level of indirection afforded by this factory approach).
	 * *********************************************************************************
	 * ~$设置目标bean的名称.<p>目标没有单体bean,但realisticially总是会(因为如果目标bean是一个单例对象,
	 *    单例bean说可以直接注入依赖对象,因而无需本厂提供的额外级别的间接寻址方法).
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
		return ObjectFactory.class;
	}

	@Override
	protected ObjectFactory createInstance() {
		return new TargetBeanObjectFactory(getBeanFactory(), this.targetBeanName);
	}


	/**
	 * Independent inner class - for serialization purposes.
	 * *****************************************************
	 * ~$ 独立的内部类,用于序列化。
	 */
	private static class TargetBeanObjectFactory implements ObjectFactory, Serializable {

		private final BeanFactory beanFactory;

		private final String targetBeanName;

		public TargetBeanObjectFactory(BeanFactory beanFactory, String targetBeanName) {
			this.beanFactory = beanFactory;
			this.targetBeanName = targetBeanName;
		}

		public Object getObject() throws BeansException {
			return this.beanFactory.getBean(this.targetBeanName);
		}
	}

}
