package com.gd.app.web;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.gd.app.desdata.Cryptor;
import com.gd.app.desdata.CryptorNew;
import com.gd.app.entity.ScoreLevel;
import com.gd.app.service.AgentTaskService;

@Controller
public class ScoreLevelInit {
	public static List<ScoreLevel> scoreLevels = null;
	@Resource
	AgentTaskService agentTaskService;

	private ScoreLevelInit() {
		// 初始化兼容的KEY
		Cryptor.RC4Init("4cea79d4-3406-463f-9593-09cc9264cb82");
		// 初始化新KEY
		CryptorNew.RC4Init("4cea79d4-3406-463f-9593-09cc9264cb82");

	}

	@PostConstruct
	private void run() {
		scoreLevels = agentTaskService.getAllScoreLevel();

	}

}
