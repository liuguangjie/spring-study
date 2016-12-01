/*
 * Copyright 2002-2010 the original author or authors.
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
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Editor for <code>java.net.URI</code>, to directly populate a URI property
 * instead of using a String property as bridge.
 *
 * <p>Supports Spring-style URI notation: any fully qualified standard URI
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL,
 * which will be resolved to a corresponding URI.
 *
 * <p>By default, this editor will encode Strings into URIs. For instance,
 * a space will be encoded into {@code %20}. This behavior can be changed
 * by calling the {@link #URIEditor(boolean)} constructor.
 *
 * <p>Note: A URI is more relaxed than a URL in that it does not require
 * a valid protocol to be specified. Any scheme within a valid URI syntax
 * is allowed, even without a matching protocol handler being registered.
 * ***********************************************************************
 * ~$ 编辑java.net.URI,直接填充一个URI属性而不是使用一个字符串属性作为桥梁.
 *
 * <p> 支持Spring风格的URI符号:任何完全限定标准URI("file:", "http:", etc)
 *     和Spring的特殊类路径:“pseudo-URL,将决定一个相应的URI.
 *
 * <p>默认情况下,这个编辑器将编码字符串到uri.例如,一个空间将被编码成{@code %20 }.
 *    这种行为可以改变通过调用{ @link # URIEditor(布尔)}构造函数.
 *
 * <p>注意:一个URI比一个URL更轻松,因为它不需要指定一个有效的协议.
 *    允许任何计划在一个有效的URI语法,即使没有一个匹配的协议处理器被注册.
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see URI
 * @see URLEditor
 */
public class URIEditor extends PropertyEditorSupport {

	private final ClassLoader classLoader;

	private final boolean encode;



	/**
	 * Create a new, encoding URIEditor, converting "classpath:" locations into
	 * standard URIs (not trying to resolve them into physical resources).
	 * ************************************************************************
	 * ~$ 编码URIEditor,创建一个新的"classpath:"地点转换成标准的uri(不是试图解决成物理资源).
	 */
	public URIEditor() {
		this.classLoader = null;
		this.encode = true;
	}

	/**
	 * Create a new URIEditor, converting "classpath:" locations into
	 * standard URIs (not trying to resolve them into physical resources).
	 * @param encode indicates whether Strings will be encoded or not
	 */
	public URIEditor(boolean encode) {
		this.classLoader = null;
		this.encode = encode;
	}


	/**
	 * Create a new URIEditor, using the given ClassLoader to resolve
	 * "classpath:" locations into physical resource URLs.
	 * ***************************************************************
	 * ~$ 创建一个新的URIEditor,使用给定的类加载器来解决"classpath:"地点到物理资源的url.
	 * @param classLoader the ClassLoader to use for resolving "classpath:" locations
	 * (may be <code>null</code> to indicate the default ClassLoader)
	 */
	public URIEditor(ClassLoader classLoader) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
		this.encode = true;
	}

	/**
	 * Create a new URIEditor, using the given ClassLoader to resolve
	 * "classpath:" locations into physical resource URLs.
	 * @param classLoader the ClassLoader to use for resolving "classpath:" locations
	 * (may be <code>null</code> to indicate the default ClassLoader)
	 * @param encode indicates whether Strings will be encoded or not
	 */
	public URIEditor(ClassLoader classLoader, boolean encode) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
		this.encode = encode;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			String uri = text.trim();
			if (this.classLoader != null && uri.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
				ClassPathResource resource =
						new ClassPathResource(uri.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length()), this.classLoader);
				try {
					String url = resource.getURL().toString();
					setValue(createURI(url));
				}
				catch (IOException ex) {
					throw new IllegalArgumentException("Could not retrieve URI for " + resource + ": " + ex.getMessage());
				}
				catch (URISyntaxException ex) {
					throw new IllegalArgumentException("Invalid URI syntax: " + ex);
				}
			}
			else {
				try {
					setValue(createURI(uri));
				}
				catch (URISyntaxException ex) {
					throw new IllegalArgumentException("Invalid URI syntax: " + ex);
				}
			}
		}
		else {
			setValue(null);
		}
	}

	/**
	 * Create a URI instance for the given (resolved) String value.
	 * <p>The default implementation encodes the value into a RFC
	 * 2396 compliant URI.
	 * ***********************************************************
	 * ~$ 创建一个URI实例为给定的字符串值(解决).
	 * <p>默认实现编码值到一个RFC 2396兼容的URI.
	 * @param value the value to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if URI conversion failed
	 */
	protected URI createURI(String value) throws URISyntaxException {
		int colonIndex = value.indexOf(':');
		if (this.encode && colonIndex != -1) {
			int fragmentIndex = value.indexOf('#', colonIndex + 1);
			String scheme = value.substring(0, colonIndex);
			String ssp = value.substring(colonIndex + 1, (fragmentIndex > 0 ? fragmentIndex : value.length()));
			String fragment = (fragmentIndex > 0 ? value.substring(fragmentIndex + 1) : null);
			return new URI(scheme, ssp, fragment);
		}
		else {
			// not encoding or the value contains no scheme - fallback to default
			/** 没有编码或价值不包含项目——后备违约 */
			return new URI(value);
		}
	}


	@Override
	public String getAsText() {
		URI value = (URI) getValue();
		return (value != null ? value.toString() : "");
	}

}
