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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * A {@link FactoryBean} implementation that takes an interface which must have one or more
 * methods with the signatures <code>MyType xxx()</code> or <code>MyType xxx(MyIdType id)</code>
 * (typically, <code>MyService getService()</code> or <code>MyService getService(String id)</code>)
 * and creates a dynamic proxy which implements that interface, delegating to an
 * underlying {@link BeanFactory}.
 *
 * <p>Such service locators permit the decoupling of calling code from
 * the {@link BeanFactory} API, by using an
 * appropriate custom locator interface. They will typically be used for
 * <b>prototype beans</b>, i.e. for factory methods that are supposed to
 * return a new instance for each call. The client receives a reference to the
 * service locator via setter or constructor injection, to be able to invoke
 * the locator's factory methods on demand. <b>For singleton beans, direct
 * setter or constructor injection of the target bean is preferable.</b>
 *
 * <p>On invocation of the no-arg factory method, or the single-arg factory
 * method with a String id of <code>null</code> or empty String, if exactly
 * <b>one</b> bean in the factory matches the return type of the factory
 * method, that bean is returned, otherwise a
 * {@link org.springframework.beans.factory.NoSuchBeanDefinitionException}
 * is thrown.
 *
 * <p>On invocation of the single-arg factory method with a non-null (and
 * non-empty) argument, the proxy returns the result of a
 * {@link BeanFactory#getBean(String)} call,
 * using a stringified version of the passed-in id as bean name.
 *
 * <p>A factory method argument will usually be a String, but can also be an
 * int or a custom enumeration type, for example, stringified via
 * <code>toString</code>. The resulting String can be used as bean name as-is,
 * provided that corresponding beans are defined in the bean factory.
 * Alternatively, {@link #setServiceMappings(Properties) a custom mapping}
 * between service ids and bean names can be defined.
 *
 * <p>By way of an example, consider the following service locator interface.
 * Note that this interface is not dependant on any Spring APIs.
 *
 * **************************************************************************
 * ~$ 需要一个{@link FactoryBean }实现一个接口,它必须有一个或多个方法的签名MyType xxx()或MyType xxx(MyIdType id)
 *  (通常,MyService getService()或MyService getService(String id))并创建一个动态代理实现了这个接口,委托给一个潜在的{@link BeanFactory }.
 *
 * <p>这样的服务定位器允许的调用代码解耦{@link BeanFactory } API,通过使用一个适当的定制定位器接口.
 * 它们通常被用于原型beans,例如工厂方法应该返回一个新实例为每个调用.
 * 客户端收到服务定位器的引用通过setter或构造函数注入,能够根据需要调用定位器的工厂方法.
 * 单例bean,直接目标bean的setter或构造函数注入是可取的.
 *
 * <p>不带参数调用工厂方法,或single-arg工厂方法的字符串id null或空字符串,
 *  如果一个bean在工厂与工厂方法的返回类型,bean返回,否则{@link org.springframework.beans.factory.NoSuchBeanDefinitionException }.
 *
 * <p>在single-arg调用工厂方法与非空(非空)参数,代理返回的结果{@link BeanFactory #getBean(String)}调用,
 *    使用传入的stringified版本id作为bean的名字.
 *
 * <p>工厂方法参数通常会是一个字符串,但也可以是一个整数或一个自定义的枚举类型,例如,通过toString stringified.
 * 由此产生的字符串按原样可以作为bean的名字,只要相应的bean中定义bean工厂.
 * 另外,{@link #setServiceMappings(Properties) a custom mapping}之间可以定义服务id和bean名称.
 *
 * <p>通过一个例子,考虑下面的服务定位器接口。注意,这个接口是不依赖任何Spring api.
 *
 * <pre class="code">package a.b.c;
 *
 *public interface ServiceFactory {
 *
 *    public MyService getService ();
 *}</pre>
 *
 * <p>A sample config in an XML-based
 * {@link BeanFactory} might look as follows:
 *
 * <pre class="code">&lt;beans>
 *
 *   &lt;!-- Prototype bean since we have state -->
 *   &lt;bean id="myService" class="a.b.c.MyService" singleton="false"/>
 *
 *   &lt;!-- will lookup the above 'myService' bean by *TYPE* -->
 *   &lt;bean id="myServiceFactory"
 *            class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
 *     &lt;property name="serviceLocatorInterface" value="a.b.c.ServiceFactory"/>
 *   &lt;/bean>
 *
 *   &lt;bean id="clientBean" class="a.b.c.MyClientBean">
 *     &lt;property name="myServiceFactory" ref="myServiceFactory"/>
 *   &lt;/bean>
 *
 *&lt;/beans></pre>
 *
 * <p>The attendant <code>MyClientBean</code> class implementation might then
 * look something like this:
 *
 * <pre class="code">package a.b.c;
 *
 *public class MyClientBean {
 *
 *    private ServiceFactory myServiceFactory;
 *
 *    // actual implementation provided by the Spring container
 *    public void setServiceFactory(ServiceFactory myServiceFactory) {
 *        this.myServiceFactory = myServiceFactory;
 *    }
 *
 *    public void someBusinessMethod() {
 *        // get a 'fresh', brand new MyService instance
 *        MyService service = this.myServiceFactory.getService();
 *        // use the service object to effect the business logic...
 *    }
 *}</pre>
 *
 * <p>By way of an example that looks up a bean <b>by name</b>, consider
 * the following service locator interface. Again, note that this
 * interface is not dependant on any Spring APIs.
 *
 * <pre class="code">package a.b.c;
 *
 *public interface ServiceFactory {
 *
 *    public MyService getService (String serviceName);
 *}</pre>
 *
 * <p>A sample config in an XML-based
 * {@link BeanFactory} might look as follows:
 *
 * <pre class="code">&lt;beans>
 *
 *   &lt;!-- Prototype beans since we have state (both extend MyService) -->
 *   &lt;bean id="specialService" class="a.b.c.SpecialService" singleton="false"/>
 *   &lt;bean id="anotherService" class="a.b.c.AnotherService" singleton="false"/>
 *
 *   &lt;bean id="myServiceFactory"
 *            class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
 *     &lt;property name="serviceLocatorInterface" value="a.b.c.ServiceFactory"/>
 *   &lt;/bean>
 *
 *   &lt;bean id="clientBean" class="a.b.c.MyClientBean">
 *     &lt;property name="myServiceFactory" ref="myServiceFactory"/>
 *   &lt;/bean>
 *
 *&lt;/beans></pre>
 *
 * <p>The attendant <code>MyClientBean</code> class implementation might then
 * look something like this:
 *
 * <pre class="code">package a.b.c;
 *
 *public class MyClientBean {
 *
 *    private ServiceFactory myServiceFactory;
 *
 *    // actual implementation provided by the Spring container
 *    public void setServiceFactory(ServiceFactory myServiceFactory) {
 *        this.myServiceFactory = myServiceFactory;
 *    }
 *
 *    public void someBusinessMethod() {
 *        // get a 'fresh', brand new MyService instance
 *        MyService service = this.myServiceFactory.getService("specialService");
 *        // use the service object to effect the business logic...
 *    }
 *
 *    public void anotherBusinessMethod() {
 *        // get a 'fresh', brand new MyService instance
 *        MyService service = this.myServiceFactory.getService("anotherService");
 *        // use the service object to effect the business logic...
 *    }
 *}</pre>
 *
 * <p>See {@link ObjectFactoryCreatingFactoryBean} for an alternate approach.
 * **************************************************************************
 * ~$ 看{@link ObjectFactoryCreatingFactoryBean } 另一种方法
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 1.1.4
 * @see #setServiceLocatorInterface
 * @see #setServiceMappings
 * @see ObjectFactoryCreatingFactoryBean
 */
public class ServiceLocatorFactoryBean implements FactoryBean<Object>, BeanFactoryAware, InitializingBean {

	private Class serviceLocatorInterface;

	private Constructor serviceLocatorExceptionConstructor;

	private Properties serviceMappings;

	private ListableBeanFactory beanFactory;

	private Object proxy;


	/**
	 * Set the service locator interface to use, which must have one or more methods with
	 * the signatures <code>MyType xxx()</code> or <code>MyType xxx(MyIdType id)</code>
	 * (typically, <code>MyService getService()</code> or <code>MyService getService(String id)</code>).
	 * See the {@link ServiceLocatorFactoryBean class-level Javadoc} for
	 * information on the semantics of such methods.
	 * **************************************************************************************************
	 * ~$ 设置服务定位器接口使用,它必须有一个或多个方法的签名MyType xxx()或MyType xxx(MyIdType id)
	 *    (通常,MyService getService()或MyService getService(字符串id)).
	 *   看到{@link ServiceLocatorFactoryBean class-level Javadoc} 等语义信息的方法.
	 */
	public void setServiceLocatorInterface(Class interfaceType) {
		this.serviceLocatorInterface = interfaceType;
	}

	/**
	 * Set the exception class that the service locator should throw if service
	 * lookup failed. The specified exception class must have a constructor
	 * with one of the following parameter types: <code>(String, Throwable)</code>
	 * or <code>(Throwable)</code> or <code>(String)</code>.
	 * <p>If not specified, subclasses of Spring's BeansException will be thrown,
	 * for example NoSuchBeanDefinitionException. As those are unchecked, the
	 * caller does not need to handle them, so it might be acceptable that
	 * Spring exceptions get thrown as long as they are just handled generically.
	 * ****************************************************************************
	 * ~$ 设置服务定位器应该抛出的异常类如果服务查找失败了.
	 *    指定的异常类必须有一个构造函数的参数类型:(String,Throwable)或(Throwable)或(String).
	 * <p>如果不指定,Spring的BeansException将抛出的子类,例如NoSuchBeanDefinitionException.
	 *   那些未经检查的,调用者不需要处理它们,所以它可能是可接受的,Spring异常被只要他们只是一般处理.
	 * @see #determineServiceLocatorExceptionConstructor
	 * @see #createServiceLocatorException
	 */
	public void setServiceLocatorExceptionClass(Class serviceLocatorExceptionClass) {
		if (serviceLocatorExceptionClass != null && !Exception.class.isAssignableFrom(serviceLocatorExceptionClass)) {
			throw new IllegalArgumentException(
					"serviceLocatorException [" + serviceLocatorExceptionClass.getName() + "] is not a subclass of Exception");
		}
		this.serviceLocatorExceptionConstructor =
				determineServiceLocatorExceptionConstructor(serviceLocatorExceptionClass);
	}

	/**
	 * Set mappings between service ids (passed into the service locator)
	 * and bean names (in the bean factory). Service ids that are not defined
	 * here will be treated as bean names as-is.
	 * <p>The empty string as service id key defines the mapping for <code>null</code> and
	 * empty string, and for factory methods without parameter. If not defined,
	 * a single matching bean will be retrieved from the bean factory.
	 * *************************************************************************************
	 * ~$ 设置服务id之间的映射(传递到服务定位器)和bean名称(bean工厂).这里没有定义的服务id将被视为bean名称按原样.
	 * <p>空字符串作为服务id关键定义了零的映射和空字符串,和工厂方法没有参数.如果没有定义,一个匹配将从bean检索bean工厂.
	 * @param serviceMappings mappings between service ids and bean names,
	 * with service ids as keys as bean names as values
	 */
	public void setServiceMappings(Properties serviceMappings) {
		this.serviceMappings = serviceMappings;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ListableBeanFactory)) {
			throw new FatalBeanException(
					"ServiceLocatorFactoryBean needs to run in a BeanFactory that is a ListableBeanFactory");
		}
		this.beanFactory = (ListableBeanFactory) beanFactory;
	}

	public void afterPropertiesSet() {
		if (this.serviceLocatorInterface == null) {
			throw new IllegalArgumentException("Property 'serviceLocatorInterface' is required");
		}

		// Create service locator proxy.
		/** 创建服务定位器代理.*/
		this.proxy = Proxy.newProxyInstance(
				this.serviceLocatorInterface.getClassLoader(),
				new Class[] {this.serviceLocatorInterface},
				new ServiceLocatorInvocationHandler());
	}


	/**
	 * Determine the constructor to use for the given service locator exception
	 * class. Only called in case of a custom service locator exception.
	 * <p>The default implementation looks for a constructor with one of the
	 * following parameter types: <code>(String, Throwable)</code>
	 * or <code>(Throwable)</code> or <code>(String)</code>.
	 * *************************************************************************
	 * ~$ 确定给定的服务定位器的构造函数使用异常类.只叫一个自定义的服务定位器异常.
	 * <p>默认实现查找一个构造函数的参数类型:(String,Throwable)或(Throwable)或(String).
	 * @param exceptionClass the exception class
	 * @return the constructor to use
	 * @see #setServiceLocatorExceptionClass
	 */
	protected Constructor determineServiceLocatorExceptionConstructor(Class exceptionClass) {
		try {
			return exceptionClass.getConstructor(new Class[] {String.class, Throwable.class});
		}
		catch (NoSuchMethodException ex) {
			try {
				return exceptionClass.getConstructor(new Class[] {Throwable.class});
			}
			catch (NoSuchMethodException ex2) {
				try {
					return exceptionClass.getConstructor(new Class[] {String.class});
				}
				catch (NoSuchMethodException ex3) {
					throw new IllegalArgumentException(
							"Service locator exception [" + exceptionClass.getName() +
							"] neither has a (String, Throwable) constructor nor a (String) constructor");
				}
			}
		}
	}

	/**
	 * Create a service locator exception for the given cause.
	 * Only called in case of a custom service locator exception.
	 * <p>The default implementation can handle all variations of
	 * message and exception arguments.
	 * ***********************************************************
	 * ~$ 创建一个服务定位器异常为给定的事业.只叫一个自定义的服务定位器异常.
	 * <p>默认的实现可以处理所有消息和异常参数的变化.
	 * @param exceptionConstructor the constructor to use  ~$ 构造函数使用
	 * @param cause the cause of the service lookup failure ~$ 服务查找失败的原因
	 * @return the service locator exception to throw ~$ 服务定位器异常抛出
	 * @see #setServiceLocatorExceptionClass
	 */
	protected Exception createServiceLocatorException(Constructor exceptionConstructor, BeansException cause) {
		Class[] paramTypes = exceptionConstructor.getParameterTypes();
		Object[] args = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			if (paramTypes[i].equals(String.class)) {
				args[i] = cause.getMessage();
			}
			else if (paramTypes[i].isInstance(cause)) {
				args[i] = cause;
			}
		}
		return (Exception) BeanUtils.instantiateClass(exceptionConstructor, args);
	}


	public Object getObject() {
		return this.proxy;
	}

	public Class<?> getObjectType() {
		return this.serviceLocatorInterface;
	}

	public boolean isSingleton() {
		return true;
	}


	/**
	 * Invocation handler that delegates service locator calls to the bean factory.
	 * ****************************************************************************
	 * ~$ 调用处理程序,代表服务定位器调用bean工厂.
	 */
	private class ServiceLocatorInvocationHandler implements InvocationHandler {

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (ReflectionUtils.isEqualsMethod(method)) {
				// Only consider equal when proxies are identical.
				/** 时只考虑平等的代理都是相同的。*/
				return (proxy == args[0]);
			}
			else if (ReflectionUtils.isHashCodeMethod(method)) {
				// Use hashCode of service locator proxy.
				/** 使用服务定位器的hashCode代理.*/
				return System.identityHashCode(proxy);
			}
			else if (ReflectionUtils.isToStringMethod(method)) {
				return "Service locator: " + serviceLocatorInterface.getName();
			}
			else {
				return invokeServiceLocatorMethod(method, args);
			}
		}

		@SuppressWarnings("unchecked")
		private Object invokeServiceLocatorMethod(Method method, Object[] args) throws Exception {
			Class serviceLocatorMethodReturnType = getServiceLocatorMethodReturnType(method);
			try {
				String beanName = tryGetBeanName(args);
				if (StringUtils.hasLength(beanName)) {
					// Service locator for a specific bean name.
					/** 服务定位器为一个特定的bean的名称*/
					return beanFactory.getBean(beanName, serviceLocatorMethodReturnType);
				}
				else {
					// Service locator for a bean type.
					/** 服务定位器bean类型.*/
					return BeanFactoryUtils.beanOfTypeIncludingAncestors(beanFactory, serviceLocatorMethodReturnType);
				}
			}
			catch (BeansException ex) {
				if (serviceLocatorExceptionConstructor != null) {
					throw createServiceLocatorException(serviceLocatorExceptionConstructor, ex);
				}
				throw ex;
			}
		}

		/**
		 * Check whether a service id was passed in.
		 * *****************************************
		 * ~$ 检查是否通过服务id.
		 */
		private String tryGetBeanName(Object[] args) {
			String beanName = "";
			if (args != null && args.length == 1 && args[0] != null) {
				beanName = args[0].toString();
			}
			// Look for explicit serviceId-to-beanName mappings.
			/** 寻找明确serviceId-to-beanName映射 */
			if (serviceMappings != null) {
				String mappedName = serviceMappings.getProperty(beanName);
				if (mappedName != null) {
					beanName = mappedName;
				}
			}
			return beanName;
		}

		private Class getServiceLocatorMethodReturnType(Method method) throws NoSuchMethodException {
			Class[] paramTypes = method.getParameterTypes();
			Method interfaceMethod = serviceLocatorInterface.getMethod(method.getName(), paramTypes);
			Class serviceLocatorReturnType = interfaceMethod.getReturnType();

			// Check whether the method is a valid service locator.
			/** 检查方法是否有效的服务定位器.*/
			if (paramTypes.length > 1 || void.class.equals(serviceLocatorReturnType)) {
				throw new UnsupportedOperationException(
						"May only call methods with signature '<type> xxx()' or '<type> xxx(<idtype> id)' " +
						"on factory interface, but tried to call: " + interfaceMethod);
			}
			return serviceLocatorReturnType;
		}
	}

}
