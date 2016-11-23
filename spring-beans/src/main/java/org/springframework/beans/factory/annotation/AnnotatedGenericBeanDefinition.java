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

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

/**
 * Extension of the {@link GenericBeanDefinition}
 * class, adding support for annotation metadata exposed through the
 * {@link AnnotatedBeanDefinition} interface.
 *
 * <p>This GenericBeanDefinition variant is mainly useful for testing code that expects
 * to operate on an AnnotatedBeanDefinition, for example strategy implementations
 * in Spring's component scanning support (where the default definition class is
 * {@link org.springframework.context.annotation.ScannedGenericBeanDefinition},
 * which also implements the AnnotatedBeanDefinition interface).
 * ************************************************************************************
 * ~$ 扩展的{@link GenericBeanDefinition }类,
 *    添加注释支持元数据暴露通过 {@link AnnotatedBeanDefinition }接口.
 * <p>这种GenericBeanDefinition转化主要是用于测试的代码,希望AnnotatedBeanDefinition操作,
 * 例如Spring组件扫描策略实现支持
 * (缺省定义类是{@link org.springframework.context.annotation.ScannedGenericBeanDefinition },
 	也实现了AnnotatedBeanDefinition接口).
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotatedBeanDefinition#getMetadata()
 * @see StandardAnnotationMetadata
 */
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

	private final AnnotationMetadata annotationMetadata;


	/**
	 * Create a new AnnotatedGenericBeanDefinition for the given bean class.
	 * *********************************************************************
	 * ~$ 创建一个新的AnnotatedGenericBeanDefinition给定bean类.
	 * @param beanClass the loaded bean class
	 */
	public AnnotatedGenericBeanDefinition(Class beanClass) {
		setBeanClass(beanClass);
		this.annotationMetadata = new StandardAnnotationMetadata(beanClass);
	}


	public final AnnotationMetadata getMetadata() {
		 return this.annotationMetadata;
	}

}
