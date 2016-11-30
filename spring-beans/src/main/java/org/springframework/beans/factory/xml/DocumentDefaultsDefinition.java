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

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.parsing.DefaultsDefinition;

/**
 * Simple JavaBean that holds the defaults specified at the <code>&lt;beans&gt;</code>
 * level in a standard Spring XML bean definition document:
 * <code>default-lazy-init</code>, <code>default-autowire</code>, etc.
 * ***********************************************************************************
 * ~$ 简单的JavaBean,它拥有默认值指定的bean级别标准的Spring XML bean定义文档:default-lazy-init,default-autowire等等.
 * @author Juergen Hoeller
 * @since 2.0.2
 */
public class DocumentDefaultsDefinition implements DefaultsDefinition {

	private String lazyInit;

	private String merge;

	private String autowire;

	private String dependencyCheck;

	private String autowireCandidates;

	private String initMethod;

	private String destroyMethod;

	private Object source;


	/**
	 * Set the default lazy-init flag for the document that's currently parsed.
	 * ************************************************************************
	 * ~$ 设置默认lazy-init 标志目前文档的解析.
	 */
	public void setLazyInit(String lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Return the default lazy-init flag for the document that's currently parsed.
	 * **************************************************************************
	 * ~$ 返回默认lazy-init 标志目前文档的解析.
	 */
	public String getLazyInit() {
		return this.lazyInit;
	}

	/**
	 * Set the default merge setting for the document that's currently parsed.
	 * ***********************************************************************
	 * ~$ 设置默认为当前解析的文档合并设置。
	 */
	public void setMerge(String merge) {
		this.merge = merge;
	}

	/**
	 * Return the default merge setting for the document that's currently parsed.
	 * **************************************************************************
	 * ~$  返回默认为当前解析的文档合并设置。
	 */
	public String getMerge() {
		return this.merge;
	}

	/**
	 * Set the default autowire setting for the document that's currently parsed.
	 * **************************************************************************
	 * ~$ 设置默认为当前解析的文档自动装配设置.
	 */
	public void setAutowire(String autowire) {
		this.autowire = autowire;
	}

	/**
	 * Return the default autowire setting for the document that's currently parsed.
	 * *****************************************************************************
	 * ~$ 返回默认为当前解析的文档自动装配设置.
	 */
	public String getAutowire() {
		return this.autowire;
	}

	/**
	 * Set the default dependency-check setting for the document that's currently parsed.
	 * **********************************************************************************
	 * ~$ 设置默认的依赖性检查设置的文档目前解析.
	 */
	public void setDependencyCheck(String dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	/**
	 * Return the default dependency-check setting for the document that's currently parsed.
	 * *************************************************************************************
	 * ~$ 返回默认的依赖性检查设置的文档目前解析.
	 */
	public String getDependencyCheck() {
		return this.dependencyCheck;
	}

	/**
	 * Set the default autowire-candidate pattern for the document that's currently parsed.
	 * Also accepts a comma-separated list of patterns.
	 * ************************************************************************************
	 * ~$ 设置默认目前autowire-candidate模式文档的解析.还接受一个以逗号分隔的模式.
	 */
	public void setAutowireCandidates(String autowireCandidates) {
		this.autowireCandidates = autowireCandidates;
	}

	/**
	 * Return the default autowire-candidate pattern for the document that's currently parsed.
	 * May also return a comma-separated list of patterns.
	 * **************************************************************************************
	 * ~$ 返回默认目前autowire-candidate模式文档的解析.也可以返回一个以逗号分隔的模式.
	 */
	public String getAutowireCandidates() {
		return this.autowireCandidates;
	}

	/**
	 * Set the default init-method setting for the document that's currently parsed.
	 * *****************************************************************************
	 * ~$ 设置默认的init方法设置为文档的解析.
	 */
	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	/**
	 * Return the default init-method setting for the document that's currently parsed.
	 * ********************************************************************************
	 * ~$ 返回默认的init方法设置为文档的解析.
	 */
	public String getInitMethod() {
		return this.initMethod;
	}

	/**
	 * Set the default destroy-method setting for the document that's currently parsed.
	 * ********************************************************************************
	 * ~$ 设置默认为当前解析的文档销毁方法设置.
	 */
	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	/**
	 * Return the default destroy-method setting for the document that's currently parsed.
	 * ***********************************************************************************
	 * ~$ 返回默认的销毁方法设置为文档的解析.
	 */
	public String getDestroyMethod() {
		return this.destroyMethod;
	}

	/**
	 * Set the configuration source <code>Object</code> for this metadata element.
	 * <p>The exact type of the object will depend on the configuration mechanism used.
	 * ********************************************************************************
	 * ~$ 这个元数据元素的配置源对象.
	 * <p>对象的确切类型将取决于所使用的配置机制.
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return this.source;
	}

}
