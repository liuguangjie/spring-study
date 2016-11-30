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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Interface used by the {@link DefaultBeanDefinitionDocumentReader} to handle custom,
 * top-level (directly under {@code <beans/>}) tags.
 *
 * <p>Implementations are free to turn the metadata in the custom tag into as many
 * {@link BeanDefinition BeanDefinitions} as required.
 *
 * <p>The parser locates a {@link BeanDefinitionParser} from the associated
 * {@link NamespaceHandler} for the namespace in which the custom tag resides.
 * ************************************************************************************
 * ~$ 接口使用{@link DefaultBeanDefinitionDocumentReader }来处理自定义,顶级(直属{@code <bean/> })标记.
 *
 * <p>实现可以自由定制标记的元数据变成许多{@link BeanDefinition BeanDefinition }.
 *
 * <p>解析器定位一个{@link BeanDefinitionParser }从相关
 *    {@link NamespaceHandler}的名称空间定义标记所在.
 * @author Rob Harrop
 * @since 2.0
 * @see NamespaceHandler
 * @see AbstractBeanDefinitionParser
 */
public interface BeanDefinitionParser {

	/**
	 * Parse the specified {@link Element} and register the resulting
	 * {@link BeanDefinition BeanDefinition(s)} with the
	 * {@link ParserContext#getRegistry() BeanDefinitionRegistry}
	 * embedded in the supplied {@link ParserContext}.
	 * <p>Implementations must return the primary {@link BeanDefinition} that results
	 * from the parse if they will ever be used in a nested fashion (for example as
	 * an inner tag in a {@code <property/>} tag). Implementations may return
	 * {@code null} if they will <strong>not</strong> be used in a nested fashion.
	 * *******************************************************************************
	 * ~$ 解析指定的{@link Element}和注册结果{@link BeanDefinition BeanDefinition(s)}与
	 *  {@link ParserContext#getRegistry() BeanDefinitionRegistry }中嵌入提供{@link ParserContext }.
	 * <p>实现必须返回主{@link BeanDefinition },从解析结果,如果他们会使用嵌套的方式(例如作为内部标记{@code <属性/> }标签).
	 *    实现可能会返回零} { @code如果他们将不会使用嵌套的方式.
	 * @param element the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
	 *                ~$ 元素被解析成一个或多个{@link BeanDefinition BeanDefinition }
	 * @param parserContext the object encapsulating the current state of the parsing process;
	 * provides access to a {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 *                      ~$   对象封装的当前状态解析过程,提供了访问{@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 * @return the primary {@link BeanDefinition}
	 */
	BeanDefinition parse(Element element, ParserContext parserContext);

}
