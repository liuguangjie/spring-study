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

package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

/**
 * A generic converter that conditionally executes.
 *
 * <p>Applies a rule that determines if a converter between a set of
 * {@link #getConvertibleTypes() convertible types} matches given a client request to
 * convert between a source field of convertible type S and a target field of convertible type T.
 *
 * <p>Often used to selectively match custom conversion logic based on the presence of
 * a field or class-level characteristic, such as an annotation or method. For example,
 * when converting from a String field to a Date field, an implementation might return
 * <code>true</code> if the target field has also been annotated with <code>@DateTimeFormat</code>.
 *
 * <p>As another example, when converting from a String field to an Account field,
 * an implementation might return true if the target Account class defines a
 * <code>public static findAccount(String)</code> method.
 * ************************************************************************************************
 * ~$ 一个通用的转换器,有条件地执行.
 *
 * <p>适用规则,确定如果一个转换器之间的一组{@link #getConvertibleTypes() convertible types}
 *    匹配给定一个客户机请求源字段可转换的类型之间的转换和转换类型T的目标字段.
 *
 * <p>常用于有选择地匹配自定义转换逻辑基于字段或类级别的存在特点,比如注释或方法.
 *    例如,当从一个字符串字段转换成日期字段,实现可能会返回true,如果目标字段也被与@DateTimeFormat注释.
 *
 * <p>作为另一个例子,当从一个字符串字段转换到一个账户,一个实现可能会返回true,如果目标帐户类定义了一个公共静态findAccount(String)方法.
 * @author Keith Donald
 * @since 3.0
 */
public interface ConditionalGenericConverter extends GenericConverter {

	/**
	 * Should the converter from <code>sourceType</code> to <code>targetType</code>
	 * currently under consideration be selected?
	 * ****************************************************************************
	 * ~$ 应该从sourceType转换器targetType目前正在考虑选择?
	 * @param sourceType the type descriptor of the field we are converting from
	 *                   ~$ 的类型描述符字段我们转换
	 * @param targetType the type descriptor of the field we are converting to
	 *                   ~$ 的类型描述符字段我们转换
	 * @return true if conversion should be performed, false otherwise
	 */
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
	
}
