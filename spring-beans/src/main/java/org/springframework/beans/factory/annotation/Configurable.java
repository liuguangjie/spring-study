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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as being eligible for Spring-driven configuration.
 * 
 * <p>Typically used with the AspectJ <code>AnnotationBeanConfigurerAspect</code>.
 *
 * ****************************************************************************
 * ~$ 标记一个类作为资格之配置
 * <p>通常使用AspectJ  <code>AnnotationBeanConfigurerAspect</code>.
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Adrian Colyer
 * @author Ramnivas Laddad
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Configurable {

	/**
	 * The name of the bean definition that serves as the configuration template.
	 * *************************************************************************
	 * ~$ bean定义的名称作为模板的配置.
	 */
	String value() default "";

	/**
	 * Are dependencies to be injected via autowiring?
	 * ***********************************************
	 * ~$ 依赖关系是通过自动装配注射吗?
	 */
	Autowire autowire() default Autowire.NO;

	/**
	 * Is dependency checking to be performed for configured objects?
	 * **************************************************************
	 * ~$ 配置对象的依赖性检查要执行吗?
	 */
	boolean dependencyCheck() default false;
	
	/**
	 * Are dependencies to be injected prior to the construction of an object?
	 * ***********************************************************************
	 * ~$ 是依赖注入前施工对象的吗?
	 */
	boolean preConstruction() default false;

}
