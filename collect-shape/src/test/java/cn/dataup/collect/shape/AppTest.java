package cn.dataup.collect.shape;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autonavi.collect.constant.CommonConstant;

import cn.dataup.collect.shape.component.ShapeInfoCacheComponent;
import cn.dataup.collect.tools.service.CollectShapeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class AppTest {
	@Autowired
	private ShapeInfoCacheComponent shapeInfoCacheComponent;
	@Autowired
	private CollectShapeService collectShapeService;
	@Test
	public void test() throws Exception {
//		System.out.println(collectShapeService.checkLocationsVaild(118.5578, 36.1037, 656744514102231040L, CommonConstant.GPS_SYSTEM.BAIDU.getCode()));
		while (true) {
			
		}

	}
}
