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

package org.springframework.beans.factory.config;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A container object holding a map of attributes and optionally destruction callbacks. The callbacks will be invoked,
 * if an attribute is being removed or if the holder is cleaned out.
 *
 * *******************************************************************************************************************
 * ~$ 一个容器对象持有的地图属性和选择性地破坏回调。回调函数将会被调用,如果一个属性被移除或者持有人是清理。
 * @author Micha Kiener
 * @since 3.1
 */
public class DestructionAwareAttributeHolder implements Serializable {

	/** The map containing the registered attributes. */
	/** 这个Map包含注册属性.*/
	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	/**
	 * The optional map having any destruction callbacks registered using the
	 * name of the bean as the key.
	 * ***********************************************************************
	 * ~$  可选的Map有任何破坏回调注册使用bean的名称作为键。
	 */
	private Map<String, Runnable> registeredDestructionCallbacks;



	/**
	 * Returns the map representation of the registered attributes directly. Be
	 * aware to synchronize any invocations to it on the map object itself to
	 * avoid concurrent modification exceptions.
	 * ************************************************************************
	 * ~$ 直接返回注册的地图表示的属性。
	 *   注意同步地图上的任何调用对象本身,以避免并发修改异常。
	 *
	 * @return the attributes as a map representation
	 */
	public Map<String, Object> getAttributeMap() {
		return attributes;
	}

	/**
	 * Returns the attribute having the specified name, if available,
	 * <code>null</code> otherwise.
	 * **************************************************************
	 * ~$ 返回属性指定名称,如果可用,否则无效。
	 * @param name
	 *            the name of the attribute to be returned
	 * @return the attribute value or <code>null</code> if not available
	 */
	@SuppressWarnings("unchecked")
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Puts the given object with the specified name as an attribute to the
	 * underlying map.
	 * ********************************************************************
	 * ~$ 将给定对象与指定名称作为底层属性映射。
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the value to be stored
	 * @return any previously object stored under the same name, if any,
	 *         <code>null</code> otherwise
	 */
	@SuppressWarnings("unchecked")
	public Object setAttribute(String name, Object value) {
		return attributes.put(name, value);
	}

	/**
	 * Remove the object with the given <code>name</code> from the underlying
	 * scope.
	 * <p>
	 * Returns <code>null</code> if no object was found; otherwise returns the
	 * removed <code>Object</code>.
	 * <p>
	 * Note that an implementation should also remove a registered destruction
	 * callback for the specified object, if any. It does, however, <i>not</i>
	 * need to <i>execute</i> a registered destruction callback in this case,
	 * since the object will be destroyed by the caller (if appropriate).
	 * <p>
	 * <b>Note: This is an optional operation.</b> Implementations may throw
	 * {@link UnsupportedOperationException} if they do not support explicitly
	 * removing an object.
	 *
	 * *********************************************************************
	 * ~$ 删除的对象名字从底层范围.
	 * <p>返回null如果没有找到对象;否则返回删除的对象。
	 * <p>注意,一个实现也应该删除注册破坏指定对象回调,如果任何.
	 *     然而,它确实不需要执行注册回调在这种情况下,破坏自对象将被调用者(如果合适的话).
	 * <p>注意:这是一个可选的操作.实现可能会把{@link UnsupportedOperationException }方式,
	 * 	  如果他们不支持显式地删除一个对象
	 * @param name
	 *            the name of the object to remove
	 * @return the removed object, or <code>null</code> if no object was present
	 * @see #registerDestructionCallback
	 */
	@SuppressWarnings("unchecked")
	public Object removeAttribute(String name) {
		Object value = attributes.remove(name);

		// check for a destruction callback to be invoked
		Runnable callback = getDestructionCallback(name, true);
		if (callback != null) {
			callback.run();
		}

		return value;
	}

	/**
	 * Clears the map by removing all registered attribute values and invokes
	 * every destruction callback registered.
	 * ***********************************************************************
	 * ~$ 清除Map上通过删除所有注册属性值并调用每一个破坏回调注册。
	 */
	public void clear() {
		synchronized (this) {
			// step through the attribute map and invoke destruction callbacks,
			/** 一步通过毁灭属性映射和调用回调, */
			// if any
			if (registeredDestructionCallbacks != null) {
				for (Runnable runnable : registeredDestructionCallbacks.values()) {
					runnable.run();
				}

				registeredDestructionCallbacks.clear();
			}
		}

		// clear out the registered attribute map
		/** 清除注册属性映射*/
		attributes.clear();
	}

	/**
	 * Register a callback to be executed on destruction of the specified object
	 * in the scope (or at destruction of the entire scope, if the scope does
	 * not destroy individual objects but rather only terminates in its
	 * entirety).
	 * <p>
	 * <b>Note: This is an optional operation.</b> This method will only be
	 * called for scoped beans with actual destruction configuration
	 * (DisposableBean, destroy-method, DestructionAwareBeanPostProcessor).
	 * Implementations should do their best to execute a given callback at the
	 * appropriate time. If such a callback is not supported by the underlying
	 * runtime environment at all, the callback <i>must be ignored and a
	 * corresponding warning should be logged</i>.
	 * <p>
	 * Note that 'destruction' refers to to automatic destruction of the object
	 * as part of the scope's own lifecycle, not to the individual scoped object
	 * having been explicitly removed by the application. If a scoped object
	 * gets removed via this facade's {@link #removeAttribute(String)} method,
	 * any registered destruction callback should be removed as well, assuming
	 * that the removed object will be reused or manually destroyed.
	 *
	 * *************************************************************************
	 * ~$ 注册一个回调执行指定对象的破坏范围(或毁灭的整个范围,如果范围不破坏单个对象,而是只有终止全部)。
	 * <p>注意:这是一个可选的操作。这个方法只会要求与实际破坏范围的bean配置(DisposableBean,销毁方法,DestructionAwareBeanPostProcessor)。
	 *    实现他们应该尽最大努力去执行一个给定的回调在适当的时候。如果这样的由底层运行时环境不支持回调,回调必须忽略和相应的警告应该被记录。
	 * <p> 注意,“毁灭”是指自动破坏对象的范围的生命周期,而不是个人作用域的对象已经由应用程序显式地删除.
	 *     如果一个作用域的对象被删除通过立面的{@link #removeAttribute(String)}的方法,破坏任何注册回调也应该被移除,假设删除对象将被重用或手动销毁。
	 * @param name
	 *            the name of the object to execute the destruction callback for
	 * @param callback
	 *            the destruction callback to be executed. Note that the
	 *            passed-in Runnable will never throw an exception, so it can
	 *            safely be executed without an enclosing try-catch block.
	 *            Furthermore, the Runnable will usually be serializable,
	 *            provided that its target object is serializable as well.
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getDestroyMethodName()
	 * @see DestructionAwareBeanPostProcessor
	 */
	public void registerDestructionCallback(String name, Runnable callback) {
		if (registeredDestructionCallbacks == null) {
			registeredDestructionCallbacks = new ConcurrentHashMap<String, Runnable>();
		}

		registeredDestructionCallbacks.put(name, callback);
	}

	/**
	 * Returns the destruction callback, if any registered for the attribute
	 * with the given name or <code>null</code> if no such callback was
	 * registered.
	 * *********************************************************************
	 * ~$ 返回毁灭的回调,如果任何属性的名称或零如果没有这样的回调注册。
	 * @param name
	 *            the name of the registered callback requested
	 * @param remove
	 *            <code>true</code>, if the callback should be removed after
	 *            this call, <code>false</code>, if it stays
	 * @return the callback, if found, <code>null</code> otherwise
	 */
	public Runnable getDestructionCallback(String name, boolean remove) {
		if (registeredDestructionCallbacks == null) {
			return null;
		}

		if (remove) {
			return registeredDestructionCallbacks.remove(name);
		}
		
		return registeredDestructionCallbacks.get(name);
	}
}
