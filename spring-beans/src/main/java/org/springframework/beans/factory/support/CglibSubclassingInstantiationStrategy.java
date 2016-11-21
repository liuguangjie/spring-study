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

package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;

/**
 * Default object instantiation strategy for use in BeanFactories.
 * Uses CGLIB to generate subclasses dynamically if methods need to be
 * overridden by the container, to implement Method Injection.
 * *******************************************************************
 * ~$ 用于beanfactory默认对象实例化策略 使用CGLIB动态生成子类如果容器方法需要覆盖,实现方法注入
 *
 * <p>Using Method Injection features requires CGLIB on the classpath.
 * However, the core IoC container will still run without CGLIB being available.
 * *******************************************************************************
 * ~$ 使用方法注入特性需要CGLIB的类路径中. 然而,核心IoC容器没有CGLIB可用 也可以运行
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public class CglibSubclassingInstantiationStrategy extends SimpleInstantiationStrategy {

	/**
	 * Index in the CGLIB callback array for passthrough behavior,
	 * in which case the subclass won't override the original class.
	 * ******************************************************************
	 * ~$ CGLIB调数组中的索引透传的行为,在这种情况下,子类不会覆盖原来的类
	 */
	private static final int PASSTHROUGH = 0;

	/**
	 * Index in the CGLIB callback array for a method that should
	 * be overridden to provide method lookup.
	 * ***********************************************************
	 * ~$ CGLIB调数组中的索引应该覆盖一个方法提供方法查找
	 */
	private static final int LOOKUP_OVERRIDE = 1;
	
	/**
	 * Index in the CGLIB callback array for a method that should
	 * be overridden using generic Methodreplacer functionality.
	 * **********************************************************
	 * ~$ CGLIB调数组中的索引的方法应该使用通用Methodreplacer功能覆盖
	 */
	private static final int METHOD_REPLACER = 2;


	@Override
	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

		// Must generate CGLIB subclass.
		/** 必须生成 CGLIB 子类*/
		return new CglibSubclassCreator(beanDefinition, owner).instantiate(null, null);
	}

	@Override
	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
			Constructor ctor, Object[] args) {

		return new CglibSubclassCreator(beanDefinition, owner).instantiate(ctor, args);
	}


	/**
	 * An inner class so we don't have a CGLIB dependency in core.
	 * ***********************************************************
	 *  ~$ 一个内部类,所以我们没有CGLIB依赖的核心
	 */
	private static class CglibSubclassCreator {

		private static final Log logger = LogFactory.getLog(CglibSubclassCreator.class);

		private final RootBeanDefinition beanDefinition;

		private final BeanFactory owner;

		public CglibSubclassCreator(RootBeanDefinition beanDefinition, BeanFactory owner) {
			this.beanDefinition = beanDefinition;
			this.owner = owner;
		}

		/**
		 * Create a new instance of a dynamically generated subclasses implementing the
		 * required lookups.
		 * ****************************************************************************
		 * ~$ 创建一个新的动态生成子类的实例实现所需的查询
		 *
		 * @param ctor constructor to use. If this is <code>null</code>, use the
		 * no-arg constructor (no parameterization, or Setter Injection)
		 *	~$ 构造函数使用 如果这是NULL  使用不带参数的构造函数(没有参数化,或Setter注入)
		 * @param args arguments to use for the constructor.
		 * Ignored if the ctor parameter is <code>null</code>.
		 *	~$ 使用构造函数的参数。忽略了如果构造函数 参数是<code>null</code>.
		 * @return new instance of the dynamically generated class
		 */
		public Object instantiate(Constructor ctor, Object[] args) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(this.beanDefinition.getBeanClass());
			enhancer.setCallbackFilter(new CallbackFilterImpl());
			enhancer.setCallbacks(new Callback[] {
					NoOp.INSTANCE,
					new LookupOverrideMethodInterceptor(),
					new ReplaceOverrideMethodInterceptor()
			});

			return (ctor == null) ? 
					enhancer.create() : 
					enhancer.create(ctor.getParameterTypes(), args);
		}


		/**
		 * Class providing hashCode and equals methods required by CGLIB to
		 * ensure that CGLIB doesn't generate a distinct class per bean.
		 * Identity is based on class and bean definition.
		 * ****************************************************************
		 * ~$ CGLIB类提供hashCode和 equals 方法要求确保CGLIB不会每个bean生成一个不同的类。身份是基于类和bean定义
		 */
		private class CglibIdentitySupport {

			/**
			 * Exposed for equals method to allow access to enclosing class field
			 * ******************************************************************
			 * ~$ 暴露在equals方法允许访问封闭类字段
			 */
			protected RootBeanDefinition getBeanDefinition() {
				return beanDefinition;
			}

			@Override
			public boolean equals(Object other) {
				return (other.getClass().equals(getClass()) &&
						((CglibIdentitySupport) other).getBeanDefinition().equals(beanDefinition));
			}

			@Override
			public int hashCode() {
				return beanDefinition.hashCode();
			}
		}


		/**
		 * CGLIB MethodInterceptor to override methods, replacing them with an
		 * implementation that returns a bean looked up in the container.
		 * *******************************************************************
		 * ~$ CGLIB MethodInterceptor覆盖方法,取而代之的是一个实现返回一个bean抬起头的容器
		 */
		private class LookupOverrideMethodInterceptor extends CglibIdentitySupport implements MethodInterceptor {

			public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
				// Cast is safe, as CallbackFilter filters are used selectively.
				LookupOverride lo = (LookupOverride) beanDefinition.getMethodOverrides().getOverride(method);
				return owner.getBean(lo.getBeanName());
			}			
		}


		/**
		 * CGLIB MethodInterceptor to override methods, replacing them with a call
		 * to a generic MethodReplacer.
		 * ***********************************************************************
		 * ~$ CGLIB MethodInterceptor覆盖方法,取而代之的是调用一个通用MethodReplacer
		 */
		private class ReplaceOverrideMethodInterceptor extends CglibIdentitySupport implements MethodInterceptor {

			public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
				ReplaceOverride ro = (ReplaceOverride) beanDefinition.getMethodOverrides().getOverride(method);
				// TODO could cache if a singleton for minor performance optimization
				MethodReplacer mr = (MethodReplacer) owner.getBean(ro.getMethodReplacerBeanName());
				return mr.reimplement(obj, method, args);
			}
		}


		/**
		 * CGLIB object to filter method interception behavior.
		 * ****************************************************
		 * ~$  CGLIB对象筛选方法拦截行为
		 */
		private class CallbackFilterImpl extends CglibIdentitySupport implements CallbackFilter {
			
			public int accept(Method method) {
				MethodOverride methodOverride = beanDefinition.getMethodOverrides().getOverride(method);
				if (logger.isTraceEnabled()) {
					logger.trace("Override for '" + method.getName() + "' is [" + methodOverride + "]");
				}
				if (methodOverride == null) {
					return PASSTHROUGH;
				}
				else if (methodOverride instanceof LookupOverride) {
					return LOOKUP_OVERRIDE;
				}
				else if (methodOverride instanceof ReplaceOverride) {
					return METHOD_REPLACER;
				}
				throw new UnsupportedOperationException(
						"Unexpected MethodOverride subclass: " + methodOverride.getClass().getName());
			}
		}
	}

}
