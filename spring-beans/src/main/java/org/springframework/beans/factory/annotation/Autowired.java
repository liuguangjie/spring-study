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
 * Marks a constructor, field, setter method or config method as to be
 * autowired by Spring's dependency injection facilities.
 *
 * <p>Only one constructor (at max) of any given bean class may carry this
 * annotation, indicating the constructor to autowire when used as a Spring
 * bean. Such a constructor does not have to be public.
 *
 * <p>Fields are injected right after construction of a bean, before any
 * config methods are invoked. Such a config field does not have to be public.
 *
 * <p>Config methods may have an arbitrary name and any number of arguments;
 * each of those arguments will be autowired with a matching bean in the
 * Spring container. Bean property setter methods are effectively just
 * a special case of such a general config method. Such config methods
 * do not have to be public.
 *
 * <p>In the case of multiple argument methods, the 'required' parameter is 
 * applicable for all arguments.
 *
 * <p>In case of a {@link java.util.Collection} or {@link java.util.Map}
 * dependency type, the container will autowire all beans matching the
 * declared value type. In case of a Map, the keys must be declared as
 * type String and will be resolved to the corresponding bean names.
 *
 * <p>Note that actual injection is performed through a
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} which in turn means that you <em>cannot</em>
 * use {@code @Autowired} to inject references into
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} or {@link BeanFactoryPostProcessor} types. Please
 * consult the javadoc for the {@link AutowiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * ***************************************************************************
 * ~$ 标志着一个构造函数,字段setter方法或配置方法是通过Spring的依赖项注入autowired的设施.
 *
 * <p>只有一个构造函数(max)任何给定的bean类可能携带这个注释,
 *    说明构造函数自动装配使用时作为一个Spring bean.这样的构造函数不需要公开.
 *
 * <p>字段注入建设bean之后,任何配置方法之前被调用.这样的配置字段不需要公开.
 *
 * <p>配置方法可能有任意名字和任意数量的参数;这些参数将会与通过名字匹配Spring容器中的bean
 *    Bean属性setter方法有效的只是这样一个通用配置方法的一个特例。这种配置方法不需要公开.
 *
 * <p>的多个参数方法,所需的参数适用于所有参数
 *
 * <p>对于{@link java.util.Collection}或{@link java.util.Map}依赖类型,
 *    容器会自动装配所有bean匹配的声明的值类型.
 *    对于地图,key必须声明为类型字符串并将解决相应的bean的名称.
 *
 * <p>注意实际注入是通过执行一个{@link org.springframework.beans.factory.config.BeanPostProcessor
 *  BeanPostProcessor }反过来意味着你<em>cannot</em>使用{@code @autowired},
 *  注入引用 {@link org.springframework.beans.factory.config.BeanPostProcessor
 *  BeanPostProcessor }或{@link BeanFactoryPostProcessor }类型.
 *  请咨询的javadoc {@link AutowiredAnnotationBeanPostProcessor }类(默认情况下,检查是否存在该注释).
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Qualifier
 * @see Value
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

	/**
	 * Declares whether the annotated dependency is required.
	 * <p>Defaults to <code>true</code>.
	 * *****************************************************
	 * ~$ 声明是否需要带注释的依赖.
	 */
	boolean required() default true;

}
