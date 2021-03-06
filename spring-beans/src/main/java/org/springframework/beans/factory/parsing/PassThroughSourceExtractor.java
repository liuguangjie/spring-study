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
 * Simple {@link SourceExtractor} implementation that just passes
 * the candidate source metadata object through for attachment.
 *
 * <p>Using this implementation means that tools will get raw access to the
 * underlying configuration source metadata provided by the tool.
 *
 * <p>This implementation <strong>should not</strong> be used in a production
 * application since it is likely to keep too much metadata in memory
 * (unnecessarily).
 * ***************************************************************************
 * ~$ 仅仅通过简单的{@link SourceExtractor }实现元数据对象通过附件候选人来源.
 *
 * <p>使用此实现意味着工具将原始访问底层配置源提供的元数据工具.
 *
 * <p>这个实现不应使用在生产应用程序中,因为它可能会让太多的元数据在内存中(不必要的).
 * @author Rob Harrop
 * @since 2.0
 */
public class PassThroughSourceExtractor implements SourceExtractor {

	/**
	 * Simply returns the supplied <code>sourceCandidate</code> as-is.
	 * ***************************************************************
	 * ~$ 简单地返回sourceCandidate原有提供.
	 * @param sourceCandidate the source metadata
	 * @return the supplied <code>sourceCandidate</code>
	 */
	public Object extractSource(Object sourceCandidate, Resource definingResource) {
		return sourceCandidate;
	}

}
