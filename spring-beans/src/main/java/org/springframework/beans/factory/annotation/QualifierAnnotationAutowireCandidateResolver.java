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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * {@link AutowireCandidateResolver} implementation that matches bean definition qualifiers
 * against {@link Qualifier qualifier annotations} on the field or parameter to be autowired.
 * Also supports suggested expression values through a {@link Value value} annotation.
 *
 *实现限定符相匹配的bean定义
 *对{@link Qualifier qualifier annotations } autowired的字段或参数。
 *还支持建议通过{@link Value value}表达式值注释。
 *
 *
 * <p>Also supports JSR-330's {@link javax.inject.Qualifier} annotation, if available.
 *	还支持 JSR-330's {@link javax.inject.Qualifier} 注解,  如果可用
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see AutowireCandidateQualifier
 * @see Qualifier
 * @see Value
 */
public class QualifierAnnotationAutowireCandidateResolver implements AutowireCandidateResolver, BeanFactoryAware {

	private final Set<Class<? extends Annotation>> qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>();

	private Class<? extends Annotation> valueAnnotationType = Value.class;

	private BeanFactory beanFactory;


	/**
	 * Create a new QualifierAnnotationAutowireCandidateResolver
	 * for Spring's standard {@link Qualifier} annotation.
	 *
	 * 创建一个新的 QualifierAnnotationAutowireCandidateResolver
	 * Spring的标准{@link Qualifier}注释。
	 *
	 * <p>还支持 JSR-330's {@link javax.inject.Qualifier} 注解,  如果可用
	 */
	@SuppressWarnings("unchecked")
	public QualifierAnnotationAutowireCandidateResolver() {
		this.qualifierTypes.add(Qualifier.class);
		ClassLoader cl = QualifierAnnotationAutowireCandidateResolver.class.getClassLoader();
		try {
			this.qualifierTypes.add((Class<? extends Annotation>) cl.loadClass("javax.inject.Qualifier"));
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}

	/**
	 * Create a new QualifierAnnotationAutowireCandidateResolver
	 * for the given qualifier annotation type.
	 *
	 * 创建一个新的QualifierAnnotationAutowireCandidateResolver
	 	对于给定的限定符注释类型。
	 *
	 * @param qualifierType the qualifier annotation to look for
	 *                      寻找qualifier注解
	 */
	public QualifierAnnotationAutowireCandidateResolver(Class<? extends Annotation> qualifierType) {
		Assert.notNull(qualifierType, "'qualifierType' must not be null");
		this.qualifierTypes.add(qualifierType);
	}

	/**
	 * Create a new QualifierAnnotationAutowireCandidateResolver
	 * for the given qualifier annotation types.
	 *
	 * 创建一个新的QualifierAnnotationAutowireCandidateResolver
	 	对于给定的限定符注释类型。
	 *
	 * @param qualifierTypes the qualifier annotations to look for
	 *                       寻找qualifier注解
	 */
	public QualifierAnnotationAutowireCandidateResolver(Set<Class<? extends Annotation>> qualifierTypes) {
		Assert.notNull(qualifierTypes, "'qualifierTypes' must not be null");
		this.qualifierTypes.addAll(qualifierTypes);
	}


	/**
	 * Register the given type to be used as a qualifier when autowiring.
	 * <p>This identifies qualifier annotations for direct use (on fields,
	 * method parameters and constructor parameters) as well as meta
	 * annotations that in turn identify actual qualifier annotations.
	 * <p>This implementation only supports annotations as qualifier types.
	 * The default is Spring's {@link Qualifier} annotation which serves
	 * as a qualifier for direct use and also as a meta annotation.
	 *
	 *注册时要使用的特定类型限定符自动装配。
	 * <p>直接使用这个标识注解(字段,方法参数和构造函数参数)以及元注释,从而确定实际的注解。
	 * <p>这个实现只支持注释类型限定符。 默认是spring的{@link Qualifier}注释 作为直接使用限定符,也作为一个元注释。
	 *
	 *
	 * @param qualifierType the annotation type to register
	 *                      注册的注释类型
	 */
	public void addQualifierType(Class<? extends Annotation> qualifierType) {
		this.qualifierTypes.add(qualifierType);
	}

	/**
	 * Set the 'value' annotation type, to be used on fields, method parameters
	 * and constructor parameters.
	 * <p>The default value annotation type is the Spring-provided
	 * {@link Value} annotation.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate a default value
	 * expression for a specific argument.
	 * *****************************************************************************
	 * “价值”的注释类型,字段,方法上使用参数
	 *和构造函数参数。
	 * <p>注释类型默认值是 Spring-provided
	 * {@link Value}注释。
	 * <p> setter属性存在,这样开发者就可以提供他们自己的
	 *(non-Spring-specific)注释类型来表示一个默认值
	 *表达为一个特定的参数。
	 *
	 */
	public void setValueAnnotationType(Class<? extends Annotation> valueAnnotationType) {
		this.valueAnnotationType = valueAnnotationType;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	/**
	 * Determine whether the provided bean definition is an autowire candidate.
	 * <p>To be considered a candidate the bean's <em>autowire-candidate</em>
	 * attribute must not have been set to 'false'. Also, if an annotation on
	 * the field or parameter to be autowired is recognized by this bean factory
	 * as a <em>qualifier</em>, the bean must 'match' against the annotation as
	 * well as any attributes it may contain. The bean definition must contain
	 * the same qualifier or match by meta attributes. A "value" attribute will
	 * fallback to match against the bean name or an alias if a qualifier or
	 * attribute does not match.
	 * *************************************************************************
	 *确定是否一个自动装配的候选人提供的bean定义。
	 * < p >要考虑候选人bean的< em > autowire-candidate < / em >
	 *属性不能被设置为“false”。同样,如果一个注释
	 *字段或参数autowired是认可这个bean工厂
	 *作为< em >限定符< / em >,bean必须“匹配”的注释
	 *它可能包含任何属性。必须包含bean定义
	 *元属性相同的限定符或匹配。“价值”属性
	 *后备匹配如果限定符或bean名称或别名 属性不匹配
	 * @see Qualifier
	 */
	public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		if (!bdHolder.getBeanDefinition().isAutowireCandidate()) {
			// if explicitly false, do not proceed with qualifier check
			return false;
		}
		if (descriptor == null) {
			// no qualification necessary
			return true;
		}
		boolean match = checkQualifiers(bdHolder, descriptor.getAnnotations());
		if (match) {
			MethodParameter methodParam = descriptor.getMethodParameter();
			if (methodParam != null) {
				Method method = methodParam.getMethod();
				if (method == null || void.class.equals(method.getReturnType())) {
					match = checkQualifiers(bdHolder, methodParam.getMethodAnnotations());
				}
			}
		}
		return match;
	}

	/**
	 * Match the given qualifier annotations against the candidate bean definition.
	 * ***************************************************************************
	 * 匹配给定的限定符注释对候选人bean定义。
	 */
	protected boolean checkQualifiers(BeanDefinitionHolder bdHolder, Annotation[] annotationsToSearch) {
		if (ObjectUtils.isEmpty(annotationsToSearch)) {
			return true;
		}
		SimpleTypeConverter typeConverter = new SimpleTypeConverter();
		for (Annotation annotation : annotationsToSearch) {
			Class<? extends Annotation> type = annotation.annotationType();
			if (isQualifier(type)) {
				if (!checkQualifier(bdHolder, annotation, typeConverter)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks whether the given annotation type is a recognized qualifier type.
	 * ***********************************************************************
	 * 检查是否给定的注释类型是公认的限定符的类型。
	 */
	protected boolean isQualifier(Class<? extends Annotation> annotationType) {
		for (Class<? extends Annotation> qualifierType : this.qualifierTypes) {
			if (annotationType.equals(qualifierType) || annotationType.isAnnotationPresent(qualifierType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Match the given qualifier annotation against the candidate bean definition.
	 * **************************************************************************
	 * 匹配给定的限定符注释对候选人bean定义。
	 */
	protected boolean checkQualifier(
			BeanDefinitionHolder bdHolder, Annotation annotation, TypeConverter typeConverter) {

		Class<? extends Annotation> type = annotation.annotationType();
		RootBeanDefinition bd = (RootBeanDefinition) bdHolder.getBeanDefinition();
		AutowireCandidateQualifier qualifier = bd.getQualifier(type.getName());
		if (qualifier == null) {
			qualifier = bd.getQualifier(ClassUtils.getShortName(type));
		}
		if (qualifier == null) {
			Annotation targetAnnotation = null;
			if (bd.getResolvedFactoryMethod() != null) {
				targetAnnotation = bd.getResolvedFactoryMethod().getAnnotation(type);
			}
			if (targetAnnotation == null) {
				// look for matching annotation on the target class
				if (this.beanFactory != null) {
					Class<?> beanType = this.beanFactory.getType(bdHolder.getBeanName());
					if (beanType != null) {
						targetAnnotation = ClassUtils.getUserClass(beanType).getAnnotation(type);
					}
				}
				if (targetAnnotation == null && bd.hasBeanClass()) {
					targetAnnotation = ClassUtils.getUserClass(bd.getBeanClass()).getAnnotation(type);
				}
			}
			if (targetAnnotation != null && targetAnnotation.equals(annotation)) {
				return true;
			}
		}
		Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
		if (attributes.isEmpty() && qualifier == null) {
			// if no attributes, the qualifier must be present
			return false;
		}
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();
			Object expectedValue = entry.getValue();
			Object actualValue = null;
			// check qualifier first
			if (qualifier != null) {
				actualValue = qualifier.getAttribute(attributeName);
			}
			if (actualValue == null) {
				// fall back on bean definition attribute
				actualValue = bd.getAttribute(attributeName);
			}
			if (actualValue == null && attributeName.equals(AutowireCandidateQualifier.VALUE_KEY) &&
					expectedValue instanceof String && bdHolder.matchesName((String) expectedValue)) {
				// fall back on bean name (or alias) match
				continue;
			}
			if (actualValue == null && qualifier != null) {
				// fall back on default, but only if the qualifier is present
				actualValue = AnnotationUtils.getDefaultValue(annotation, attributeName);
			}
			if (actualValue != null) {
				actualValue = typeConverter.convertIfNecessary(actualValue, expectedValue.getClass());
			}
			if (!expectedValue.equals(actualValue)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Determine whether the given dependency carries a value annotation.
	 * *****************************************************************
	 * 确定给定的依赖有注释的值。
	 * @see Value
	 */
	public Object getSuggestedValue(DependencyDescriptor descriptor) {
		Object value = findValue(descriptor.getAnnotations());
		if (value == null) {
			MethodParameter methodParam = descriptor.getMethodParameter();
			if (methodParam != null) {
				value = findValue(methodParam.getMethodAnnotations());
			}
		}
		return value;
	}

	/**
	 * Determine a suggested value from any of the given candidate annotations.
	 * ***********************************************************************
	 *确定一个建议值从任何给定候选人的注释。
	 */
	protected Object findValue(Annotation[] annotationsToSearch) {
		for (Annotation annotation : annotationsToSearch) {
			if (this.valueAnnotationType.isInstance(annotation)) {
				Object value = AnnotationUtils.getValue(annotation);
				if (value == null) {
					throw new IllegalStateException("Value annotation must have a value attribute");
				}
				return value;
			}
		}
		return null;
	}

}
