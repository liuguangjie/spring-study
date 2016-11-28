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

package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;

/**
 * Marker interface for a defaults definition,
 * extending BeanMetadataElement to inherit source exposure.
 *
 * <p>Concrete implementations are typically based on 'document defaults',
 * for example specified at the root tag level within an XML document.
 *
 * ***********************************************************************
 * ~$ 默认值定义的标记接口,扩展BeanMetadataElement继承源接触.
 *
 * <p> 具体实现通常是基于文档的默认值,例如在根标记级别进行指定在一个XML文档.
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see org.springframework.beans.factory.xml.DocumentDefaultsDefinition
 * @see ReaderEventListener#defaultsRegistered(DefaultsDefinition)
 */
public interface DefaultsDefinition extends BeanMetadataElement {

}
