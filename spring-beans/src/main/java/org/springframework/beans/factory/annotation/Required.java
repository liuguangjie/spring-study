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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method (typically a JavaBean setter method) as being 'required': that is,
 * the setter method must be configured to be dependency-injected with a value.
 * 
 * <p>Please do consult the javadoc for the {@link RequiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * ************************************************************************************
 * ~$ 标志着一个方法(通常是一个JavaBean setter方法)是'required':
 *    也就是说,必须配置为dependency-injected setter方法与一个值.
 *
 * <p>请咨询{@link  RequiredAnnotationBeanPostProcessor}类的javadoc(默认情况下,检查是否存在该注释)。
 * @author Rob Harrop
 * @since 2.0
 * @see RequiredAnnotationBeanPostProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Required {

}
