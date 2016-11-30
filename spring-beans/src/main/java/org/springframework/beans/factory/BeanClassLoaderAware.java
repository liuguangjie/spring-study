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
 * Callback that allows a bean to be aware of the bean
 * {@link ClassLoader class loader}; that is, the class loader used by the
 * present bean factory to load bean classes.
 *
 * <p>This is mainly intended to be implemented by framework classes which
 * have to pick up application classes by name despite themselves potentially
 * being loaded from a shared class loader.
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 * ***************************************************************************
 * ~$ 回调,允许一个bean需要注意的bean类装入器{@link ClassLoader class loader};
 *    也就是说,目前所使用的类加载器加载bean工厂bean类.
 *
 * <p>这主要是为了实现的框架类必须自己挑选应用程序类的名字,尽管可能被从一个共享类加载器加载.
 *
 * <p>所有bean生命周期方法的列表,请参阅的{@link BeanFactory BeanFactory javadocs }.
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanClassLoaderAware extends Aware {

	/**
	 * Callback that supplies the bean {@link ClassLoader class loader} to
	 * a bean instance.
	 * <p>Invoked <i>after</i> the population of normal bean properties but
	 * <i>before</i> an initialization callback such as
	 * {@link InitializingBean InitializingBean's}
	 * {@link InitializingBean#afterPropertiesSet()}
	 * method or a custom init-method.
	 * ********************************************************************
	 * ~$ 回调提供bean类装入器{@link ClassLoader class loader} bean实例.
	 * <p>调用后正常的bean属性的人口但是在一个初始化的回调如{@link InitializingBean InitializingBean's}
	 *    {@link InitializingBean # afterPropertiesSet()}方法或一个定制的init方法.
	 * @param classLoader the owning class loader; may be <code>null</code> in
	 * which case a default <code>ClassLoader</code> must be used, for example
	 * the <code>ClassLoader</code> obtained via
	 *                    ~$ 拥有类装入器;可能是零在这种情况下,必须使用一个默认的类加载器,例如通过获得的类加载器
	 * {@link org.springframework.util.ClassUtils#getDefaultClassLoader()}
	 */
	void setBeanClassLoader(ClassLoader classLoader);

}
