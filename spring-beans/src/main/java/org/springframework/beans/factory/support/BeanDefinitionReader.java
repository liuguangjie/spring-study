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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Simple interface for bean definition readers.
 * Specifies load methods with Resource parameters.
 *
 * <p>Concrete bean definition readers can of course add additional
 * load and register methods for bean definitions, specific to
 * their bean definition format.
 *
 * <p>Note that a bean definition reader does not have to implement
 * this interface. It only serves as suggestion for bean definition
 * readers that want to follow standard naming conventions.
 * ****************************************************************
 * ~$ 简单的界面为bean定义的读者.指定加载方法与资源参数.
 *
 * <p>混凝土bean定义的读者当然可以添加额外的负载和注册方法bean定义,特定的bean定义格式.
 *
 * <p>注意,一个bean定义读者不需要实现该接口.它只作为建议为bean定义的读者,要遵循标准命名约定.
 * @author Juergen Hoeller
 * @since 1.1
 * @see Resource
 */
public interface BeanDefinitionReader {

	/**
	 * Return the bean factory to register the bean definitions with.
	 * <p>The factory is exposed through the BeanDefinitionRegistry interface,
	 * encapsulating the methods that are relevant for bean definition handling.
	 * *************************************************************************
	 * ~$ 返回bean工厂注册的bean定义
	 * <p>工厂暴露通过BeanDefinitionRegistry接口,封装相关的bean定义的方法处理
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * Return the resource loader to use for resource locations.
	 * Can be checked for the <b>ResourcePatternResolver</b> interface and cast
	 * accordingly, for loading multiple resources for a given resource pattern.
	 * <p>Null suggests that absolute resource loading is not available
	 * for this bean definition reader.
	 * <p>This is mainly meant to be used for importing further resources
	 * from within a bean definition resource, for example via the "import"
	 * tag in XML bean definitions. It is recommended, however, to apply
	 * such imports relative to the defining resource; only explicit full
	 * resource locations will trigger absolute resource loading.
	 * <p>There is also a <code>loadBeanDefinitions(String)</code> method available,
	 * for loading bean definitions from a resource location (or location pattern).
	 * This is a convenience to avoid explicit ResourceLoader handling.
	 * ******************************************************************************
	 * ~$ 返回资源加载器使用的资源的位置.可以检查ResourcePatternResolver接口,因此,对于一个给定的资源模式加载多个资源.
	 * <p>零意味着绝对的资源加载不是可利用为这个bean定义的读者.
	 * <p>这主要是用于进口进一步资源在bean定义资源,例如通过XML bean定义的"import"标签.
	 *    然而,建议申请这样的进口相对于定义资源;只有明确完整的资源位置将触发绝对资源加载.
	 * <p>还有一个loadBeanDefinitions(String)方法,加载bean定义从一个资源位置(或模式)位置.
	 * 这是一个方便,避免显式ResourceLoader处理.
	 *
	 * @see #loadBeanDefinitions(String)
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 */
	ResourceLoader getResourceLoader();

	/**
	 * Return the class loader to use for bean classes.
	 * <p><code>null</code> suggests to not load bean classes eagerly
	 * but rather to just register bean definitions with class names,
	 * with the corresponding Classes to be resolved later (or never).
	 * ***************************************************************
	 * ~$ 返回bean类的类装入器使用.
	 * <p>零建议热切而的加载bean类注册的bean定义类名,与相应的类来解决后(或没有).
	 */
	ClassLoader getBeanClassLoader();

	/**
	 * Return the BeanNameGenerator to use for anonymous beans
	 * (without explicit bean name specified).
	 * *******************************************************
	 * ~$ 返回BeanNameGenerator使用匿名bean.
	 * (没有显式指定bean名称)
	 */
	BeanNameGenerator getBeanNameGenerator();


	/**
	 * Load bean definitions from the specified resource.
	 * **************************************************
	 * ~$ 从指定的加载bean定义的资源.
	 * @param resource the resource descriptor  ~$ 资源描述符
	 * @return the number of bean definitions found  ~$ bean定义发现的数量
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resources.
	 * @param resources the resource descriptors
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resource location.
	 * <p>The location can also be a location pattern, provided that the
	 * ResourceLoader of this bean definition reader is a ResourcePatternResolver.
	 * ***************************************************************************
	 * ~$ 从指定的资源加载bean定义的位置.
	 * <p>提供的位置也可以是位置模式,这个bean定义的ResourceLoader读者ResourcePatternResolver.
	 * @param location the resource location, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(Resource)
	 * @see #loadBeanDefinitions(Resource[])
	 */
	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resource locations.
	 * @param locations the resource locations, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;

}
