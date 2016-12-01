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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Editor for <code>java.io.File</code>, to directly populate a File property
 * from a Spring resource location.
 *
 * <p>Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL.
 *
 * <p><b>NOTE:</b> The behavior of this editor has changed in Spring 2.0.
 * Previously, it created a File instance directly from a filename.
 * As of Spring 2.0, it takes a standard Spring resource location as input;
 * this is consistent with URLEditor and InputStreamEditor now.
 *
 * <p><b>NOTE:</b> In Spring 2.5 the following modification was made.
 * If a file name is specified without a URL prefix or without an absolute path
 * then we try to locate the file using standard ResourceLoader semantics.
 * If the file was not found, then a File instance is created assuming the file
 * name refers to a relative file location.
 * ****************************************************************************
 * ~$ 编辑器java.io.File,直接填充文件属性从Spring资源的位置.
 *
 * <p>支持Spring风格的URL符号:任何完全限定标准URL("file:", "http:", etc)
 *    和Spring的特殊"classpath:"pseudo-URL.
 *
 * <p>注意:这个编辑器的行为改变了在Spring 2.0中.以前,它创建了一个文件从一个文件名直接实例.
 *    Spring 2.0,需要一个标准的Spring资源位置作为输入;这是符合URLEditor InputStreamEditor现在.
 *
 * <p>注意:在Spring 2.5中做以下修改.如果指定一个文件名没有URL前缀或没有一个绝对路径然后我们试图找到文件使用标准ResourceLoader语义.
 *    如果文件不存在,则创建一个文件实例假设文件名称指的是一个相对的文件位置.
 * @author Juergen Hoeller
 * @author Thomas Risberg
 * @since 09.12.2003
 * @see File
 * @see ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see URLEditor
 * @see InputStreamEditor
 */
public class FileEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new FileEditor,
	 * using the default ResourceEditor underneath.
	 * ********************************************
	 * ~$ 创建一个新的FileEditor,使用默认ResourceEditor下面.
	 */
	public FileEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new FileEditor,
	 * using the given ResourceEditor underneath.
	 * @param resourceEditor the ResourceEditor to use
	 */
	public FileEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(null);
			return;
		}

		// Check whether we got an absolute file path without "file:" prefix.
		/** 检查是否我们绝对没有"file:"前缀的文件路径. */
		// For backwards compatibility, we'll consider those as straight file path.
		/** 对于向后兼容性,我们将认为这些文件路径. */
		if (!ResourceUtils.isUrl(text)) {
			File file = new File(text);
			if (file.isAbsolute()) {
				setValue(file);
				return;
			}
		}

		// Proceed with standard resource location parsing.
		/** 进行标准的资源位置的解析. */
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();

		// If it's a URL or a path pointing to an existing resource, use it as-is.
		/** 如果它是一个URL或路径指向现有资源,按原样使用它. */
		if (ResourceUtils.isUrl(text) || resource.exists()) {
			try {
				setValue(resource.getFile());
			}
			catch (IOException ex) {
				throw new IllegalArgumentException(
						"Could not retrieve File for " + resource + ": " + ex.getMessage());
			}
		}
		else {
			// Create a relative File reference and hope for the best.
			/** 创建一个相对文件参考,希望最好的.*/
			setValue(new File(text));
		}
	}

	@Override
	public String getAsText() {
		File value = (File) getValue();
		return (value != null ? value.getPath() : "");
	}

}
