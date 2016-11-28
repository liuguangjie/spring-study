/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;

/**
 * Strategy interface used by a {@link ConfigurableBeanFactory},
 * representing a target scope to hold bean instances in.
 * This allows for extending the BeanFactory's standard scopes
 * {@link ConfigurableBeanFactory#SCOPE_SINGLETON "singleton"} and
 * {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE "prototype"}
 * with custom further scopes, registered for a
 * {@link ConfigurableBeanFactory#registerScope(String, Scope) specific key}.
 *
 * <p>{@link org.springframework.context.ApplicationContext} implementations
 * such as a {@link org.springframework.web.context.WebApplicationContext}
 * may register additional standard scopes specific to their environment,
 * e.g. {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST "request"}
 * and {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION "session"},
 * based on this Scope SPI.
 *
 * <p>Even if its primary use is for extended scopes in a web environment,
 * this SPI is completely generic: It provides the ability to get and put
 * objects from any underlying storage mechanism, such as an HTTP session
 * or a custom conversation mechanism. The name passed into this class's
 * <code>get</code> and <code>remove</code> methods will identify the
 * target object in the current scope.
 *
 * <p><code>Scope</code> implementations are expected to be thread-safe.
 * One <code>Scope</code> instance can be used with multiple bean factories
 * at the same time, if desired (unless it explicitly wants to be aware of
 * the containing BeanFactory), with any number of threads accessing
 * the <code>Scope</code> concurrently from any number of factories.
 *
 * ***************************************************************************************************
 * ~$ 策略接口使用{ @link ConfigurableBeanFactory },代表一个目标范围bean实例.
 *    这允许扩展BeanFactory的标准范围{@link ConfigurableBeanFactory #SCOPE_SINGLETON "singleton"}
 *   和{@link ConfigurableBeanFactory #SCOPE_PROTOTYPE "prototype"}进一步自定义范围,注册一个{@link ConfigurableBeanFactory #registerScope(字符串、范围)特定关键}.
 *
 * <p>即使它的主要用途是为扩展范围在web环境中,这种SPI是完全通用的:它提供了能力,
 *    把对象从任何底层存储机制,如一个HTTP会话或一个自定义的对话机制.名称传递到这个类的获取和删除方法将识别目标对象在当前的范围.
 *
 * <p>预计将实现线程安全的.一个范围实例可以同时使用多个bean工厂,如果需要(除非明确想要意识到包含BeanFactory),与任何数量的线程并发访问范围从任意数量的工厂.
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see ConfigurableBeanFactory#registerScope
 * @see CustomScopeConfigurer
 * @see org.springframework.aop.scope.ScopedProxyFactoryBean
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.request.SessionScope
 */
public interface Scope {

	/**
	 * Return the object with the given name from the underlying scope,
	 * {@link ObjectFactory#getObject() creating it}
	 * if not found in the underlying storage mechanism.
	 * <p>This is the central operation of a Scope, and the only operation
	 * that is absolutely required.
	 * ********************************************************************
	 * ~$ 返回的对象的名字从底层范围,{@link ObjectFactory #getObject() creating it}
	 *   如果不是发现在底层存储机制.
	 * <p> 这是中央的操作范围,唯一的操作是绝对必需的.
	 * @param name the name of the object to retrieve
	 * @param objectFactory the {@link ObjectFactory} to use to create the scoped
	 * object if it is not present in the underlying storage mechanism
	 * @return the desired object (never <code>null</code>)
	 */
	Object get(String name, ObjectFactory<?> objectFactory);

	/**
	 * Remove the object with the given <code>name</code> from the underlying scope.
	 * <p>Returns <code>null</code> if no object was found; otherwise
	 * returns the removed <code>Object</code>.
	 * <p>Note that an implementation should also remove a registered destruction
	 * callback for the specified object, if any. It does, however, <i>not</i>
	 * need to <i>execute</i> a registered destruction callback in this case,
	 * since the object will be destroyed by the caller (if appropriate).
	 * <p><b>Note: This is an optional operation.</b> Implementations may throw
	 * {@link UnsupportedOperationException} if they do not support explicitly
	 * removing an object.
	 * *******************************************************************************
	 * ~$ 删除的对象名字从底层范围.<p>返回null如果没有找到对象;否则返回删除的对象.
	 * <p>注意,一个实现也应该删除注册破坏指定对象回调,如果任何.
	 *    然而,它确实不需要执行注册回调在这种情况下,破坏自对象将被调用者(如果合适的话).
	 * <p>注意:这是一个可选的操作.实现可能会把{@link UnsupportedOperationException }方式,如果他们不支持显式地删除一个对象.
	 * @param name the name of the object to remove
	 * @return the removed object, or <code>null</code> if no object was present
	 * @see #registerDestructionCallback
	 */
	Object remove(String name);

	/**
	 * Register a callback to be executed on destruction of the specified
	 * object in the scope (or at destruction of the entire scope, if the
	 * scope does not destroy individual objects but rather only terminates
	 * in its entirety).
	 * <p><b>Note: This is an optional operation.</b> This method will only
	 * be called for scoped beans with actual destruction configuration
	 * (DisposableBean, destroy-method, DestructionAwareBeanPostProcessor).
	 * Implementations should do their best to execute a given callback
	 * at the appropriate time. If such a callback is not supported by the
	 * underlying runtime environment at all, the callback <i>must be
	 * ignored and a corresponding warning should be logged</i>.
	 * <p>Note that 'destruction' refers to to automatic destruction of
	 * the object as part of the scope's own lifecycle, not to the individual
	 * scoped object having been explicitly removed by the application.
	 * If a scoped object gets removed via this facade's {@link #remove(String)}
	 * method, any registered destruction callback should be removed as well,
	 * assuming that the removed object will be reused or manually destroyed.
	 * ****************************************************************************
	 * ~$ 注册一个回调执行指定对象的破坏范围(或销毁的整个范围,如果范围不破坏单个对象,而是只有终止全部).
	 * <p>注意:这是一个可选的操作.这个方法只会要求与实际破坏范围的bean配置(DisposableBean,销毁方法,DestructionAwareBeanPostProcessor).
	 * 实现他们应该尽最大努力去执行一个给定的回调在适当的时候.如果这样的由底层运行时环境不支持回调,回调必须忽略和相应的警告应该被记录.
	 * <p>注意,“毁灭”是指自动破坏对象的范围的生命周期,而不是个人作用域的对象已经由应用程序显式地删除.
	 *   如果一个作用域的对象被删除通过立面的{@link #remove(String)}方法,破坏任何注册回调也应该被移除,假设删除对象将被重用或手动销毁.
	 * @param name the name of the object to execute the destruction callback for
	 *             ~$    对象的名称执行破坏的回调
	 * @param callback the destruction callback to be executed.
	 * Note that the passed-in Runnable will never throw an exception,
	 * so it can safely be executed without an enclosing try-catch block.
	 * Furthermore, the Runnable will usually be serializable, provided
	 * that its target object is serializable as well.
	 *                 *****************************************************
	 *                 ~$破坏执行回调。注意,传入Runnable永远不会抛出异常,所以它可以安全地执行没有封闭的try-catch块.
	 *                 此外,可将通常是可序列化的,前提是它的目标对象是可序列化的。
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getDestroyMethodName()
	 * @see DestructionAwareBeanPostProcessor
	 */
	void registerDestructionCallback(String name, Runnable callback);

	/**
	 * Resolve the contextual object for the given key, if any.
	 * E.g. the HttpServletRequest object for key "request".
	 * ********************************************************
	 * ~$ 解决给定键的上下文对象,如果任何.例如HttpServletRequest对象关键"request".
	 * @param key the contextual key
	 * @return the corresponding object, or <code>null</code> if none found
	 */
	Object resolveContextualObject(String key);

	/**
	 * Return the <em>conversation ID</em> for the current underlying scope, if any.
	 * <p>The exact meaning of the conversation ID depends on the underlying
	 * storage mechanism. In the case of session-scoped objects, the
	 * conversation ID would typically be equal to (or derived from) the
	 * {@link javax.servlet.http.HttpSession#getId() session ID}; in the
	 * case of a custom conversation that sits within the overall session,
	 * the specific ID for the current conversation would be appropriate.
	 * <p><b>Note: This is an optional operation.</b> It is perfectly valid to
	 * return <code>null</code> in an implementation of this method if the
	 * underlying storage mechanism has no obvious candidate for such an ID.
	 * ********************************************************************************
	 * ~$ 返回的会话ID为当前潜在的范围,如果可用.
	 * <p>对话ID的确切含义取决于底层存储机制.在会话范围内的对象的情况下,
	 * 对话ID通常等于(或来自){@link javax.servlet.http.HttpSession # getId()会话ID };
	 * 在一个自定义对话,坐落在整个会话,特定的ID为当前的谈话将是合适的.
	 * <p>注意:这是一个可选的操作.返回null是完全有效的实现这个方法如果底层存储机制没有明显的候选人这样的ID.
	 * @return the conversation ID, or <code>null</code> if there is no
	 * conversation ID for the current scope
	 * ~$ 会话ID,或者零如果没有对话ID为当前的范围
	 */
	String getConversationId();

}
