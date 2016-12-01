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

package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.xml.sax.InputSource;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * PropertyEditorRegistrar implementation that populates a given
 * {@link PropertyEditorRegistry}
 * (typically a {@link org.springframework.beans.BeanWrapper} used for bean
 * creation within an {@link org.springframework.context.ApplicationContext})
 * with resource editors. Used by
 * {@link org.springframework.context.support.AbstractApplicationContext}.
 * **************************************************************************
 * ~$ PropertyEditorRegistrar实现填充给定{@link PropertyEditorRegistry }
 *    (通常是{ @link org.springframework.beans。BeanWrapper }用于bean创建在一个
 *    {@link org.springframework.context.ApplicationContext })与资源编辑器.
 *    使用{@link org.springframework.context.support.AbstractApplicationContext }.
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 */
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {

	private final PropertyResolver propertyResolver;

	private final ResourceLoader resourceLoader;


	/**
	 * Create a new ResourceEditorRegistrar for the given {@link ResourceLoader}
	 * using a {@link StandardEnvironment}.
	 * *************************************************************************
	 * ~$ 创建一个新的ResourceEditorRegistrar给定{@link ResourceLoader }使用{@link StandardEnvironment }.
	 * @param resourceLoader the ResourceLoader (or ResourcePatternResolver)
	 * to create editors for (usually an ApplicationContext)
	 * @see ResourcePatternResolver
	 * @see org.springframework.context.ApplicationContext
	 * @deprecated as of Spring 3.1 in favor of
	 * {@link #ResourceEditorRegistrar(ResourceLoader, Environment)}
	 */
	@Deprecated
	public ResourceEditorRegistrar(ResourceLoader resourceLoader) {
		this(resourceLoader, new StandardEnvironment());
	}

	/**
	 * Create a new ResourceEditorRegistrar for the given ResourceLoader
	 * *****************************************************************
	 * ~$ 创建一个新的给定ResourceLoader ResourceEditorRegistrar
	 * @param resourceLoader the ResourceLoader (or ResourcePatternResolver)
	 * to create editors for (usually an ApplicationContext)
	 * @see ResourcePatternResolver
	 * @see org.springframework.context.ApplicationContext
	 */
	public ResourceEditorRegistrar(ResourceLoader resourceLoader, PropertyResolver propertyResolver) {
		this.resourceLoader = resourceLoader;
		this.propertyResolver = propertyResolver;
	}


	/**
	 * Populate the given <code>registry</code> with the following resource editors:
	 * ResourceEditor, InputStreamEditor, InputSourceEditor, FileEditor, URLEditor,
	 * URIEditor, ClassEditor, ClassArrayEditor.
	 * <p>If this registrar has been configured with a {@link ResourcePatternResolver},
	 * a ResourceArrayPropertyEditor will be registered as well.
	 * *******************************************************************************
	 * ~$ 给定的注册表填充下面的资源编辑器:ResourceEditor,InputStreamEditor,InputSourceEditor,FileEditor,URLEditor,URIEditor,ClassEditor ClassArrayEditor.
	 * <p>如果这个注册已经配置了一个{@link ResourcePatternResolver },ResourceArrayPropertyEditor将注册.
	 * @see ResourceEditor
	 * @see InputStreamEditor
	 * @see InputSourceEditor
	 * @see FileEditor
	 * @see URLEditor
	 * @see URIEditor
	 * @see ClassEditor
	 * @see ClassArrayEditor
	 * @see ResourceArrayPropertyEditor
	 */
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
		doRegisterEditor(registry, Resource.class, baseEditor);
		doRegisterEditor(registry, ContextResource.class, baseEditor);
		doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
		doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
		doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
		doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));

		ClassLoader classLoader = this.resourceLoader.getClassLoader();
		doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
		doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
		doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));

		if (this.resourceLoader instanceof ResourcePatternResolver) {
			doRegisterEditor(registry, Resource[].class,
					new ResourceArrayPropertyEditor((ResourcePatternResolver) this.resourceLoader, this.propertyResolver));
		}
	}

	/**
	 * Override default editor, if possible (since that's what we really mean to do here);
	 * otherwise register as a custom editor.
	 * ***********************************************************************************
	 * ~$ 覆盖默认编辑器,如果可能的话(因为这是我们真正的意思);否则注册为一个自定义编辑器.
	 */
	private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
		if (registry instanceof PropertyEditorRegistrySupport) {
			((PropertyEditorRegistrySupport) registry).overrideDefaultEditor(requiredType, editor);
		}
		else {
			registry.registerCustomEditor(requiredType, editor);
		}
	}

}
