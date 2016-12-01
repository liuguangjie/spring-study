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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URL;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;

/**
 * Editor for <code>java.net.URL</code>, to directly populate a URL property
 * instead of using a String property as bridge.
 *
 * <p>Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL,
 * as well as Spring's context-specific relative file paths.
 *
 * <p>Note: A URL must specify a valid protocol, else it will be rejected
 * upfront. However, the target resource does not necessarily have to exist
 * at the time of URL creation; this depends on the specific resource type.
 * *************************************************************************
 * ~$ 编辑java.net.URL,直接填充一个URL属性而不是使用一个字符串属性作为桥梁.
 *
 * <p>支持Spring风格的URL符号:任何完全限定标准URL("file:", "http:",等)
 *    和Spring的特殊"classpath:" pseudo-URL以及Spring上下文特定的相对文件路径.
 *
 * <p>注意:一个URL必须指定一个有效的协议,否则它将被拒绝的前期.
 *    然而,目标资源并不一定存在的URL创建;这取决于特定的资源类型.
 * @author Juergen Hoeller
 * @since 15.12.2003
 * @see URL
 * @see ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see FileEditor
 * @see InputStreamEditor
 */
public class URLEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new URLEditor, using the default ResourceEditor underneath.
	 * ********************************************************************
	 * ~$ 创建一个新的URLEditor,使用默认ResourceEditor下面.
	 */
	public URLEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new URLEditor, using the given ResourceEditor underneath.
	 * ******************************************************************
	 * ~$ 创建一个新的URLEditor,使用给定的ResourceEditor下面.
	 * @param resourceEditor the ResourceEditor to use
	 */
	public URLEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();
		try {
			setValue(resource != null ? resource.getURL() : null);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Could not retrieve URL for " + resource + ": " + ex.getMessage());
		}
	}

	@Override
	public String getAsText() {
		URL value = (URL) getValue();
		return (value != null ? value.toExternalForm() : "");
	}

}
