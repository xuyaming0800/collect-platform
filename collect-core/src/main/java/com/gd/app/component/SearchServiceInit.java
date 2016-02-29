package com.gd.app.component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.gd.app.service.impl.SearchEngineServiceImp;
import com.mapabc.search.score.Similarity;

@Component
public class SearchServiceInit {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private SearchEngineServiceImp searchEngineService;
	@PostConstruct
	private void run(){
		/**初始化 做一次相似度计算，加载相似度计算相关的文件*/
		logger.info("初始化索引");
		Similarity.score("北京市海淀区苏州街3号", "北京市朝阳区阜成路100号",false);
		
		/**初始化 做预搜索*/
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("keyword", "苏州街3号");
		paraMap.put("adcode", "1101");

		searchEngineService.matchAgentBaseDataFromSearchEngine(paraMap);
		searchEngineService.matchGDBaseDataFromSearchEngine(paraMap);
		searchEngineService.matchAuditbackDataFromSearchEngine(paraMap);
		
	}
	

}
