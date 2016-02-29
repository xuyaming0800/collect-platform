package com.gd.app.collect_core;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autonavi.collect.entity.ActiveTaskAroundSearchEntity;
import com.autonavi.collect.entity.ActiveTaskAroundSearchResultEntity;
import com.autonavi.collect.entity.SearchPassiveTaskFacetResult;
import com.autonavi.collect.entity.SearchPassiveTaskResultEntity;
import com.autonavi.collect.entity.SearchTaskQueryEntity;
import com.autonavi.collect.service.TaskSearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SearchTest {

	@Autowired
	private TaskSearchService TaskSearchService;

	// @Test
	public void aroundNoUserTask() {
		SearchTaskQueryEntity aroundTaskQueryEntity = new SearchTaskQueryEntity();
		// aroundTaskQueryEntity.setAdcode("110108");
		aroundTaskQueryEntity.setX(116.30108);
		aroundTaskQueryEntity.setY(39.9899);
		aroundTaskQueryEntity.setRange(100);

		aroundTaskQueryEntity.setNumber(10);
		aroundTaskQueryEntity.setPage(1);

		// aroundTaskQueryEntity.setUserId(575946127350693888L);

		SearchPassiveTaskResultEntity collectPassiveTaskEntity = TaskSearchService
				.aroundNoUserTask(aroundTaskQueryEntity);

		System.out.println(collectPassiveTaskEntity);
		System.out.println();

	}

	// @Test
	public void aroundTask() {
		SearchTaskQueryEntity aroundTaskQueryEntity = new SearchTaskQueryEntity();
		// aroundTaskQueryEntity.setAdcode("110108");
		aroundTaskQueryEntity.setX(116.30165);
		aroundTaskQueryEntity.setY(39.9887);
		aroundTaskQueryEntity.setRange(10);

		aroundTaskQueryEntity.setNumber(10);
		aroundTaskQueryEntity.setPage(1);

		aroundTaskQueryEntity.setUserId(575946127350693888L);

		SearchPassiveTaskResultEntity collectPassiveTaskEntity = TaskSearchService
				.aroundTask(aroundTaskQueryEntity);

		System.out.println(collectPassiveTaskEntity);
		System.out.println();

	}
	
	@Test
	public void activeTaskAroundSearch() {
		ActiveTaskAroundSearchEntity activeTaskAroundSearchEntity = new ActiveTaskAroundSearchEntity();
		// aroundTaskQueryEntity.setAdcode("110108");
		activeTaskAroundSearchEntity.setX(116.30505);
		activeTaskAroundSearchEntity.setY(39.9821);
		activeTaskAroundSearchEntity.setRadius(10);

//		aroundTaskQueryEntity.setNumber(10);
//		aroundTaskQueryEntity.setPage(1);

//		aroundTaskQueryEntity.setUserId(575946127350693888L);

		List<ActiveTaskAroundSearchResultEntity> resultList = TaskSearchService.activeTaskAroundSearch(activeTaskAroundSearchEntity);

		System.out.println(resultList);
		System.out.println();

	}	

	// @Test
	public void districtNoUserTask() {
		SearchTaskQueryEntity aroundTaskQueryEntity = new SearchTaskQueryEntity();
		aroundTaskQueryEntity.setAdcode("110108");
		aroundTaskQueryEntity.setNumber(5);
		aroundTaskQueryEntity.setPage(1);

		// aroundTaskQueryEntity.setUserId(575946127350693888L);

		SearchPassiveTaskResultEntity collectPassiveTaskEntity = TaskSearchService
				.districtNoUserTask(aroundTaskQueryEntity);

		System.out.println(collectPassiveTaskEntity);
		System.out.println();
	}

	// @Test
	public void districtTask() {
		SearchTaskQueryEntity aroundTaskQueryEntity = new SearchTaskQueryEntity();
		aroundTaskQueryEntity.setAdcode("110108");
		aroundTaskQueryEntity.setNumber(5);
		aroundTaskQueryEntity.setPage(1);

		aroundTaskQueryEntity.setUserId(575946127350693888L);

		SearchPassiveTaskResultEntity collectPassiveTaskEntity = TaskSearchService
				.districtTask(aroundTaskQueryEntity);

		System.out.println(collectPassiveTaskEntity);
		System.out.println();
	}

	@Test
	public void districtFacetTask() {
		SearchTaskQueryEntity aroundTaskQueryEntity = new SearchTaskQueryEntity();
		aroundTaskQueryEntity.setAdcode("1101");
		aroundTaskQueryEntity.setNumber(5);
		aroundTaskQueryEntity.setPage(1);

		// aroundTaskQueryEntity.setUserId(575946127350693888L);

		List<SearchPassiveTaskFacetResult> searchPassiveTaskFacetResults = TaskSearchService
				.districtFacetTask(aroundTaskQueryEntity);

		System.out.println(searchPassiveTaskFacetResults.size());
		System.out.println();
	}
}
