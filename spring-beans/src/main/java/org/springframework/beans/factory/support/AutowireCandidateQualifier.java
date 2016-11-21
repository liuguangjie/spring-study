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

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.util.Assert;

/**
 * Qualifier for resolving autowire candidates. A bean definition that
 * includes one or more such qualifiers enables fine-grained matching
 * against annotations on a field or parameter to be autowired.
 * ******************************************************************
 * ~$ 限定符来解决自动装配的候选人.bean定义,
 *    包括一个或多个这样的限定符
 *    允许细粒度匹配 annotation 字段或autowired的参数
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.annotation.Qualifier
 */
public class AutowireCandidateQualifier extends BeanMetadataAttributeAccessor {

	public static String VALUE_KEY = "value";

	private final String typeName;


	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type.
	 * ***********************************************************
	 * ~$ 构造一个限定符匹配一个给定类型的注释
	 * @param type the annotation type
	 */
	public AutowireCandidateQualifier(Class type) {
		this(type.getName());
	}

	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type name.
	 * <p>The type name may match the fully-qualified class name of
	 * the annotation or the short class name (without the package).
	 * ************************************************************
	 * ~$ 构造一个限定符匹配一个给定类型的注释的名字.
	 * <p> 类型名称可能匹配的完全限定类名注释或简短的类名(不包含包名)
	 * @param typeName the name of the annotation type
	 */
	public AutowireCandidateQualifier(String typeName) {
		Assert.notNull(typeName, "Type name must not be null");
		this.typeName = typeName;
	}

	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type whose <code>value</code> attribute also matches
	 * the specified value.
	 * ***********************************************************
	 * ~$ 构造一个限定符匹配一个给定类型的注释的<code>value</code> 匹配指定的值
	 * @param type the annotation type
	 * @param value the annotation value to match
	 */
	public AutowireCandidateQualifier(Class type, Object value) {
		this(type.getName(), value);
	}

	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type name whose <code>value</code> attribute also matches
	 * the specified value.
	 * <p>The type name may match the fully-qualified class name of
	 * the annotation or the short class name (without the package).
	 * *************************************************************
	 * ~$ 构造一个限定符匹配一个给定类型名称的注释的<code>value</code>匹配指定的值.
	 *  <p>类型名称可能匹配的完全限定类名注释或简短的类名(不包含包名)
	 * @param typeName the name of the annotation type
	 * @param value the annotation value to match
	 */
	public AutowireCandidateQualifier(String typeName, Object value) {
		Assert.notNull(typeName, "Type name must not be null");
		this.typeName = typeName;
		setAttribute(VALUE_KEY, value);
	}


	/**
	 * Retrieve the type name. This value will be the same as the
	 * type name provided to the constructor or the fully-qualified
	 * class name if a Class instance was provided to the constructor.
	 * ***************************************************************
	 * ~$ 检索类型名称.这个值将是一样的类型名称提供给构造函数或提供的完全限定类名如果一个类实例构造函数
	 */
	public String getTypeName() {
		return this.typeName;
	}

}
