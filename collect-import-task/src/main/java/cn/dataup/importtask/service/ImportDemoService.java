package cn.dataup.importtask.service;

import java.util.ArrayList;

import cn.dataup.importtask.entity.ImportTaskEntity;

public interface ImportDemoService {
	public boolean importTasks(ArrayList<ImportTaskEntity> importTaskEntityList);

}
