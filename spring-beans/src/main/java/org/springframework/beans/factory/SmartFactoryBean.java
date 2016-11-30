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

package org.springframework.beans.factory;

/**
 * Extension of the {@link FactoryBean} interface. Implementations may
 * indicate whether they always return independent instances, for the
 * case where their {@link #isSingleton()} implementation returning
 * <code>false</code> does not clearly indicate independent instances.
 *
 * <p>Plain {@link FactoryBean} implementations which do not implement
 * this extended interface are simply assumed to always return independent
 * instances if their {@link #isSingleton()} implementation returns
 * <code>false</code>; the exposed object is only accessed on demand.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework and within collaborating frameworks.
 * In general, application-provided FactoryBeans should simply implement
 * the plain {@link FactoryBean} interface. New methods might be added
 * to this extended interface even in point releases.
 * **************************************************************************
 * ~$ 扩展的{@link FactoryBean }接口.实现可能表明他们是否总是返回独立的情况下,
 *    如果他们{@link #isSingleton()}实现返回false并不标明独立实例.
 *
 * <p>平原{@link FactoryBean }不实现这个扩展接口的实现仅仅是认为如果他们总是返回独立实例
 *    {@link #isSingleton()}实现返回false;只暴露对象访问需求.
 *
 * <p>注意:这个接口是一个专用的接口,主要用于内部使用框架内和合作框架.
 *    一般来说,内部FactoryBeans应该简单地实现纯{@link FactoryBean }接口.
 *    新方法甚至可能被添加到这个扩展接口的版本.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see #isPrototype()
 * @see #isSingleton()
 */
public interface SmartFactoryBean<T> extends FactoryBean<T> {

	/**
	 * Is the object managed by this factory a prototype? That is,
	 * will {@link #getObject()} always return an independent instance?
	 * <p>The prototype status of the FactoryBean itself will generally
	 * be provided by the owning {@link BeanFactory}; usually, it has to be
	 * defined as singleton there.
	 * <p>This method is supposed to strictly check for independent instances;
	 * it should not return <code>true</code> for scoped objects or other
	 * kinds of non-singleton, non-independent objects. For this reason,
	 * this is not simply the inverted form of {@link #isSingleton()}.
	 * ***********************************************************************
	 * ~$ 由这个工厂管理的对象是一个产品原型?也就是说,将{@link #getObject()}总是返回一个独立实例吗?
	 * <p>FactoryBean本身的原型状态通常会由拥有{ @link BeanFactory };通常,它必须被定义为单例.
	 * <p>这种方法应该严格检查独立实例;它不应该返回对作用域对象或其他类型的单体,诱致性对象.
	 *    出于这个原因,这不是简单的反向形式{@link #isSingleton()}.
	 * @return whether the exposed object is a prototype
	 * @see #getObject()
	 * @see #isSingleton()
	 */
	boolean isPrototype();

	/**
	 * Does this FactoryBean expect eager initialization, that is,
	 * eagerly initialize itself as well as expect eager initialization
	 * of its singleton object (if any)?
	 * <p>A standard FactoryBean is not expected to initialize eagerly:
	 * Its {@link #getObject()} will only be called for actual access, even
	 * in case of a singleton object. Returning <code>true</code> from this
	 * method suggests that {@link #getObject()} should be called eagerly,
	 * also applying post-processors eagerly. This may make sense in case
	 * of a {@link #isSingleton() singleton} object, in particular if
	 * post-processors expect to be applied on startup.
	 * *********************************************************************
	 * ~$ 这FactoryBean期待渴望的初始化,急切地初始化本身以及期望渴望单例对象的初始化(如果有的话)?
	 * <p> 标准FactoryBean预计不会急切地初始化:其{@link #getObject()}只会要求实际访问,即使在一个单例对象.
	 *     该方法返回true表明,{@link #getObject()}应该叫热切,也急切地应用后处理器.
	 *     这也许是有意义的对于{@link #isSingleton() singleton}对象,特别是如果后处理器预计在启动时应用.
	 * @return whether eager initialization applies
	 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
	 */
	boolean isEagerInit();

}
