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

package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Generic converter interface for converting between two or more types.
 *
 * <p>This is the most flexible of the Converter SPI interfaces, but also the most complex.
 * It is flexible in that a GenericConverter may support converting between multiple source/target
 * type pairs (see {@link #getConvertibleTypes()}. In addition, GenericConverter implementations
 * have access to source/target {@link TypeDescriptor field context} during the type conversion process.
 * This allows for resolving source and target field metadata such as annotations and generics
 * information, which can be used influence the conversion logic.
 *
 * <p>This interface should generally not be used when the simpler {@link Converter} or
 * {@link ConverterFactory} interfaces are sufficient.
 * *****************************************************************************************************
 * ~$ 通用转换器接口两个或两个以上的类型之间的转换.
 *
 * <p> 这是最灵活的转换器SPI接口,也最复杂.它非常灵活,GenericConverter可以支持多个源/目标类型对之间的转换(见{@link #getConvertibleTypes()}.
 *    此外,GenericConverter实现访问源/目标{@link TypeDescriptor field context}在类型转换的过程.
 *    这使得解决源和目标字段元数据注释和泛型信息等,可影响转换逻辑.
 *
 * <p>这个接口时通常不应使用简单{@link Converter}或{@link ConverterFactory}接口是充分的.
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see TypeDescriptor
 * @see Converter
 * @see ConverterFactory
 */
public interface GenericConverter {

	/**
	 * Return the source and target types which this converter can convert between.
	 * <p>Each entry is a convertible source-to-target type pair.
	 * ****************************************************************************
	 * ~$ 返回这个转换器可以转换的源和目标类型之间.
	 * <p>每个条目是一对可转换源到目标的类型.
	 */
	Set<ConvertiblePair> getConvertibleTypes();

	/**
	 * Convert the source to the targetType described by the TypeDescriptor.
	 * *********************************************************************
	 * ~$ 转换的源targetType TypeDescriptor所描述的.
	 * @param source the source object to convert (may be null)
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return the converted object
	 */
	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);


	/**
	 * Holder for a source-to-target class pair.
	 * *****************************************
	 * ~$ 一对源到目标类的持有人.
	 */
	public static final class ConvertiblePair {

		private final Class<?> sourceType;

		private final Class<?> targetType;

		/**
		 * Create a new source-to-target pair.
		 * ***********************************
		 * ~$ 创建一个新的源到目标的一对.
		 * @param sourceType the source type
		 * @param targetType the target type
		 */
		public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
			Assert.notNull(sourceType, "Source type must not be null");
			Assert.notNull(targetType, "Target type must not be null");
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		public Class<?> getSourceType() {
			return this.sourceType;
		}

		public Class<?> getTargetType() {
			return this.targetType;
		}

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
				return true;
			}
            if (obj == null || obj.getClass() != ConvertiblePair.class) {
				return false;
			}
            ConvertiblePair other = (ConvertiblePair) obj;
            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);

        }

        @Override
        public int hashCode() {
            return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
        }
    }

}
