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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple {@link ProblemReporter} implementation that exhibits fail-fast
 * behavior when errors are encountered.
 * 
 * <p>The first error encountered results in a {@link BeanDefinitionParsingException}
 * being thrown.
 *
 * <p>Warnings are written to
 * {@link #setLogger(Log) the log} for this class.
 * **********************************************************************************
 * ~$ 简单的{@link ProblemReporter }实现展品快速失败当遇到错误的行为.
 * <p> 结果在遇到第一个错误{@link BeanDefinitionParsingException }抛出.
 *
 * <p>警告被写入{@link #setLogger(Log) the log}这个类.
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 */
public class FailFastProblemReporter implements ProblemReporter {

	private Log logger = LogFactory.getLog(getClass());


	/**
	 * Set the {@link Log logger} that is to be used to report warnings.
	 * <p>If set to <code>null</code> then a default {@link Log logger} set to
	 * the name of the instance class will be used.
	 * ************************************************************************
	 * ~$ 设置{@link Log logger} 也被用于报告警告.
	 * <p>如果设置为null,那么默认 {@link Log logger} 设置为将使用实例类的名称.
	 * @param logger the {@link Log logger} that is to be used to report warnings
	 */
	public void setLogger(Log logger) {
		this.logger = (logger != null ? logger : LogFactory.getLog(getClass()));
	}


	/**
	 * Throws a {@link BeanDefinitionParsingException} detailing the error
	 * that has occurred.
	 * *******************************************************************
	 * ~$ 抛出一个{@link BeanDefinitionParsingException }详细的错误发生.
	 * @param problem the source of the error
	 */
	public void fatal(Problem problem) {
		throw new BeanDefinitionParsingException(problem);
	}

	/**
	 * Throws a {@link BeanDefinitionParsingException} detailing the error
	 * that has occurred.
	 * *******************************************************************
	 * ~$ 抛出一个{ @link BeanDefinitionParsingException }详细的错误发生.
	 * @param problem the source of the error
	 */
	public void error(Problem problem) {
		throw new BeanDefinitionParsingException(problem);
	}

	/**
	 * Writes the supplied {@link Problem} to the {@link Log} at <code>WARN</code> level.
	 * **********************************************************************************
	 * ~$ 写提供{@link Problem} {@link Log}在警告级别.
	 * @param problem the source of the warning
	 */
	public void warning(Problem problem) {
		this.logger.warn(problem, problem.getRootCause());
	}

}
