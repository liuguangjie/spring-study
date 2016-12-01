/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.beans.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.Assert;

/**
 * PagedListHolder is a simple state holder for handling lists of objects,
 * separating them into pages. Page numbering starts with 0.
 *
 * <p>This is mainly targetted at usage in web UIs. Typically, an instance will be
 * instantiated with a list of beans, put into the session, and exported as model.
 * The properties can all be set/get programmatically, but the most common way will
 * be data binding, i.e. populating the bean from request parameters. The getters
 * will mainly be used by the view.
 *
 * <p>Supports sorting the underlying list via a {@link SortDefinition} implementation,
 * available as property "sort". By default, a {@link MutableSortDefinition} instance
 * will be used, toggling the ascending value on setting the same property again.
 *
 * <p>The data binding names have to be called "pageSize" and "sort.ascending",
 * as expected by BeanWrapper. Note that the names and the nesting syntax match
 * the respective JSTL EL expressions, like "myModelAttr.pageSize" and
 * "myModelAttr.sort.ascending".
 * ************************************************************************************
 * ~$ PagedListHolder是一个简单的状态容器处理列表的对象,分离成页面。页面编号从0开始.
 *
 * <p>这主要是针对使用在web ui。通常,一个实例将被实例化bean列表,放入会话,并导出为模型.
 *    的属性都可以通过编程设置/获取,但最常见的方式将数据绑定,即从请求参数填充bean.使用的getter将主要观点.
 *
 * <p>支持排序的列表通过{@link SortDefinition }实现,可作为属性“类型”.
 *    默认情况下,{ @link MutableSortDefinition }实例将被使用,切换设置相同的属性提升价值.
 *
 * <p>数据绑定名称必须被称为“页大小”和"sort.ascending",
 *    正如BeanWrapper所预期的. 注意,名称和嵌套语法匹配相应的JSTL EL表达式,像“myModelAttr.pageSize”和“myModelAttr.sort.ascending”.
 * @author Juergen Hoeller
 * @since 19.05.2003
 * @see #getPageList()
 * @see MutableSortDefinition
 */
public class PagedListHolder<E> implements Serializable {

	public static final int DEFAULT_PAGE_SIZE = 10;

	public static final int DEFAULT_MAX_LINKED_PAGES = 10;


	private List<E> source;

	private Date refreshDate;

	private SortDefinition sort;

	private SortDefinition sortUsed;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private int page = 0;

	private boolean newPageSet;

	private int maxLinkedPages = DEFAULT_MAX_LINKED_PAGES;


	/**
	 * Create a new holder instance.
	 * You'll need to set a source list to be able to use the holder.
	 * **************************************************************
	 * ~$ 创建一个新的实例.你需要设置一个源列表能够使用持有人.
	 * @see #setSource
	 */
	public PagedListHolder() {
		this(new ArrayList<E>(0));
	}

	/**
	 * Create a new holder instance with the given source list, starting with
	 * a default sort definition (with "toggleAscendingOnProperty" activated).
	 * ***********************************************************************
	 * ~$ 创建一个新的持有人与给定的源列表实例,从一个默认类型定义("toggleAscendingOnProperty"激活).
	 * @param source the source List
	 * @see MutableSortDefinition#setToggleAscendingOnProperty
	 */
	public PagedListHolder(List<E> source) {
		this(source, new MutableSortDefinition(true));
	}

	/**
	 * Create a new holder instance with the given source list.
	 * ********************************************************
	 * ~$ 创建一个新的持有人实例与给定的源列表.
	 * @param source the source List
	 * @param sort the SortDefinition to start with
	 */
	public PagedListHolder(List<E> source, SortDefinition sort) {
		setSource(source);
		setSort(sort);
	}


	/**
	 * Set the source list for this holder.
	 * ************************************
	 * ~$ 这个支架的源列表.
	 */
	public void setSource(List<E> source) {
		Assert.notNull(source, "Source List must not be null");
		this.source = source;
		this.refreshDate = new Date();
		this.sortUsed = null;
	}

	/**
	 * Return the source list for this holder.
	 * ***************************************
	 * ~$ 返回这个持有人的源列表.
	 */
	public List<E> getSource() {
		return this.source;
	}

	/**
	 * Return the last time the list has been fetched from the source provider.
	 * ************************************************************************
	 * ~$ 返回最后一次从源到提供者列表。
	 */
	public Date getRefreshDate() {
		return this.refreshDate;
	}

	/**
	 * Set the sort definition for this holder.
	 * Typically an instance of MutableSortDefinition.
	 * ***********************************************
	 * ~$ 集定义的持有人.通常MutableSortDefinition的实例.
	 * @see MutableSortDefinition
	 */
	public void setSort(SortDefinition sort) {
		this.sort = sort;
	}

	/**
	 * Return the sort definition for this holder.
	 * *******************************************
	 * ~$ 返回的类定义这个持有人.
	 */
	public SortDefinition getSort() {
		return this.sort;
	}

	/**
	 * Set the current page size.
	 * Resets the current page number if changed.
	 * <p>Default value is 10.
	 * *******************************************
	 * ~$ 设置当前页面大小.如果改变了重置当前页码.
	 */
	public void setPageSize(int pageSize) {
		if (pageSize != this.pageSize) {
			this.pageSize = pageSize;
			if (!this.newPageSet) {
				this.page = 0;
			}
		}
	}

	/**
	 * Return the current page size.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * Set the current page number.
	 * Page numbering starts with 0.
	 */
	public void setPage(int page) {
		this.page = page;
		this.newPageSet = true;
	}

	/**
	 * Return the current page number.
	 * Page numbering starts with 0.
	 */
	public int getPage() {
		this.newPageSet = false;
		if (this.page >= getPageCount()) {
			this.page = getPageCount() - 1;
		}
		return this.page;
	}

	/**
	 * Set the maximum number of page links to a few pages around the current one.
	 * ***************************************************************************
	 * ~$ 设置页面的链接的最大数量在当前几页.
	 */
	public void setMaxLinkedPages(int maxLinkedPages) {
		this.maxLinkedPages = maxLinkedPages;
	}

	/**
	 * Return the maximum number of page links to a few pages around the current one.
	 * ******************************************************************************
	 * ~$ 返回的最大数量页面链接在当前几页.
	 */
	public int getMaxLinkedPages() {
		return this.maxLinkedPages;
	}


	/**
	 * Return the number of pages for the current source list.
	 * *******************************************************
	 * ~$ 返回当前源列表的页面数.
	 */
	public int getPageCount() {
		float nrOfPages = (float) getNrOfElements() / getPageSize();
		return (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
	}

	/**
	 * Return if the current page is the first one.
	 * ********************************************
	 * ~$ 如果返回当前页面是第一个.
	 */
	public boolean isFirstPage() {
		return getPage() == 0;
	}

	/**
	 * Return if the current page is the last one.
	 * *******************************************
	 * ~$ 返回当前页面是否最后一个.
	 */
	public boolean isLastPage() {
		return getPage() == getPageCount() -1;
	}

	/**
	 * Switch to previous page.
	 * Will stay on first page if already on first page.
	 * *************************************************
	 * ~$ 切换到前一页.将继续如果已经在第一页第一页.
	 */
	public void previousPage() {
		if (!isFirstPage()) {
			this.page--;
		}
	}

	/**
	 * Switch to next page.
	 * Will stay on last page if already on last page.
	 * ***********************************************
	 * ~$ 切换到下一个页面.将继续如果已经在最后一页最后一页.
	 */
	public void nextPage() {
		if (!isLastPage()) {
			this.page++;
		}
	}

	/**
	 * Return the total number of elements in the source list.
	 * *******************************************************
	 * ~$ 返回源列表中元素的总数.
	 */
	public int getNrOfElements() {
		return getSource().size();
	}

	/**
	 * Return the element index of the first element on the current page.
	 * Element numbering starts with 0.
	 * ******************************************************************
	 * ~$ 返回第一个元素的索引元素在当前页面.元素编号从0开始.
	 */
	public int getFirstElementOnPage() {
		return (getPageSize() * getPage());
	}

	/**
	 * Return the element index of the last element on the current page.
	 * Element numbering starts with 0.
	 * *****************************************************************
	 * ~$ 返回最后一个元素的元素索引在当前页面.元素编号从0开始.
	 */
	public int getLastElementOnPage() {
		int endIndex = getPageSize() * (getPage() + 1);
		int size = getNrOfElements();
		return (endIndex > size ? size : endIndex) - 1;
	}

	/**
	 * Return a sub-list representing the current page.
	 * ************************************************
	 * ~$ 返回一个列表的代表当前页面.
	 */
	public List<E> getPageList() {
		return getSource().subList(getFirstElementOnPage(), getLastElementOnPage() + 1);
	}

	/**
	 * Return the first page to which create a link around the current page.
	 * *********************************************************************
	 * ~$ 返回第一页,在当前页面创建一个链接.
	 */
	public int getFirstLinkedPage() {
		return Math.max(0, getPage() - (getMaxLinkedPages() / 2));
	}

	/**
	 * Return the last page to which create a link around the current page.
	 * ********************************************************************
	 * ~$ 返回最后一页,在当前页面创建一个链接.
	 */
	public int getLastLinkedPage() {
		return Math.min(getFirstLinkedPage() + getMaxLinkedPages() - 1, getPageCount() - 1);
	}


	/**
	 * Resort the list if necessary, i.e. if the current <code>sort</code> instance
	 * isn't equal to the backed-up <code>sortUsed</code> instance.
	 * <p>Calls <code>doSort</code> to trigger actual sorting.
	 * ****************************************************************************
	 * ~$ 如有必要 ,即如果当前实例并不等于备份sortUsed实例.
	 * @see #doSort
	 */
	public void resort() {
		SortDefinition sort = getSort();
		if (sort != null && !sort.equals(this.sortUsed)) {
			this.sortUsed = copySortDefinition(sort);
			doSort(getSource(), sort);
			setPage(0);
		}
	}

	/**
	 * Create a deep copy of the given sort definition,
	 * for use as state holder to compare a modified sort definition against.
	 * <p>Default implementation creates a MutableSortDefinition instance.
	 * Can be overridden in subclasses, in particular in case of custom
	 * extensions to the SortDefinition interface. Is allowed to return
	 * null, which means that no sort state will be held, triggering
	 * actual sorting for each <code>resort</code> call.
	 * **********************************************************************
	 * ~$ 创建一个深拷贝给定类的定义,用作状态容器比较反对一种修改定义.
	 * <p> 默认实现创建一个MutableSortDefinition实例.
	 *     可以在子类中覆盖,特别是SortDefinition界面的自定义扩展.
	 *     被允许返回null,这意味着没有国家将举行,引发实际排序为每个度假村的电话.
	 * @param sort the current SortDefinition object
	 * @return a deep copy of the SortDefinition object
	 * @see MutableSortDefinition#MutableSortDefinition(SortDefinition)
	 */
	protected SortDefinition copySortDefinition(SortDefinition sort) {
		return new MutableSortDefinition(sort);
	}

	/**
	 * Actually perform sorting of the given source list, according to
	 * the given sort definition.
	 * <p>The default implementation uses Spring's PropertyComparator.
	 * Can be overridden in subclasses.
	 * ***************************************************************
	 * ~$ 实际执行给定源列表的排序,根据给定的类定义.
	 * <p>默认实现使用Spring的PropertyComparator.可以在子类中覆盖.
	 * @see PropertyComparator#sort(List, SortDefinition)
	 */
	protected void doSort(List<E> source, SortDefinition sort) {
		PropertyComparator.sort(source, sort);
	}

}
