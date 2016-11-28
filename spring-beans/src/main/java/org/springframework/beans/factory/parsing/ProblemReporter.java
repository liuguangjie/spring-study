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

/**
 * SPI interface allowing tools and other external processes to handle errors
 * and warnings reported during bean definition parsing.
 * ***************************************************************************
 * ~$ SPI接口允许工具和其他外部流程处理错误和警告报告在bean定义解析.
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see Problem
 */
public interface ProblemReporter {

	/**
	 * Called when a fatal error is encountered during the parsing process.
	 * <p>Implementations must treat the given problem as fatal,
	 * i.e. they have to eventually raise an exception.
	 * ********************************************************************
	 * ~$ 时调用解析过程中遇到一个致命错误.
	 * <p> 实现必须把特定的问题是致命的,也就是说他们必须最终引发一个异常.
	 * @param problem the source of the error (never <code>null</code>)
	 */
	void fatal(Problem problem);

	/**
	 * Called when an error is encountered during the parsing process.
	 * <p>Implementations may choose to treat errors as fatal.
	 * ***************************************************************
	 * ~$ 在解析过程中遇到错误时调用.
	 * <p>实现可以选择对待错误是致命的.
	 * @param problem the source of the error (never <code>null</code>)
	 */
	void error(Problem problem);

	/**
	 * Called when a warning is raised during the parsing process.
	 * <p>Warnings are <strong>never</strong> considered to be fatal.
	 * **************************************************************
	 * ~$ 当在解析过程中提出的一个警告.
	 * <p>警告从未被认为是致命的.
	 * @param problem the source of the warning (never <code>null</code>)
	 */
	void warning(Problem problem);

}
