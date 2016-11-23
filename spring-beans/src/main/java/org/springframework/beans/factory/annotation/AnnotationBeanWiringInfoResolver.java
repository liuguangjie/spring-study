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

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link BeanWiringInfoResolver} that
 * uses the Configurable annotation to identify which classes need autowiring.
 * The bean name to look up will be taken from the {@link Configurable} annotation
 * if specified; otherwise the default will be the fully-qualified name of the
 * class being configured.
 * *******************************************************************************
 * ~$ {@link BeanWiringInfoResolver },使用可配置的注释来识别哪些类需要自动装配.
 *    bean名称查找将从{@link Configurable}注释如果指定,否则默认的完全限定名称类配置.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see Configurable
 * @see org.springframework.beans.factory.wiring.ClassNameBeanWiringInfoResolver
 */
public class AnnotationBeanWiringInfoResolver implements BeanWiringInfoResolver {

	public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
		Assert.notNull(beanInstance, "Bean instance must not be null");
		Configurable annotation = beanInstance.getClass().getAnnotation(Configurable.class);
		return (annotation != null ? buildWiringInfo(beanInstance, annotation) : null);
	}

	/**
	 * Build the BeanWiringInfo for the given Configurable annotation.
	 * ***************************************************************
	 * ~$ 构建BeanWiringInfo为给定 Configurable 的注释
	 * @param beanInstance the bean instance
	 * @param annotation the Configurable annotation found on the bean class
	 * @return the resolved BeanWiringInfo
	 */
	protected BeanWiringInfo buildWiringInfo(Object beanInstance, Configurable annotation) {
		if (!Autowire.NO.equals(annotation.autowire())) {
			return new BeanWiringInfo(annotation.autowire().value(), annotation.dependencyCheck());
		}
		else {
			if (!"".equals(annotation.value())) {
				// explicitly specified bean name
				/** 显式地指定bean名称*/
				return new BeanWiringInfo(annotation.value(), false);
			}
			else {
				// default bean name
				return new BeanWiringInfo(getDefaultBeanName(beanInstance), true);
			}
		}
	}

	/**
	 * Determine the default bean name for the specified bean instance.
	 * <p>The default implementation returns the superclass name for a CGLIB
	 * proxy and the name of the plain bean class else.
	 * *********************************************************************
	 * ~$ 确定默认bean名称指定的bean实例.
	 * <p>默认实现返回的超类名称CGLIB代理和其他普通的bean类的名称.
	 * @param beanInstance the bean instance to build a default name for
	 * @return the default bean name to use
	 * @see ClassUtils#getUserClass(Class)
	 */
	protected String getDefaultBeanName(Object beanInstance) {
		return ClassUtils.getUserClass(beanInstance).getName();
	}

}
