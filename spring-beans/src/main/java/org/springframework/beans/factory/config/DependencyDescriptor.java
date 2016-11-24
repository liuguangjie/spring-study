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

package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

/**
 * Descriptor for a specific dependency that is about to be injected.
 * Wraps a constructor parameter, a method parameter or a field,
 * allowing unified access to their metadata.
 *
 * ******************************************************************
 * ~$ 描述符为一个特定的依赖,即将被注入。
 *    包装一个构造函数参数,方法参数或字段,允许统一访问他们的元数据。
 * @author Juergen Hoeller
 * @since 2.5
 */
public class DependencyDescriptor implements Serializable {

	private transient MethodParameter methodParameter;

	private transient Field field;

	private Class declaringClass;

	private String methodName;

	private Class[] parameterTypes;

	private int parameterIndex;

	private String fieldName;

	private final boolean required;

	private final boolean eager;

	private transient Annotation[] fieldAnnotations;


	/**
	 * Create a new descriptor for a method or constructor parameter.
	 * Considers the dependency as 'eager'.
	 * **************************************************************
	 * ~$ 创建一个新的描述符的方法或者构造函数的参数。认为“渴望”的依赖。
	 * @param methodParameter the MethodParameter to wrap
	 * @param required whether the dependency is required
	 */
	public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
		this(methodParameter, required, true);
	}

	/**
	 * Create a new descriptor for a method or constructor parameter.
	 * **************************************************************
	 * ~$ 创建一个新的描述符的方法或者构造函数的参数.
	 * @param methodParameter the MethodParameter to wrap
	 * @param required whether the dependency is required
	 * @param eager whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching
	 *              ~$ 这种依赖性是否“渴望”的急切解决潜在目标bean类型匹配
	 */
	public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		this.methodParameter = methodParameter;
		this.declaringClass = methodParameter.getDeclaringClass();
		if (this.methodParameter.getMethod() != null) {
			this.methodName = methodParameter.getMethod().getName();
			this.parameterTypes = methodParameter.getMethod().getParameterTypes();
		}
		else {
			this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
		}
		this.parameterIndex = methodParameter.getParameterIndex();
		this.required = required;
		this.eager = eager;
	}

	/**
	 * Create a new descriptor for a field.
	 * Considers the dependency as 'eager'.
	 * **************************************************
	 * ~$ 创建一个新的描述符字段.认为“渴望”的依赖.
	 * @param field the field to wrap
	 * @param required whether the dependency is required
	 */
	public DependencyDescriptor(Field field, boolean required) {
		this(field, required, true);
	}

	/**
	 * Create a new descriptor for a field.
	 * ************************************
	 * ~$ 创建一个新的描述符字段.
	 * @param field the field to wrap
	 * @param required whether the dependency is required
	 * @param eager whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching
	 */
	public DependencyDescriptor(Field field, boolean required, boolean eager) {
		Assert.notNull(field, "Field must not be null");
		this.field = field;
		this.declaringClass = field.getDeclaringClass();
		this.fieldName = field.getName();
		this.required = required;
		this.eager = eager;
	}


	/**
	 * Return the wrapped MethodParameter, if any.
	 * <p>Note: Either MethodParameter or Field is available.
	 * ******************************************************
	 * ~$ 返回包装MethodParameter,如果任何.
	 * <p>注意:MethodParameter或字段.
	 * @return the MethodParameter, or <code>null</code> if none
	 */
	public MethodParameter getMethodParameter() {
		return this.methodParameter;
	}

	/**
	 * Return the wrapped Field, if any.
	 * <p>Note: Either MethodParameter or Field is available.
	 * *****************************************************
	 * ~$ 返回包装领域,如果任何.
	 * <p>注意:MethodParameter或字段。
	 * @return the Field, or <code>null</code> if none
	 */
	public Field getField() {
		return this.field;
	}

	/**
	 * Return whether this dependency is required.
	 * *******************************************
	 * ~$ 返回是否需要这种依赖性
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * Return whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching.
	 * ***********************************************************
	 * ~$ 返回是否这种依赖性是'eager'的急切地解决潜在目标bean类型匹配.
	 */
	public boolean isEager() {
		return this.eager;
	}


	/**
	 * Initialize parameter name discovery for the underlying method parameter, if any.
	 * <p>This method does not actually try to retrieve the parameter name at
	 * this point; it just allows discovery to happen when the application calls
	 * {@link #getDependencyName()} (if ever).
	 * *********************************************************************************
	 * ~$ 初始化参数名称发现的潜在的方法参数,如果任何.
	 * <p>这个方法并不实际尝试检索参数名称在这一点上,
	 *    它只允许发现发生在应用程序调用{@link #getDependencyName()}(如果有).
	 */
	public void initParameterNameDiscovery(ParameterNameDiscoverer parameterNameDiscoverer) {
		if (this.methodParameter != null) {
			this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
		}
	}

	/**
	 * Determine the name of the wrapped parameter/field.
	 * **************************************************
	 * ~$ 确定包装的参数/字段的名称.
	 * @return the declared name (never <code>null</code>)
	 */
	public String getDependencyName() {
		return (this.field != null ? this.field.getName() : this.methodParameter.getParameterName());
	}

	/**
	 * Determine the declared (non-generic) type of the wrapped parameter/field.
	 * *************************************************************************
	 * ~$ 决定宣布(非泛型)类型的包装参数/字段。
	 * @return the declared type (never <code>null</code>)
	 */
	public Class<?> getDependencyType() {
		return (this.field != null ? this.field.getType() : this.methodParameter.getParameterType());
	}

	/**
	 * Determine the generic type of the wrapped parameter/field.
	 * **********************************************************
	 * ~$ 确定包装的泛型类型参数/字段。
	 * @return the generic type (never <code>null</code>)
	 */
	public Type getGenericDependencyType() {
		return (this.field != null ? this.field.getGenericType() : this.methodParameter.getGenericParameterType());
	}

	/**
	 * Determine the generic element type of the wrapped Collection parameter/field, if any.
	 * *************************************************************************************
	 * ~$ 确定包装收集参数的通用元素类型/字段,如果任何.
	 * @return the generic type, or <code>null</code> if none
	 */
	public Class<?> getCollectionType() {
		return (this.field != null ?
				GenericCollectionTypeResolver.getCollectionFieldType(this.field) :
				GenericCollectionTypeResolver.getCollectionParameterType(this.methodParameter));
	}

	/**
	 * Determine the generic key type of the wrapped Map parameter/field, if any.
	 * **************************************************************************
	 * ~$ 确定包装的通用密钥类型映射参数/字段,如果任何.
	 * @return the generic type, or <code>null</code> if none
	 */
	public Class<?> getMapKeyType() {
		return (this.field != null ?
				GenericCollectionTypeResolver.getMapKeyFieldType(this.field) :
				GenericCollectionTypeResolver.getMapKeyParameterType(this.methodParameter));
	}

	/**
	 * Determine the generic value type of the wrapped Map parameter/field, if any.
	 * ***************************************************************************
	 * ~$ 确定包装的通用密钥类型映射参数/字段,如果任何.
	 * @return the generic type, or <code>null</code> if none
	 */
	public Class<?> getMapValueType() {
		return (this.field != null ?
				GenericCollectionTypeResolver.getMapValueFieldType(this.field) :
				GenericCollectionTypeResolver.getMapValueParameterType(this.methodParameter));
	}

	/**
	 * Obtain the annotations associated with the wrapped parameter/field, if any.
	 * ***************************************************************************
	 * ~$ 获得与包装相关的注解参数/字段,如果任何。
	 */
	public Annotation[] getAnnotations() {
		if (this.field != null) {
			if (this.fieldAnnotations == null) {
				this.fieldAnnotations = this.field.getAnnotations();
			}
			return this.fieldAnnotations;
		}
		else {
			return this.methodParameter.getParameterAnnotations();
		}
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------
    /** 序列化支持*/
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization; just initialize state after deserialization.
		/** 依赖于默认的序列化,反序列化后初始化状态。*/
		ois.defaultReadObject();

		// Restore reflective handles (which are unfortunately not serializable)
		/** 恢复反射处理(不幸的是不序列化)*/
		try {
			if (this.fieldName != null) {
				this.field = this.declaringClass.getDeclaredField(this.fieldName);
			}
			else if (this.methodName != null) {
				this.methodParameter = new MethodParameter(
						this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
			}
			else {
				this.methodParameter = new MethodParameter(
						this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
			}
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Could not find original class structure", ex);
		}
	}

}
