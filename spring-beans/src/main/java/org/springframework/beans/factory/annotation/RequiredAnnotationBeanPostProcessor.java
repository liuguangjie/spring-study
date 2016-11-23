/*
 * Copyright 2002-2011 the original author or authors.
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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Conventions;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that enforces required JavaBean properties to have been configured.
 * Required bean properties are detected through a Java 5 annotation:
 * by default, Spring's {@link Required} annotation.
 *
 * <p>The motivation for the existence of this BeanPostProcessor is to allow
 * developers to annotate the setter properties of their own classes with an
 * arbitrary JDK 1.5 annotation to indicate that the container must check
 * for the configuration of a dependency injected value. This neatly pushes
 * responsibility for such checking onto the container (where it arguably belongs),
 * and obviates the need (<b>in part</b>) for a developer to code a method that
 * simply checks that all required properties have actually been set.
 *
 * <p>Please note that an 'init' method may still need to implemented (and may
 * still be desirable), because all that this class does is enforce that a
 * 'required' property has actually been configured with a value. It does
 * <b>not</b> check anything else... In particular, it does not check that a
 * configured value is not <code>null</code>.
 *
 * <p>Note: A default RequiredAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom RequiredAnnotationBeanPostProcessor bean definition.
 *
 * ******************************************************************************************
 * ~$ {@link org.springframework.beans.factory.config.BeanPostProcessor }实现,
 *    强制要求JavaBean属性配置.
 *    需要检测到bean属性通过一个Java 5注释:默认情况下,Spring的{@link Required } 注解.
 *
 * <p>的动机的存在这BeanPostProcessor是允许开发人员为setter属性的类注释一个任意的JDK 1.5注释表明容器必须检查依赖项注入的配置值.
 *    这巧妙地将这种检查的责任推给了容器(在那里可以说是),而不再需要(部分)为开发人员编写一个方法,
 *    简单地检查所有必需的属性已经被设置.
 *
 * <p>请注意,一个"init"方法可能仍然需要实现(和可能仍然是可取的),因为所有这类并执行,所需的属性已经配置了一个值.
 *    它不检查别的……特别是,它不检查配置值非空.
 *
 * <p>注意:默认RequiredAnnotationBeanPostProcessor将注册"context:annotation-config"和"context:component-scan"XML标记.
 *    删除或关闭默认注释RequiredAnnotationBeanPostProcessor,如果您想要指定一个自定义配置bean定义.
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setRequiredAnnotationType
 * @see Required
 */
public class RequiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {

	/**
	 * Bean definition attribute that may indicate whether a given bean is supposed
	 * to be skipped when performing this post-processor's required property check.
	 * ****************************************************************************
	 * ~$ Bean定义属性,可能表明一个给定的Bean是否应该跳过当执行这个后处理器所需的属性检查.
	 * @see #shouldSkip
	 */
	public static final String SKIP_REQUIRED_CHECK_ATTRIBUTE =
			Conventions.getQualifiedAttributeName(RequiredAnnotationBeanPostProcessor.class, "skipRequiredCheck");


	private Class<? extends Annotation> requiredAnnotationType = Required.class;

	private int order = Ordered.LOWEST_PRECEDENCE - 1;

	private ConfigurableListableBeanFactory beanFactory;

	/** Cache for validated bean names, skipping re-validation for the same bean */
	/** 缓存验证bean名称,跳过后续同样的bean*/
	private final Set<String> validatedBeanNames = Collections.synchronizedSet(new HashSet<String>());


	/**
	 * Set the 'required' annotation type, to be used on bean property
	 * setter methods.
	 * <p>The default required annotation type is the Spring-provided
	 * {@link Required} annotation.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a property value
	 * is required.
	 * ***********************************************************************
	 * ~$ 设置所需的注解类型,使用bean属性setter方法.
	 * <p>默认需要注解类型是spring{@link Required}注解
	 * <p>setter属性存在,这样开发者就可以提供自己的(non-Spring-specific)
	 *    注释类型来表示属性值是必需的.
	 */
	public void setRequiredAnnotationType(Class<? extends Annotation> requiredAnnotationType) {
		Assert.notNull(requiredAnnotationType, "'requiredAnnotationType' must not be null");
		this.requiredAnnotationType = requiredAnnotationType;
	}

	/**
	 * Return the 'required' annotation type.
	 * **************************************
	 * ~$ 返回需要的注解类型
	 */
	protected Class<? extends Annotation> getRequiredAnnotationType() {
		return this.requiredAnnotationType;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		}
	}

	public void setOrder(int order) {
	  this.order = order;
	}

	public int getOrder() {
	  return this.order;
	}


	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
	}

	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {

		if (!this.validatedBeanNames.contains(beanName)) {
			if (!shouldSkip(this.beanFactory, beanName)) {
				List<String> invalidProperties = new ArrayList<String>();
				for (PropertyDescriptor pd : pds) {
					if (isRequiredProperty(pd) && !pvs.contains(pd.getName())) {
						invalidProperties.add(pd.getName());
					}
				}
				if (!invalidProperties.isEmpty()) {
					throw new BeanInitializationException(buildExceptionMessage(invalidProperties, beanName));
				}
			}
			this.validatedBeanNames.add(beanName);
		}
		return pvs;
	}

	/**
	 * Check whether the given bean definition is not subject to the annotation-based
	 * required property check as performed by this post-processor.
	 * <p>The default implementations check for the presence of the
	 * {@link #SKIP_REQUIRED_CHECK_ATTRIBUTE} attribute in the bean definition, if any.
	 * ********************************************************************************
	 * ~$ 检查给定的bean定义是否不受所需的基于注释的属性检查通过后处理器执行.
	 * <p> 默认实现检查{@link #SKIP_REQUIRED_CHECK_ATTRIBUTE }属性的存在在bean定义中,如果任何.
	 * @param beanFactory the BeanFactory to check against
	 *                    BeanFactory核对
	 * @param beanName the name of the bean to check against
	 *                 核对bean的名称
	 * @return <code>true</code> to skip the bean; <code>false</code> to process it
	 */
	protected boolean shouldSkip(ConfigurableListableBeanFactory beanFactory, String beanName) {
		if (beanFactory == null || !beanFactory.containsBeanDefinition(beanName)) {
			return false;
		}
		Object value = beanFactory.getBeanDefinition(beanName).getAttribute(SKIP_REQUIRED_CHECK_ATTRIBUTE);
		return (value != null && (Boolean.TRUE.equals(value) || Boolean.valueOf(value.toString())));
	}

	/**
	 * Is the supplied property required to have a value (that is, to be dependency-injected)?
	 * <p>This implementation looks for the existence of a
	 * {@link #setRequiredAnnotationType "required" annotation}
	 * on the supplied {@link PropertyDescriptor property}.
	 * ***************************************************************************************
	 * ~$ 所需提供的属性有一个值(也就是说,dependency-injected)?
	 * <p>这个实现查找的存在{@link #setRequiredAnnotationType "required" annotation}上提供的{@link PropertyDescriptor property}.
	 *
	 * @param propertyDescriptor the target PropertyDescriptor (never <code>null</code>)
	 *  							目标PropertyDescriptor (never <code>null</code>)
	 * @return <code>true</code> if the supplied property has been marked as being required;
	 * <code>false</code> if not, or if the supplied property does not have a setter method
	 * *************************************************************************************
	 * ~$ 如果提供的属性已经被标记为需要; 如果提供的财产没有setter方法
	 */
	protected boolean isRequiredProperty(PropertyDescriptor propertyDescriptor) {
		Method setter = propertyDescriptor.getWriteMethod();
		return (setter != null && AnnotationUtils.getAnnotation(setter, getRequiredAnnotationType()) != null);
	}

	/**
	 * Build an exception message for the given list of invalid properties.
	 * *******************************************************************
	 * ~$ 建立一个给定的列表异常消息无效的属性.
	 * @param invalidProperties the list of names of invalid properties
	 *							无效的属性的名称列表
	 * @param beanName the name of the bean
	 * @return the exception message
	 */
	private String buildExceptionMessage(List<String> invalidProperties, String beanName) {
		int size = invalidProperties.size();
		StringBuilder sb = new StringBuilder();
		sb.append(size == 1 ? "Property" : "Properties");
		for (int i = 0; i < size; i++) {
			String propertyName = invalidProperties.get(i);
			if (i > 0) {
				if (i == (size - 1)) {
					sb.append(" and");
				}
				else {
					sb.append(",");
				}
			}
			sb.append(" '").append(propertyName).append("'");
		}
		sb.append(size == 1 ? " is" : " are");
		sb.append(" required for bean '").append(beanName).append("'");
		return sb.toString();
	}

}
