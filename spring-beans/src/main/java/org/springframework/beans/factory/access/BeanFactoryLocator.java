/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.beans.factory.access;

import org.springframework.beans.BeansException;

/**
 * Defines a contract for the lookup, use, and release of a
 * {@link org.springframework.beans.factory.BeanFactory},
 * or a <code>BeanFactory</code> subclass such as an
 * {@link org.springframework.context.ApplicationContext}.
 *
 * <p>Where this interface is implemented as a singleton class such as
 * {@link SingletonBeanFactoryLocator}, the Spring team <strong>strongly</strong>
 * suggests that it be used sparingly and with caution. By far the vast majority
 * of the code inside an application is best written in a Dependency Injection
 * style, where that code is served out of a
 * <code>BeanFactory</code>/<code>ApplicationContext</code> container, and has
 * its own dependencies supplied by the container when it is created. However,
 * even such a singleton implementation sometimes has its use in the small glue
 * layers of code that is sometimes needed to tie other code together. For
 * example, third party code may try to construct new objects directly, without
 * the ability to force it to get these objects out of a <code>BeanFactory</code>.
 * If the object constructed by the third party code is just a small stub or
 * proxy, which then uses an implementation of this class to get a
 * <code>BeanFactory</code> from which it gets the real object, to which it
 * delegates, then proper Dependency Injection has been achieved.
 *
 * <p>As another example, in a complex J2EE app with multiple layers, with each
 * layer having its own <code>ApplicationContext</code> definition (in a
 * hierarchy), a class like <code>SingletonBeanFactoryLocator</code> may be used
 * to demand load these contexts.
 *
 * ******************************************************************************
 * ~$ 定义了一个合同查找.
 *    使用和发布的{@link org.springframework.beans.factory.BeanFactory },
 *    或 一个 <code>BeanFactory</code> 子类
 *    {@link org.springframework.context.ApplicationContext}.
 * <p> 这个接口被实现为一个单例类,如{@link SingletonBeanFactoryLocator },
 *     spring 团队 强烈 建议 谨慎和小心使用
 * 	   迄今为止绝大多数的应用程序中的代码是最好写的依赖注入的风格,
 * 	   这段代码在哪里服务的 <code>BeanFactory</code>/<code>ApplicationContext</code> 容器
 * 	   和有自己的依赖关系由容器时创建的。然而,
 * 	   即使这样一个单例实现有时有其使用的小胶水层代码,有时需要与其他代码捆绑在一起的。
 * 	   例如,第三方代码可能试图直接构造新的对象,
 * 	   没有迫使它的能力让这些对象的<code>BeanFactory</code>
 * 	   如果对象由第三方代码只是一个小的存根或代理,
 * 	   然后使用这个类的实现得到BeanFactory
 * 	   它真正的对象,它代表,然后适当的依赖注入已经实现。
 * @author Colin Sampaleanu
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.context.access.DefaultLocatorFactory
 * @see org.springframework.context.ApplicationContext
 */
public interface BeanFactoryLocator {

	/**
	 * Use the {@link org.springframework.beans.factory.BeanFactory} (or derived
	 * interface such as {@link org.springframework.context.ApplicationContext})
	 * specified by the <code>factoryKey</code> parameter.
	 * <p>The definition is possibly loaded/created as needed.
	 * @param factoryKey a resource name specifying which <code>BeanFactory</code> the
	 * <code>BeanFactoryLocator</code> must return for usage. The actual meaning of the
	 * resource name is specific to the implementation of <code>BeanFactoryLocator</code>.
	 * ***********************************************************************************
	 * ~$ 使用{@link org.springframework.beans.factory.BeanFactory }
	 *               (或派生接口如{@link org.springframework.context.ApplicationContext })
	 *    指定的<code>factoryKey</code>参数.
	 * <p> 根据需要定义可能是加载/创建 .
	 *
	 * @param factoryKey 资源名称指定 <code>BeanFactory</code> <code>BeanFactoryLocator</code>
	 * 					 必须为使用返回. 资源的实际意义的名字是特定于实现的 <code>BeanFactoryLocator</code>
	 *
	 * @return the <code>BeanFactory</code> instance, wrapped as a {@link BeanFactoryReference} object
	 * @throws BeansException if there is an error loading or accessing the <code>BeanFactory</code>
	 */
	BeanFactoryReference useBeanFactory(String factoryKey) throws BeansException;

}
