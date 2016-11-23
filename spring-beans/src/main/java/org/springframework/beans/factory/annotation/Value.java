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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation at the field or method/constructor parameter level
 * that indicates a default value expression for the affected argument.
 *
 * <p>Typically used for expression-driven dependency injection. Also supported
 * for dynamic resolution of handler method parameters, e.g. in Spring MVC.
 *
 * <p>A common use case is to assign default field values using
 * "#{systemProperties.myProp}" style expressions.
 *
 * <p>Note that actual processing of the {@code @Value} annotation is performed
 * by a {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} which in turn means that you <em>cannot</em> use
 * {@code @Value} within
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} or {@link BeanFactoryPostProcessor} types. Please
 * consult the javadoc for the {@link AutowiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * *******************************************************************************
 * ~$ 注释的字段或方法或构造函数的参数水平,表明一个默认值影响参数的表达式.
 *
 * <p>通常用于expression-driven依赖注入.还支持动态分辨率的处理程序方法参数,例如Spring MVC.
 *
 * <p>一个常见的用例是使用"#{systemProperties.myProp }"风格表达式.
 *
 * <p>注意,实际处理{ @code @value }注释是由{@link org.springframework.beans.factory.config.BeanPostProcessor
 * 	  BeanPostProcessor } 反过来意味着你不能使用{@code @value}在{@link org.springframework.beans.factory.config.BeanPostProcessor
 * 	  BeanPostProcessor }或{@link BeanFactoryPostProcessor }类型.
 * 	  请咨询的javadoc {@link AutowiredAnnotationBeanPostProcessor }类(默认情况下,检查是否存在该注释)
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Autowired
 * @see org.springframework.beans.factory.config.BeanExpressionResolver
 * @see org.springframework.beans.factory.support.AutowireCandidateResolver#getSuggestedValue
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

	/**
	 * The actual value expression: e.g. "#{systemProperties.myProp}".
	 * ***************************************************************
	 * ~$ 实际值表达式:例如"#{systemProperties.myProp}"
	 */
	String value();

}
