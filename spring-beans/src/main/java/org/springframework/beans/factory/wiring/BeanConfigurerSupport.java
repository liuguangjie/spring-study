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

package org.springframework.beans.factory.wiring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Convenient base class for configurers that can perform Dependency Injection
 * on objects (however they may be created). Typically subclassed by AspectJ aspects.
 *
 * <p>Subclasses may also need a custom metadata resolution strategy, in the
 * {@link BeanWiringInfoResolver} interface. The default implementation looks
 * for a bean with the same name as the fully-qualified class name. (This is
 * the default name of the bean in a Spring XML file if the '<code>id</code>'
 * attribute is not used.)
 * **********************************************************************************
 * ~$ configurers能够进行方便的基类对象的依赖项注入(不过他们可能被创建).
 *    通常由AspectJ方面从它派生出子类.
 *
 * <p>子类可能还需要一个定制的元数据解决策略,在{@link BeanWiringInfoResolver }接口.
 *    默认实现查找bean名称相同的完全限定类名.(这是默认的bean的名称在Spring XML文件如果不使用的id属性).
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @since 2.0
 * @see #setBeanWiringInfoResolver
 * @see ClassNameBeanWiringInfoResolver
 */
public class BeanConfigurerSupport implements BeanFactoryAware, InitializingBean, DisposableBean  {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private volatile BeanWiringInfoResolver beanWiringInfoResolver;

	private volatile ConfigurableListableBeanFactory beanFactory;


	/**
	 * Set the {@link BeanWiringInfoResolver} to use.
	 * <p>The default behavior is to look for a bean with the same name as the class.
	 * As an alternative, consider using annotation-driven bean wiring.
	 * ******************************************************************************
	 * ~$ 使用设置{@link BeanWiringInfoResolver }.默认行为是寻找一个具有相同名称的bean类.
	 *    作为一种替代方法,考虑使用注解驱动的bean连接.
	 * @see ClassNameBeanWiringInfoResolver
	 * @see org.springframework.beans.factory.annotation.AnnotationBeanWiringInfoResolver
	 */
	public void setBeanWiringInfoResolver(BeanWiringInfoResolver beanWiringInfoResolver) {
		Assert.notNull(beanWiringInfoResolver, "BeanWiringInfoResolver must not be null");
		this.beanWiringInfoResolver = beanWiringInfoResolver;
	}

	/**
	 * Set the {@link BeanFactory} in which this aspect must configure beans.
	 * **********************************************************************
	 * ~$设置{@link BeanFactory }这方面必须配置bean.
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
				 "Bean configurer aspect needs to run in a ConfigurableListableBeanFactory: " + beanFactory);
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		if (this.beanWiringInfoResolver == null) {
			this.beanWiringInfoResolver = createDefaultBeanWiringInfoResolver();
		}
	}

	/**
	 * Create the default BeanWiringInfoResolver to be used if none was
	 * specified explicitly.
	 * <p>The default implementation builds a {@link ClassNameBeanWiringInfoResolver}.
	 * ********************************************************************************
	 * ~$ 创建默认使用BeanWiringInfoResolver如果没有显式地指定.
	 * <p>默认实现构建一个{@link ClassNameBeanWiringInfoResolver }.
	 * @return the default BeanWiringInfoResolver (never <code>null</code>)
	 */
	protected BeanWiringInfoResolver createDefaultBeanWiringInfoResolver() {
		return new ClassNameBeanWiringInfoResolver();
	}

	/**
	 * Check that a {@link BeanFactory} has been set.
	 * **********************************************
	 * ~$检查一个{@link BeanFactory }已设置.
	 */
	public void afterPropertiesSet() {
		Assert.notNull(this.beanFactory, "BeanFactory must be set");
	}

	/**
	 * Release references to the {@link BeanFactory} and
	 * {@link BeanWiringInfoResolver} when the container is destroyed.
	 * ****************************************************************
	 * ~$释放引用{@link BeanFactory }和{@link BeanWiringInfoResolver }当容器被摧毁.
	 */
	public void destroy() {
		this.beanFactory = null;
		this.beanWiringInfoResolver = null;
	}


	/**
	 * Configure the bean instance.
	 * <p>Subclasses can override this to provide custom configuration logic.
	 * Typically called by an aspect, for all bean instances matched by a
	 * pointcut.
	 * **********************************************************************
	 * ~$配置bean实例.子类可以重写这个提供自定义配置逻辑.通常通过一个方面,呼吁所有bean实例匹配切入点.
	 * @param beanInstance the bean instance to configure (must <b>not</b> be <code>null</code>)
	 */
	public void configureBean(Object beanInstance) {
		if (this.beanFactory == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("BeanFactory has not been set on " + ClassUtils.getShortName(getClass()) + ": " +
						"Make sure this configurer runs in a Spring container. Unable to configure bean of type [" +
						ClassUtils.getDescriptiveType(beanInstance) + "]. Proceeding without injection.");
			}
			return;
		}

		BeanWiringInfo bwi = this.beanWiringInfoResolver.resolveWiringInfo(beanInstance);
		if (bwi == null) {
			// Skip the bean if no wiring info given.
			/** 跳过bean如果没有布线信息. */
			return;
		}

		try {
			if (bwi.indicatesAutowiring() ||
					(bwi.isDefaultBeanName() && !this.beanFactory.containsBean(bwi.getBeanName()))) {
				// Perform autowiring (also applying standard factory / post-processor callbacks).
				/** 执行自动装配(也应用标准 factory/post-processor 回调). */
				this.beanFactory.autowireBeanProperties(beanInstance, bwi.getAutowireMode(), bwi.getDependencyCheck());
				Object result = this.beanFactory.initializeBean(beanInstance, bwi.getBeanName());
				checkExposedObject(result, beanInstance);
			}
			else {
				// Perform explicit wiring based on the specified bean definition.
				/** 执行显式布线根据指定的bean定义.*/
				Object result = this.beanFactory.configureBean(beanInstance, bwi.getBeanName());
				checkExposedObject(result, beanInstance);
			}
		}
		catch (BeanCreationException ex) {
			Throwable rootCause = ex.getMostSpecificCause();
			if (rootCause instanceof BeanCurrentlyInCreationException) {
				BeanCreationException bce = (BeanCreationException) rootCause;
				if (this.beanFactory.isCurrentlyInCreation(bce.getBeanName())) {
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to create target bean '" + bce.getBeanName() +
								"' while configuring object of type [" + beanInstance.getClass().getName() +
								"] - probably due to a circular reference. This is a common startup situation " +
								"and usually not fatal. Proceeding without injection. Original exception: " + ex);
					}
					return;
				}
			}
			throw ex;
		}
	}

	private void checkExposedObject(Object exposedObject, Object originalBeanInstance) {
		if (exposedObject != originalBeanInstance) {
			throw new IllegalStateException("Post-processor tried to replace bean instance of type [" +
					originalBeanInstance.getClass().getName() + "] with (proxy) object of type [" +
					exposedObject.getClass().getName() + "] - not supported for aspect-configured classes!");
		}
	}

}
