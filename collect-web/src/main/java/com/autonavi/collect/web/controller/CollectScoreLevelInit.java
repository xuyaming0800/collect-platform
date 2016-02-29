package com.autonavi.collect.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.autonavi.collect.bean.CollectDicScoreDetail;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.service.TaskScoreService;
import com.gd.app.desdata.Cryptor;
import com.gd.app.desdata.CryptorNew;

public class CollectScoreLevelInit {
	public static Map<Long,CollectDicScoreDetail> scoreLevels ;
	public static Map<Integer,CollectDicScoreDetail> currentScoreTypes ;
	@Autowired
	private TaskScoreService taskScoreService;

	private CollectScoreLevelInit() {
		// 初始化兼容的KEY
		Cryptor.RC4Init("4cea79d4-3406-463f-9593-09cc9264cb82");
		// 初始化新KEY
		CryptorNew.RC4Init("4cea79d4-3406-463f-9593-09cc9264cb82");

	}

	@PostConstruct
	private void run() {
		scoreLevels=new HashMap<Long,CollectDicScoreDetail>();
		currentScoreTypes=new HashMap<Integer,CollectDicScoreDetail>();
		List<CollectDicScoreDetail> scores = taskScoreService.getAllScoreInfo();
		for(CollectDicScoreDetail detail:scores){
			scoreLevels.put(detail.getId(),detail);
			if(detail.getStatus().intValue()==CommonConstant.SCORE_STATUS_OK){
				currentScoreTypes.put(detail.getTaskType(),detail);
			}
		}

	}

}
