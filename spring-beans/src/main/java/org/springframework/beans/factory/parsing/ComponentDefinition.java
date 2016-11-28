/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * Interface that describes the logical view of a set of {@link BeanDefinition BeanDefinitions}
 * and {@link BeanReference BeanReferences} as presented in some configuration context.
 *
 * <p>With the introduction of {@link org.springframework.beans.factory.xml.NamespaceHandler pluggable custom XML tags},
 * it is now possible for a single logical configuration entity, in this case an XML tag, to
 * create multiple {@link BeanDefinition BeanDefinitions} and {@link BeanReference RuntimeBeanReferences}
 * in order to provide more succinct configuration and greater convenience to end users. As such, it can
 * no longer be assumed that each configuration entity (e.g. XML tag) maps to one {@link BeanDefinition}.
 * For tool vendors and other users who wish to present visualization or support for configuring Spring
 * applications it is important that there is some mechanism in place to tie the {@link BeanDefinition BeanDefinitions}
 * in the {@link org.springframework.beans.factory.BeanFactory} back to the configuration data in a way
 * that has concrete meaning to the end user. As such, {@link org.springframework.beans.factory.xml.NamespaceHandler}
 * implementations are able to publish events in the form of a <code>ComponentDefinition</code> for each
 * logical entity being configured. Third parties can then {@link ReaderEventListener subscribe to these events},
 * allowing for a user-centric view of the bean metadata.
 *
 * <p>Each <code>ComponentDefinition</code> has a {@link #getSource source object} which is configuration-specific.
 * In the case of XML-based configuration this is typically the {@link org.w3c.dom.Node} which contains the user
 * supplied configuration information. In addition to this, each {@link BeanDefinition} enclosed in a
 * <code>ComponentDefinition</code> has its own {@link BeanDefinition#getSource() source object} which may point
 * to a different, more specific, set of configuration data. Beyond this, individual pieces of bean metadata such
 * as the {@link org.springframework.beans.PropertyValue PropertyValues} may also have a source object giving an
 * even greater level of detail. Source object extraction is handled through the
 * {@link SourceExtractor} which can be customized as required.
 *
 * <p>Whilst direct access to important {@link BeanReference BeanReferences} is provided through
 * {@link #getBeanReferences}, tools may wish to inspect all {@link BeanDefinition BeanDefinitions} to gather
 * the full set of {@link BeanReference BeanReferences}. Implementations are required to provide
 * all {@link BeanReference BeanReferences} that are required to validate the configuration of the
 * overall logical entity as well as those required to provide full user visualisation of the configuration.
 * It is expected that certain {@link BeanReference BeanReferences} will not be important to
 * validation or to the user view of the configuration and as such these may be ommitted. A tool may wish to
 * display any additional {@link BeanReference BeanReferences} sourced through the supplied
 * {@link BeanDefinition BeanDefinitions} but this is not considered to be a typical case.
 *
 * <p>Tools can determine the important of contained {@link BeanDefinition BeanDefinitions} by checking the
 * {@link BeanDefinition#getRole role identifier}. The role is essentially a hint to the tool as to how
 * important the configuration provider believes a {@link BeanDefinition} is to the end user. It is expected
 * that tools will <strong>not</strong> display all {@link BeanDefinition BeanDefinitions} for a given
 * <code>ComponentDefinition</code> choosing instead to filter based on the role. Tools may choose to make
 * this filtering user configurable. Particular notice should be given to the
 * {@link BeanDefinition#ROLE_INFRASTRUCTURE INFRASTRUCTURE role identifier}. {@link BeanDefinition BeanDefinitions}
 * classified with this role are completely unimportant to the end user and are required only for
 * internal implementation reasons.
 * *******************************************************************************************************************
 * ~$ 接口,描述了一组的逻辑视图{@link BeanDefinition BeanDefinition }和{@link BeanReference BeanReferences }为在一些配置上下文.
 *
 * <p>通过引入{@link org.springframework.beans.factory.xml.NamespaceHandler pluggable custom XML tags},现在可以为一个逻辑配置实体,在这种情况下,
 * XML标签,创建多个{@link BeanDefinition BeanDefinition }和{@link BeanReference RuntimeBeanReferences }为了提供更简洁的配置和更大的便利给终端用户.
 * 因此,它可以不再假定每个配置实体(例如XML标记)映射到一个{@link BeanDefinition }.
 * 工具厂商和其他用户希望呈现可视化或支持配置Spring应用程序是很重要的,有一些机制结合{@link BeanDefinition BeanDefinition }
 * 在{@link org.springframework.beans.factory.BeanFactory }回到配置数据的方式具体意义给最终用户.
 * 因此,{@link org.springframework.beans.factory.xml.NamespaceHandler }实现能够发布事件的形式ComponentDefinition被配置为每个逻辑实体.
 * 第三方可以{@link ReaderEventListener subscribe to these events},,允许以用户为中心的视图bean的元数据.
 *
 * <p>每个ComponentDefinition有{@link #getSource source object} configuration-specific.对于基于xml的配置通常{@link org.w3c.dom.Node}包含用户提供配置信息.
 * 除此之外,每个{@link BeanDefinition }封闭ComponentDefinition有自己的{@link BeanDefinition#getSource() source object}可能指向一个不同的、更具体的配置数据集.
 * 除此之外,beans 数据的各个部分,如{@link org.springframework.beans.PropertyValue PropertyValue }也可能有一个源对象提供一个更大级别的细节.
 *  源对象提取处理通过{@link SourceExtractor },可以根据需要定制.
 *
 * <p>而直接访问重要{@link BeanReference BeanReferences } {@link #getBeanReferences }提供工具可能希望检查所有{@link BeanDefinition BeanDefinition }
 * 收集的全套{@link BeanReference BeanReferences }.实现必须提供所有{@link BeanReference BeanReferences }验证所需的配置总体逻辑实体以及那些需要提供完整的用户可视化的配置.
 * 预计某些{@link BeanReference BeanReferences }将不重要的验证或用户视图的配置,因此这些可能查找.一个工具可以显示任何额外的{@link BeanReference BeanReferences }采购通过提供
 * {@link BeanDefinition BeanDefinition }但这并不被认为是一个典型的案例.
 *
 * <p>工具可以确定重要的包含{@link BeanDefinition BeanDefinition }通过检查标识符{@link BeanDefinition#getRole role identifier}.
 *  本质上是一个提示作用的工具是如何重要的配置提供者相信{@link BeanDefinition }给最终用户.
 *  预计工具将不会显示所有{@link BeanDefinition BeanDefinition }为给定ComponentDefinition选择滤波器基于角色.
 *  用户可配置的工具可以选择让这个过滤.特别注意应标识符{@link BeanDefinition#ROLE_INFRASTRUCTURE INFRASTRUCTURE role identifier}.
 *   {@link BeanDefinition BeanDefinition }与此角色分类对最终用户是完全不重要,只需要原因内部实现.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see AbstractComponentDefinition
 * @see CompositeComponentDefinition
 * @see BeanComponentDefinition
 * @see ReaderEventListener#componentRegistered(ComponentDefinition)
 */
public interface ComponentDefinition extends BeanMetadataElement {

	/**
	 * Get the user-visible name of this <code>ComponentDefinition</code>.
	 * <p>This should link back directly to the corresponding configuration data
	 * for this component in a given context.
	 * *************************************************************************
	 * ~$ 得到这个ComponentDefinition的用户可见的名字.
	 * <p>这应该直接链接到相应的配置数据为这个组件在一个给定的上下文.
	 */
	String getName();

	/**
	 * Return a friendly description of the described component.
	 * <p>Implementations are encouraged to return the same value from
	 * <code>toString()</code>.
	 * *************************************************************************
	 * ~$ 回一个友好的描述组件的描述.
	 * <p> 鼓励实现从toString()返回相同的值.
	 */
	String getDescription();

	/**
	 * Return the {@link BeanDefinition BeanDefinitions} that were registered
	 * to form this <code>ComponentDefinition</code>.
	 * <p>It should be noted that a <code>ComponentDefinition</code> may well be related with
	 * other {@link BeanDefinition BeanDefinitions} via {@link BeanReference references},
	 * however these are <strong>not</strong> included as they may be not available immediately.
	 * Important {@link BeanReference BeanReferences} are available from {@link #getBeanReferences()}.
	 * **********************************************************************************************
	 * ~$ 返回{@link BeanDefinition BeanDefinition }这个ComponentDefinition注册形式.
	 * <p>应该注意的是,一个ComponentDefinition很可能是与其他{@link BeanDefinition BeanDefinition }
	 *    通过{@link BeanReference references},然而这些不包括可能不是立即可用.
	 *    重要的{@link BeanReference BeanReferences }可从{@link #getBeanReferences()}.
	 * @return the array of BeanDefinitions, or an empty array if none
	 */
	BeanDefinition[] getBeanDefinitions();

	/**
	 * Return the {@link BeanDefinition BeanDefinitions} that represent all relevant
	 * inner beans within this component.
	 * <p>Other inner beans may exist within the associated {@link BeanDefinition BeanDefinitions},
	 * however these are not considered to be needed for validation or for user visualization.
	 * ********************************************************************************************
	 * ~$ 返回{@link BeanDefinition BeanDefinition }代表所有相关内部bean在这个组件.
	 * <p>其他内部bean内可能存在关联的{@link BeanDefinition BeanDefinition },然而这些都不是被认为是需要验证或用户可视化.
	 * @return the array of BeanDefinitions, or an empty array if none
	 */
	BeanDefinition[] getInnerBeanDefinitions();

	/**
	 * Return the set of {@link BeanReference BeanReferences} that are considered
	 * to be important to this <code>ComponentDefinition</code>.
	 * <p>Other {@link BeanReference BeanReferences} may exist within the associated
	 * {@link BeanDefinition BeanDefinitions}, however these are not considered
	 * to be needed for validation or for user visualization.
	 * *****************************************************************************
	 * ~$ 返回的集合{@link BeanReference BeanReferences }这个ComponentDefinition被认为是重要的.
	 * <p>其他{@link BeanReference BeanReferences }内可能存在关联的{@link BeanDefinition BeanDefinition },然而这些都不是被认为是需要验证或用户可视化.
	 *
	 * @return the array of BeanReferences, or an empty array if none
	 */
	BeanReference[] getBeanReferences();

}
