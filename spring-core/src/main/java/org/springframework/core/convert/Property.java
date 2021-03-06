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
package org.springframework.core.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * A description of a JavaBeans Property that allows us to avoid a dependency on
 * <code>java.beans.PropertyDescriptor</code>. The <code>java.beans</code> package
 * is not available in a number of environments (e.g. Android, Java ME), so this is
 * desirable for portability of Spring's core conversion facility.
 *
 * <p>Used to build a TypeDescriptor from a property location.
 * The built TypeDescriptor can then be used to convert from/to the property type.
 * *******************************************************************************
 * ~$ 一个javabean属性的描述,使我们能够避免依赖java.beans.PropertyDescriptor.
 *    java.bean包不可用在许多环境中(例如安卓,Java ME),这是Spring的理想的可移植性的核心转换设备.
 *
 * <p>用于构建TypeDescriptor从一个属性的位置.构建TypeDescriptor可以用来转换从/到属性类型.
 * @author Keith Donald
 * @since 3.1
 * @see TypeDescriptor#TypeDescriptor(Property)
 * @see TypeDescriptor#nested(Property, int)
 */
public final class Property {

	private final Class<?> objectType;

	private final Method readMethod;

	private final Method writeMethod;

	private final String name;
	
	private final MethodParameter methodParameter;

	private final Annotation[] annotations;


	public Property(Class<?> objectType, Method readMethod, Method writeMethod) {
		this.objectType = objectType;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.methodParameter = resolveMethodParameter();
		this.name = resolveName();
		this.annotations = resolveAnnotations();
	}


	/**
	 * The object declaring this property, either directly or in a superclass the object extends.
	 * *********************************************************************
	 * ~$ 对象声明这个属性,直接或在一个超类对象扩展.
	 */
	public Class<?> getObjectType() {
		return this.objectType;
	}

	/**
	 * The name of the property: e.g. 'foo'
	 * ************************************
	 * ~$ 属性的名称:例如“foo”
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The property type: e.g. <code>java.lang.String</code>
	 * *****************************************************
	 * ~$ 属性类型:如以 java.lang.String
	 */
	public Class<?> getType() {
		return this.methodParameter.getParameterType();
	}

	/**
	 * The property getter method: e.g. <code>getFoo()</code>
	 */
	public Method getReadMethod() {
		return this.readMethod;
	}

	/**
	 * The property setter method: e.g. <code>setFoo(String)</code>
	 */
	public Method getWriteMethod() {
		return this.writeMethod;
	}


	// package private
	
	MethodParameter getMethodParameter() {
		return this.methodParameter;
	}

	Annotation[] getAnnotations() {
		return this.annotations;
	}


	// internal helpers
	
	private String resolveName() {
		if (this.readMethod != null) {
			int index = this.readMethod.getName().indexOf("get");
			if (index != -1) {
				index += 3;
			}
			else {
				index = this.readMethod.getName().indexOf("is");
				if (index == -1) {
					throw new IllegalArgumentException("Not a getter method");
				}
				index += 2;
			}
			return StringUtils.uncapitalize(this.readMethod.getName().substring(index));
		}
		else {
			int index = this.writeMethod.getName().indexOf("set") + 3;
			if (index == -1) {
				throw new IllegalArgumentException("Not a setter method");
			}
			return StringUtils.uncapitalize(this.writeMethod.getName().substring(index));
		}
	}

	private MethodParameter resolveMethodParameter() {
		MethodParameter read = resolveReadMethodParameter();
		MethodParameter write = resolveWriteMethodParameter();
		if (read == null && write == null) {
			throw new IllegalStateException("Property is neither readable nor writeable");
		}
		if (read != null && write != null && !write.getParameterType().isAssignableFrom(read.getParameterType())) {
			throw new IllegalStateException("Write parameter is not assignable from read parameter");
		}
		return read != null ? read : write;
	}
	
	private MethodParameter resolveReadMethodParameter() {
		if (getReadMethod() == null) {
			return null;
		}
		return resolveParameterType(new MethodParameter(getReadMethod(), -1));			
	}

	private MethodParameter resolveWriteMethodParameter() {
		if (getWriteMethod() == null) {
			return null;
		}
		return resolveParameterType(new MethodParameter(getWriteMethod(), 0));			
	}

	private MethodParameter resolveParameterType(MethodParameter parameter) {
		// needed to resolve generic property types that parameterized by sub-classes e.g. T getFoo();
		/** 需要解决一般的财产类型参数化的子类例如T getFoo(); */
		GenericTypeResolver.resolveParameterType(parameter, getObjectType());
		return parameter;			
	}
	
	private Annotation[] resolveAnnotations() {
		Map<Class<?>, Annotation> annMap = new LinkedHashMap<Class<?>, Annotation>();
		Method readMethod = getReadMethod();
		if (readMethod != null) {
			for (Annotation ann : readMethod.getAnnotations()) {
				annMap.put(ann.annotationType(), ann);
			}
		}
		Method writeMethod = getWriteMethod();
		if (writeMethod != null) {
			for (Annotation ann : writeMethod.getAnnotations()) {
				annMap.put(ann.annotationType(), ann);
			}
		}
		Field field = getField();
		if (field != null) {
			for (Annotation ann : field.getAnnotations()) {
				annMap.put(ann.annotationType(), ann);
			}
		}
		return annMap.values().toArray(new Annotation[annMap.size()]);
	}

	private Field getField() {
		String name = getName();
		if (!StringUtils.hasLength(name)) {
			return null;
		}
		Class<?> declaringClass = declaringClass();
		Field field = ReflectionUtils.findField(declaringClass, name);
		if (field == null) {
			// Same lenient fallback checking as in CachedIntrospectionResults...
			field = ReflectionUtils.findField(declaringClass,
					name.substring(0, 1).toLowerCase() + name.substring(1));
			if (field == null) {
				field = ReflectionUtils.findField(declaringClass,
						name.substring(0, 1).toUpperCase() + name.substring(1));
			}
		}
		return field;
	}

	private Class<?> declaringClass() {
		if (getReadMethod() != null) {
			return getReadMethod().getDeclaringClass();
		}
		else {
			return getWriteMethod().getDeclaringClass();
		}
	}

}