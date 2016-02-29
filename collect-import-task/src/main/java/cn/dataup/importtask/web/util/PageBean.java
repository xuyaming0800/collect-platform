package cn.dataup.importtask.web.util;

/**
 * 分页所用模型
 * 使用示例：
 *  pageBean.setMaxResults(30);  //这句可以省略，也可以在spring中配置该属性
 *  int count=service.findCount(...);
 *  pageBean.setRowCount(count);
 *  List list=service.queryFoo(hql,pageBean.getFirstResult(),pageBean.getMaxResults());
 * 
 */
public class PageBean {
  
	private int pageNo = 1;
	private int totalPage = 1;
	private int maxResults = 5;
	private Integer rowCount = 0;

	public Integer getRowCount() {
		return rowCount;
		
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	// 设置、修正分页相关信息
	private void configPage() {
		if (rowCount == null || rowCount == 0) {
			return;
		}
		int p = rowCount / maxResults;
		if (rowCount % maxResults == 0) {
			totalPage = p;
		} else {
			totalPage = p + 1;
		}

//		if (pageNo > totalPage) {
//			pageNo = totalPage;
//		}
		if (pageNo < 1) {
			pageNo = 1;
		}
	}

	public int getFirstResult() {
		configPage();
		return maxResults * (pageNo - 1);
	}
}
