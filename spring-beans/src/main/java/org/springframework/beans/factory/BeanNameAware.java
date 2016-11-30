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

package org.springframework.beans.factory;

/**
 * Interface to be implemented by beans that want to be aware of their
 * bean name in a bean factory. Note that it is not usually recommended
 * that an object depend on its bean name, as this represents a potentially
 * brittle dependence on external configuration, as well as a possibly
 * unnecessary dependence on a Spring API.
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 * *************************************************************************
 * ~$ 接口由bean实现,要意识到自己的在一个bean工厂bean的名字.
 *    注意,它通常不建议一个对象依赖于它的bean的名字,这代表了一种潜在的脆弱的依赖外部配置,以及可能不必要的依赖Spring API.
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 01.11.2003
 * @see BeanClassLoaderAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanNameAware extends Aware {

	/**
	 * Set the name of the bean in the bean factory that created this bean.
	 * <p>Invoked after population of normal bean properties but before an
	 * init callback such as {@link InitializingBean#afterPropertiesSet()}
	 * or a custom init-method.
	 * *******************************************************************
	 * ~$ 设置bean的名称在这个bean创建bean工厂.
	 * <p>调用后人口正常的bean属性但在init调如{@link InitializingBean # afterPropertiesSet()}或一个定制的init方法.
	 * @param name the name of the bean in the factory.
	 * Note that this name is the actual bean name used in the factory, which may
	 * differ from the originally specified name: in particular for inner bean
	 * names, the actual bean name might have been made unique through appending
	 * "#..." suffixes. Use the {@link BeanFactoryUtils#originalBeanName(String)}
	 * method to extract the original bean name (without suffix), if desired.
	 *  ***************************************************************************
	 *  ~$ 注意,这个名字是实际的bean名称中使用的工厂,这可能不同于最初指定的名称:
	 *     尤其是内在bean名称,实际的bean名称可能通过添加了独特的"#..."后缀.
	 *     使用{ @link BeanFactoryUtils # originalBeanName(字符串)}方法提取原始bean名称(没有后缀),如果需要的话.
	 */
	void setBeanName(String name);

}
