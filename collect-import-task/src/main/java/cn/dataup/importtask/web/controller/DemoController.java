package cn.dataup.importtask.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.dataup.importtask.entity.ImportTaskEntity;
import cn.dataup.importtask.service.ImportDemoService;

import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;

@Controller
public class DemoController {
	@Autowired
	private ImportDemoService importDemoService;
	private Logger logger = LogManager.getLogger(this.getClass());
	@RequestMapping("/demo/importDemo")
	public void importDemo(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("测试导入");
		ArrayList<ImportTaskEntity> list=new ArrayList<ImportTaskEntity>();
		for(int i=0;i<5;i++){
			ImportTaskEntity entity=new ImportTaskEntity();
			CollectPassivePackage collectPassivePackage=new CollectPassivePackage();
			collectPassivePackage.setTaskPackageCate("通用汽车广告");
			collectPassivePackage.setTaskPackageName("测试采集包"+i);
			collectPassivePackage.setTaskPackageDesc("苏州街"+i+"号");
			collectPassivePackage.setTaskPackagePay(3D);
			entity.setCollectPassivePackage(collectPassivePackage);
			List<CollectPassiveTask> collectPassiveTaskList = new ArrayList<CollectPassiveTask>();
			CollectPassiveTask task=new CollectPassiveTask();
			task.setDataName(task.getDataName()+i);
			collectPassiveTaskList.add(task);
			List<CollectOriginalCoordinate> collectOriginalCoordinateList =new ArrayList<CollectOriginalCoordinate>();
			CollectOriginalCoordinate cood=new CollectOriginalCoordinate();
			cood.setTaskSampleImg("2015_01_01_157_test_1.jpg");
			cood.setOriginalX(116.31196D);
			cood.setOriginalY(39.988226D);
			cood.setCoordinateStatus(0);
			cood.setOriginalAdcode(110108);
			collectOriginalCoordinateList.add(cood);
			entity.setCollectPassiveTaskList(collectPassiveTaskList);
			entity.setCollectOriginalCoordinateList(collectOriginalCoordinateList);
			list.add(entity);
		}
		importDemoService.importTasks(list);
		
	}
}
