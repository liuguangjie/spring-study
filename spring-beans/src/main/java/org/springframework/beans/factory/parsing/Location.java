/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Class that models an arbitrary location in a {@link Resource resource}.
 *
 * <p>Typically used to track the location of problematic or erroneous
 * metadata in XML configuration files. For example, a
 * {@link #getSource() source} location might be 'The bean defined on
 * line 76 of beans.properties has an invalid Class'; another source might
 * be the actual DOM Element from a parsed XML {@link org.w3c.dom.Document};
 * or the source object might simply be <code>null</code>.
 * **************************************************************************
 * ~$ 类模型任意位置{@link Resource resource}.
 *
 * <p> 通常用于跟踪的位置有问题或错误的元数据在XML配置文件中。例如,{@link #getSource() source}位置可能的bean上定义bean的第76行.
 *     属性无效类的,另一个原因可能是实际的DOM元素从解析XML {@link org.w3c.dom.Document};或源对象可能是null.
 * @author Rob Harrop
 * @since 2.0
 */
public class Location {

	private final Resource resource;

	private final Object source;


	/**
	 * Create a new instance of the {@link Location} class.
	 * @param resource the resource with which this location is associated
	 */
	public Location(Resource resource) {
		this(resource, null);
	}

	/**
	 * Create a new instance of the {@link Location} class.
	 * @param resource the resource with which this location is associated
	 * @param source the actual location within the associated resource
	 * (may be <code>null</code>)
	 */
	public Location(Resource resource, Object source) {
		Assert.notNull(resource, "Resource must not be null");
		this.resource = resource;
		this.source = source;
	}


	/**
	 * Get the resource with which this location is associated.
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * Get the actual location within the associated {@link #getResource() resource}
	 * (may be <code>null</code>).
	 * <p>See the {@link Location class level javadoc for this class} for examples
	 * of what the actual type of the returned object may be.
	 * *****************************************************************************
	 * ~$ 得到的实际位置相关联的{@link #getResource() resource} (可能是null).
	 *
	 * <p> 看到{@link Location class level javadoc for this class}为返回的对象的实际类型的例子.
	 */
	public Object getSource() {
		return this.source;
	}

}
