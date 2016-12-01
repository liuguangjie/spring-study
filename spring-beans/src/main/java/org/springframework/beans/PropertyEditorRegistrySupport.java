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

package org.springframework.beans;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.xml.sax.InputSource;

import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PatternEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.util.ClassUtils;

/**
 * Base implementation of the {@link PropertyEditorRegistry} interface.
 * Provides management of default editors and custom editors.
 * Mainly serves as base class for {@link BeanWrapperImpl}.
 * ********************************************************************
 * ~$ 基本实现的{@link PropertyEditorRegistry }接口.
 *    提供管理默认编辑器和自定义编辑器.主要作为基类{@link BeanWrapperImpl }.
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.2.6
 * @see java.beans.PropertyEditorManager
 * @see java.beans.PropertyEditorSupport#setAsText
 * @see java.beans.PropertyEditorSupport#setValue
 */
public class PropertyEditorRegistrySupport implements PropertyEditorRegistry {

	private ConversionService conversionService;

	private boolean defaultEditorsActive = false;

	private boolean configValueEditorsActive = false;

	private Map<Class<?>, PropertyEditor> defaultEditors;

	private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;

	private Map<Class<?>, PropertyEditor> customEditors;

	private Map<String, CustomEditorHolder> customEditorsForPath;

	private Set<PropertyEditor> sharedEditors;

	private Map<Class<?>, PropertyEditor> customEditorCache;


	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * ****************************************************************
	 * ~$ 指定一个Spring 3.0 ConversionService用于转换属性值,
	 *     作为一个javabean PropertyEditors的替代品.
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Return the associated ConversionService, if any.
	 * ************************************************
	 * ~$ 返回相关ConversionService,如果有.
	 */
	public ConversionService getConversionService() {
		return this.conversionService;
	}


	//---------------------------------------------------------------------
	// Management of default editors
	//---------------------------------------------------------------------
	/** 管理默认编辑器*/
	/**
	 * Activate the default editors for this registry instance,
	 * allowing for lazily registering default editors when needed.
	 * ************************************************************
	 * ~$ 激活这个注册表实例的默认编辑器,允许延迟注册默认编辑器在需要的时候.
	 */
	protected void registerDefaultEditors() {
		this.defaultEditorsActive = true;
	}

	/**
	 * Activate config value editors which are only intended for configuration purposes,
	 * such as {@link StringArrayPropertyEditor}.
	 * <p>Those editors are not registered by default simply because they are in
	 * general inappropriate for data binding purposes. Of course, you may register
	 * them individually in any case, through {@link #registerCustomEditor}.
	 * *********************************************************************************
	 * ~$ 激活配置值编辑器仅用于配置的目的,如{@link StringArrayPropertyEditor }.
	 * <p>那些编辑不是默认注册仅仅是因为他们一般不用于数据绑定.
	 *    当然,你可能会在任何情况下,单独注册通过{@link # registerCustomEditor }.
	 */
	public void useConfigValueEditors() {
		this.configValueEditorsActive = true;
	}

	/**
	 * Override the default editor for the specified type with the given property editor.
	 * <p>Note that this is different from registering a custom editor in that the editor
	 * semantically still is a default editor. A ConversionService will override such a
	 * default editor, whereas custom editors usually override the ConversionService.
	 * **********************************************************************************
	 * ~$ 覆盖默认编辑器为指定的类型与给定的属性编辑器.
	 * <p>注意,这是不同于注册自定义编辑,编辑语义上仍然是一个默认编辑器.
	 *    ConversionService将覆盖这样一个默认编辑器,而自定义编辑器通常覆盖ConversionService.
	 * @param requiredType the type of the property
	 * @param propertyEditor the editor to register
	 * @see #registerCustomEditor(Class, PropertyEditor)
	 */
	public void overrideDefaultEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		if (this.overriddenDefaultEditors == null) {
			this.overriddenDefaultEditors = new HashMap<Class<?>, PropertyEditor>();
		}
		this.overriddenDefaultEditors.put(requiredType, propertyEditor);
	}

	/**
	 * Retrieve the default editor for the given property type, if any.
	 * <p>Lazily registers the default editors, if they are active.
	 * ****************************************************************
	 * ~$ 检索给定的属性类型的默认编辑器,如果有.
	 * <p>懒洋洋地寄存器的默认编辑器,如果他们是活跃的.
	 * @param requiredType type of the property
	 * @return the default editor, or <code>null</code> if none found
	 * @see #registerDefaultEditors
	 */
	public PropertyEditor getDefaultEditor(Class<?> requiredType) {
		if (!this.defaultEditorsActive) {
			return null;
		}
		if (this.overriddenDefaultEditors != null) {
			PropertyEditor editor = this.overriddenDefaultEditors.get(requiredType);
			if (editor != null) {
				return editor;
			}
		}
		if (this.defaultEditors == null) {
			createDefaultEditors();
		}
		return this.defaultEditors.get(requiredType);
	}

	/**
	 * Actually register the default editors for this registry instance.
	 * *****************************************************************
	 * ~$ 实际上注册这个注册表的默认编辑器实例.
	 */
	private void createDefaultEditors() {
		this.defaultEditors = new HashMap<Class<?>, PropertyEditor>(64);

		// Simple editors, without parameterization capabilities.
		/** 简单的编辑,没有参数化功能.*/
		// The JDK does not contain a default editor for any of these target types.
		/** JDK不包含一个默认的编辑这些目标类型.*/
		this.defaultEditors.put(Charset.class, new CharsetEditor());
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(Class[].class, new ClassArrayEditor());
		this.defaultEditors.put(Currency.class, new CurrencyEditor());
		this.defaultEditors.put(File.class, new FileEditor());
		this.defaultEditors.put(InputStream.class, new InputStreamEditor());
		this.defaultEditors.put(InputSource.class, new InputSourceEditor());
		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Pattern.class, new PatternEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
		this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
		this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
		this.defaultEditors.put(URI.class, new URIEditor());
		this.defaultEditors.put(URL.class, new URLEditor());
		this.defaultEditors.put(UUID.class, new UUIDEditor());

		// Default instances of collection editors.
		/** 集合编辑器的默认实例.*/
		// Can be overridden by registering custom instances of those as custom editors.
		/** 通过注册自定义的实例可以覆盖这些自定义编辑器.*/
		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
		this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
		this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));

		// Default editors for primitive arrays.
		/** 原始数组的默认编辑器.*/
		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());

		// The JDK does not contain a default editor for char!
		/** JDK不包含一个默认的编辑字符! */
		this.defaultEditors.put(char.class, new CharacterEditor(false));
		this.defaultEditors.put(Character.class, new CharacterEditor(true));

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		/** Spring的CustomBooleanEditor接受标志值比JDK的默认编辑器.*/
		this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
		this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

		// The JDK does not contain default editors for number wrapper types!
		/** JDK不包含数量包装器类型的默认编辑器!*/
		// Override JDK primitive number editors with our own CustomNumberEditor.
		/** 用自己的CustomNumberEditor覆盖JDK原始数字编辑.*/
		this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
		this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
		this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
		this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
		this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

		// Only register config value editors if explicitly requested.
		/** 只有寄存器配置值编辑器显式地请求.*/
		if (this.configValueEditorsActive) {
			StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
			this.defaultEditors.put(String[].class, sae);
			this.defaultEditors.put(short[].class, sae);
			this.defaultEditors.put(int[].class, sae);
			this.defaultEditors.put(long[].class, sae);
		}
	}

	/**
	 * Copy the default editors registered in this instance to the given target registry.
	 * **********************************************************************************
	 * ~$ 复制默认编辑器在这个实例中给定的目标注册中心注册.
	 * @param target the target registry to copy to
	 */
	protected void copyDefaultEditorsTo(PropertyEditorRegistrySupport target) {
		target.defaultEditorsActive = this.defaultEditorsActive;
		target.configValueEditorsActive = this.configValueEditorsActive;
		target.defaultEditors = this.defaultEditors;
		target.overriddenDefaultEditors = this.overriddenDefaultEditors;
	}


	//---------------------------------------------------------------------
	// Management of custom editors
	//---------------------------------------------------------------------
	/** 管理自定义编辑*/
	public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
	}

	public void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor) {
		if (requiredType == null && propertyPath == null) {
			throw new IllegalArgumentException("Either requiredType or propertyPath is required");
		}
		if (propertyPath != null) {
			if (this.customEditorsForPath == null) {
				this.customEditorsForPath = new LinkedHashMap<String, CustomEditorHolder>(16);
			}
			this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
		}
		else {
			if (this.customEditors == null) {
				this.customEditors = new LinkedHashMap<Class<?>, PropertyEditor>(16);
			}
			this.customEditors.put(requiredType, propertyEditor);
			this.customEditorCache = null;
		}
	}

	/**
	 * Register the given custom property editor for all properties
	 * of the given type, indicating that the given instance is a
	 * shared editor that might be used concurrently.
	 * ************************************************************
	 * ~$ 注册给定的自定义属性编辑器给定类型的所有属性,表明给定的实例是一个共享的编辑器,可以同时使用.
	 * @param requiredType the type of the property
	 * @param propertyEditor the shared editor to register
	 * @deprecated as of Spring 3.0, in favor of PropertyEditorRegistrars or ConversionService usage
	 */
	@Deprecated
	public void registerSharedEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
		if (this.sharedEditors == null) {
			this.sharedEditors = new HashSet<PropertyEditor>();
		}
		this.sharedEditors.add(propertyEditor);
	}

	/**
	 * Check whether the given editor instance is a shared editor, that is,
	 * whether the given editor instance might be used concurrently.
	 * ********************************************************************
	 * ~$ 检查是否给编辑器实例是一个共享的编辑器,也就是说,给编辑器实例是否可能同时使用.
	 * @param propertyEditor the editor instance to check
	 * @return whether the editor is a shared instance
	 */
	public boolean isSharedEditor(PropertyEditor propertyEditor) {
		return (this.sharedEditors != null && this.sharedEditors.contains(propertyEditor));
	}

	public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
		Class<?> requiredTypeToUse = requiredType;
		if (propertyPath != null) {
			if (this.customEditorsForPath != null) {
				// Check property-specific editor first.
				/** 首先检查调控房地产的编辑.*/
				PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
				if (editor == null) {
					List<String> strippedPaths = new LinkedList<String>();
					addStrippedPropertyPaths(strippedPaths, "", propertyPath);
					for (Iterator<String> it = strippedPaths.iterator(); it.hasNext() && editor == null;) {
						String strippedPath = it.next();
						editor = getCustomEditor(strippedPath, requiredType);
					}
				}
				if (editor != null) {
					return editor;
				}
			}
			if (requiredType == null) {
				requiredTypeToUse = getPropertyType(propertyPath);
			}
		}
		// No property-specific editor -> check type-specific editor.
		/** 没有调控房地产编辑- >查看特定类型编辑器.*/
		return getCustomEditor(requiredTypeToUse);
	}

	/**
	 * Determine whether this registry contains a custom editor
	 * for the specified array/collection element.
	 * ********************************************************
	 * ~$ 确定这个注册表是否包含指定的自定义编辑器/集合元素数组.
	 * @param elementType the target type of the element
	 * (can be <code>null</code> if not known)
	 * @param propertyPath the property path (typically of the array/collection;
	 * can be <code>null</code> if not known)
	 * @return whether a matching custom editor has been found
	 */
	public boolean hasCustomEditorForElement(Class<?> elementType, String propertyPath) {
		if (propertyPath != null && this.customEditorsForPath != null) {
			for (Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
				if (PropertyAccessorUtils.matchesProperty(entry.getKey(), propertyPath)) {
					if (entry.getValue().getPropertyEditor(elementType) != null) {
						return true;
					}
				}
			}
		}
		// No property-specific editor -> check type-specific editor.
		return (elementType != null && this.customEditors != null && this.customEditors.containsKey(elementType));
	}

	/**
	 * Determine the property type for the given property path.
	 * <p>Called by {@link #findCustomEditor} if no required type has been specified,
	 * to be able to find a type-specific editor even if just given a property path.
	 * <p>The default implementation always returns <code>null</code>.
	 * BeanWrapperImpl overrides this with the standard <code>getPropertyType</code>
	 * method as defined by the BeanWrapper interface.
	 * ******************************************************************************
	 * ~$ 确定给定属性的属性类型的道路.
	 * <p>称为{@link #findCustomEditor }如果没有指定所需类型,能够找到一个特定类型编辑器即使给一个属性的路径.
	 * <p>默认实现总是返回null.BeanWrapperImpl覆盖这个标准getPropertyType BeanWrapper接口定义的方法.
	 * @param propertyPath the property path to determine the type for
	 * @return the type of the property, or <code>null</code> if not determinable
	 * @see BeanWrapper#getPropertyType(String)
	 */
	protected Class<?> getPropertyType(String propertyPath) {
		return null;
	}

	/**
	 * Get custom editor that has been registered for the given property.
	 * *****************************************************************
	 * ~$ 自定义编辑器已经注册为给定的属性.
	 * @param propertyName the property path to look for
	 * @param requiredType the type to look for
	 * @return the custom editor, or <code>null</code> if none specific for this property
	 */
	private PropertyEditor getCustomEditor(String propertyName, Class<?> requiredType) {
		CustomEditorHolder holder = this.customEditorsForPath.get(propertyName);
		return (holder != null ? holder.getPropertyEditor(requiredType) : null);
	}

	/**
	 * Get custom editor for the given type. If no direct match found,
	 * try custom editor for superclass (which will in any case be able
	 * to render a value as String via <code>getAsText</code>).
	 * ****************************************************************
	 * ~$ 为给定类型自定义编辑器.如果没有直接匹配发现,
	 *    尝试自定义编辑超类(在任何情况下能呈现一个值作为字符串通过getAsText).
	 * @param requiredType the type to look for
	 * @return the custom editor, or <code>null</code> if none found for this type
	 * @see PropertyEditor#getAsText()
	 */
	private PropertyEditor getCustomEditor(Class<?> requiredType) {
		if (requiredType == null || this.customEditors == null) {
			return null;
		}
		// Check directly registered editor for type.
		/** 检查直接注册编辑类型.*/
		PropertyEditor editor = this.customEditors.get(requiredType);
		if (editor == null) {
			// Check cached editor for type, registered for superclass or interface.
			/** 检查缓存编辑类型,注册超类或接口.*/
			if (this.customEditorCache != null) {
				editor = this.customEditorCache.get(requiredType);
			}
			if (editor == null) {
				// Find editor for superclass or interface.
				/** 寻找超类或接口编辑器.*/
				for (Iterator<Class<?>> it = this.customEditors.keySet().iterator(); it.hasNext() && editor == null;) {
					Class<?> key = it.next();
					if (key.isAssignableFrom(requiredType)) {
						editor = this.customEditors.get(key);
						// Cache editor for search type, to avoid the overhead
						/** 缓存编辑搜索类型,避免开销 */
						// of repeated assignable-from checks.
						/** 重复的assignable-from检查.*/
						if (this.customEditorCache == null) {
							this.customEditorCache = new HashMap<Class<?>, PropertyEditor>();
						}
						this.customEditorCache.put(requiredType, editor);
					}
				}
			}
		}
		return editor;
	}

	/**
	 * Guess the property type of the specified property from the registered
	 * custom editors (provided that they were registered for a specific type).
	 * ************************************************************************
	 * ~$ 猜的属性类型的指定属性注册自定义编辑器(只要他们注册为一个特定的类型).
	 * @param propertyName the name of the property
	 * @return the property type, or <code>null</code> if not determinable
	 */
	protected Class<?> guessPropertyTypeFromEditors(String propertyName) {
		if (this.customEditorsForPath != null) {
			CustomEditorHolder editorHolder = this.customEditorsForPath.get(propertyName);
			if (editorHolder == null) {
				List<String> strippedPaths = new LinkedList<String>();
				addStrippedPropertyPaths(strippedPaths, "", propertyName);
				for (Iterator<String> it = strippedPaths.iterator(); it.hasNext() && editorHolder == null;) {
					String strippedName = it.next();
					editorHolder = this.customEditorsForPath.get(strippedName);
				}
			}
			if (editorHolder != null) {
				return editorHolder.getRegisteredType();
			}
		}
		return null;
	}

	/**
	 * Copy the custom editors registered in this instance to the given target registry.
	 * *********************************************************************************
	 * ~$ 复制自定义编辑在这个实例中给定的目标注册中心注册.
	 * @param target the target registry to copy to
	 * @param nestedProperty the nested property path of the target registry, if any.
	 * If this is non-null, only editors registered for a path below this nested property
	 * will be copied. If this is null, all editors will be copied.
	 */
	protected void copyCustomEditorsTo(PropertyEditorRegistry target, String nestedProperty) {
		String actualPropertyName =
				(nestedProperty != null ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null);
		if (this.customEditors != null) {
			for (Map.Entry<Class<?>, PropertyEditor> entry : this.customEditors.entrySet()) {
				target.registerCustomEditor(entry.getKey(), entry.getValue());
			}
		}
		if (this.customEditorsForPath != null) {
			for (Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
				String editorPath = entry.getKey();
				CustomEditorHolder editorHolder = entry.getValue();
				if (nestedProperty != null) {
					int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
					if (pos != -1) {
						String editorNestedProperty = editorPath.substring(0, pos);
						String editorNestedPath = editorPath.substring(pos + 1);
						if (editorNestedProperty.equals(nestedProperty) || editorNestedProperty.equals(actualPropertyName)) {
							target.registerCustomEditor(
									editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
						}
					}
				}
				else {
					target.registerCustomEditor(
							editorHolder.getRegisteredType(), editorPath, editorHolder.getPropertyEditor());
				}
			}
		}
	}


	/**
	 * Add property paths with all variations of stripped keys and/or indexes.
	 * Invokes itself recursively with nested paths.
	 * ************************************************************************
	 * ~$ 添加属性路径的所有变化将钥匙和/或索引.递归地调用自身与嵌套的路径.
	 * @param strippedPaths the result list to add to
	 * @param nestedPath the current nested path
	 * @param propertyPath the property path to check for keys/indexes to strip
	 */
	private void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {
		int startIndex = propertyPath.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR);
		if (startIndex != -1) {
			int endIndex = propertyPath.indexOf(PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR);
			if (endIndex != -1) {
				String prefix = propertyPath.substring(0, startIndex);
				String key = propertyPath.substring(startIndex, endIndex + 1);
				String suffix = propertyPath.substring(endIndex + 1, propertyPath.length());
				// Strip the first key.
				/** 第一个键.*/
				strippedPaths.add(nestedPath + prefix + suffix);
				// Search for further keys to strip, with the first key stripped.
				/** 寻找进一步的关键地带,与第一个关键的剥夺了.*/
				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
				// Search for further keys to strip, with the first key not stripped.
				/** 寻找进一步的关键地带,与第一个键不剥夺了.*/
				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
			}
		}
	}


	/**
	 * Holder for a registered custom editor with property name.
	 * Keeps the PropertyEditor itself plus the type it was registered for.
	 * ********************************************************************
	 * ~$ 注册自定义编辑器的属性名.保持PropertyEditor本身+类型注册.
	 */
	private static class CustomEditorHolder {

		private final PropertyEditor propertyEditor;

		private final Class<?> registeredType;

		private CustomEditorHolder(PropertyEditor propertyEditor, Class<?> registeredType) {
			this.propertyEditor = propertyEditor;
			this.registeredType = registeredType;
		}

		private PropertyEditor getPropertyEditor() {
			return this.propertyEditor;
		}

		private Class<?> getRegisteredType() {
			return this.registeredType;
		}

		private PropertyEditor getPropertyEditor(Class<?> requiredType) {
			// Special case: If no required type specified, which usually only happens for
			/** 特殊情况:如果没有指定所需类型,通常只发生*/
			// Collection elements, or required type is not assignable to registered type,
			/** 集合的元素,或所需类型不是可转让的注册类型,*/
			// which usually only happens for generic properties of type Object -
			/** 通常只发生在通用属性类型的对象-*/
			// then return PropertyEditor if not registered for Collection or array type.
			/** 然后返回PropertyEditor如果不是注册集合或数组类型.*/
			// (If not registered for Collection or array, it is assumed to be intended
			// for elements.)
			/** (如果不是注册集合或数组,它被认为是用于元素).*/
			if (this.registeredType == null ||
					(requiredType != null &&
					(ClassUtils.isAssignable(this.registeredType, requiredType) ||
					ClassUtils.isAssignable(requiredType, this.registeredType))) ||
					(requiredType == null &&
					(!Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()))) {
				return this.propertyEditor;
			}
			else {
				return null;
			}
		}
	}

}
