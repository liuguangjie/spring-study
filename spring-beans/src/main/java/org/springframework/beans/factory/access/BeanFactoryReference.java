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

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;

/**
 * Used to track a reference to a {@link BeanFactory} obtained through
 * a {@link BeanFactoryLocator}.
 *
 * <p>It is safe to call {@link #release()} multiple times, but
 * {@link #getFactory()} must not be called after calling release.
 * ********************************************************************
 * ~$ 用于追踪引用{@link BeanFactory}通过一个{@link BeanFactoryLocator}
 * <p> 可以多次调用 {@link #release()} 但是{@link #getFactory()}
 * 		不可称为调用后释放
 *
 * @author Colin Sampaleanu
 * @see BeanFactoryLocator
 * @see org.springframework.context.access.ContextBeanFactoryReference
 */
public interface BeanFactoryReference {

	/**
	 * Return the {@link BeanFactory} instance held by this reference.
	 * **************************************************************
	 * ~$ 返回 {@link BeanFactory} 实列 由这个引用
	 * @throws IllegalStateException if invoked after <code>release()</code> has been called
	 */
	BeanFactory getFactory();

	/**
	 * Indicate that the {@link BeanFactory} instance referred to by this object is not
	 * needed any longer by the client code which obtained the {@link BeanFactoryReference}.
	 * <p>Depending on the actual implementation of {@link BeanFactoryLocator}, and
	 * the actual type of <code>BeanFactory</code>, this may possibly not actually
	 * do anything; alternately in the case of a 'closeable' <code>BeanFactory</code>
	 * or derived class (such as {@link org.springframework.context.ApplicationContext})
	 * may 'close' it, or may 'close' it once no more references remain.
	 * <p>In an EJB usage scenario this would normally be called from
	 * <code>ejbRemove()</code> and <code>ejbPassivate()</code>.
	 * <p>This is safe to call multiple times.
	 * *************************************************************************************
	 * ~$ 表明, {@link BeanFactory} 实例引用这个对象不再需要通过客户端代码获得了
	 *    {@link BeanFactoryReference}.
	 * <p> 这取决于的实际实现  {@link BeanFactoryLocator}, 和实际的类型 BeanFactory,
	 *     这可能不是真的做任何事情; 交替的情况下 'closeable' BeanFactory  或派生类
	 *     (如{@link org.springframework.context.ApplicationContext })可能“关闭”,
	 *     或“关闭”一次不再引用依然存在.
	 * <p> 在EJB使用场景这将通常被称为  ejbRemove()  和 ejbPassivate()
	 * <p> 这是安全调用多次.
	 * @throws FatalBeanException if the <code>BeanFactory</code> cannot be released
	 * @see BeanFactoryLocator
	 * @see org.springframework.context.access.ContextBeanFactoryReference
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	void release() throws FatalBeanException;

}
