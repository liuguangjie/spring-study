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

/**
 * Simple strategy allowing tools to control how source metadata is attached
 * to the bean definition metadata.
 *
 * <p>Configuration parsers <strong>may</strong> provide the ability to attach
 * source metadata during the parse phase. They will offer this metadata in a
 * generic format which can be further modified by a {@link SourceExtractor}
 * before being attached to the bean definition metadata.
 * ***************************************************************************
 * ~$ 简单的策略允许工具来控制源与bean定义元数据的元数据.
 *
 * <p> 配置解析器可以提供附加源解析阶段中元数据的能力.他们将提供此元数据在一个通用的格式,
 *     可以进一步修改的{@link SourceExtractor }在bean定义元数据.
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.BeanMetadataElement#getSource()
 * @see org.springframework.beans.factory.config.BeanDefinition
 */
public interface SourceExtractor {

	/**
	 * Extract the source metadata from the candidate object supplied
	 * by the configuration parser.
	 * ***************************************************************
	 * ~$ 从候选对象中提取元数据来源提供的配置解析器.
	 * @param sourceCandidate the original source metadata (never <code>null</code>)
	 * @param definingResource the resource that defines the given source object
	 * (may be <code>null</code>)
	 * @return the source metadata object to store (may be <code>null</code>)
	 */
	Object extractSource(Object sourceCandidate, Resource definingResource);

}
