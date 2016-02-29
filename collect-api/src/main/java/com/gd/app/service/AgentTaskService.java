package com.gd.app.service;

import java.util.List;
import java.util.Map;

import com.gd.app.entity.AgentTask;
import com.gd.app.entity.DistrictQueryEntity;
import com.gd.app.entity.ScoreLevel;
import com.gd.app.entity.TaskAppEntity;

public interface AgentTaskService {
	 /**
     * 保存用户意见
     * 
     * @param phone
     * @param userName
     * @param content
     */
    public void saveAgentComplaint(String phone, String userName, String content);
    /**
     * 保存用户意见
     * 
     * @param phone
     * @param userName
     * @param content
     */
    public void saveAgentComplaint(String phone, String userName, String content,String deviceSys,String deviceModel);
    /**
     * 统计用户各状态的量
     * 
     * @param userName
     * @return
     */
    public List<AgentTask> queryTotalGroupByStatus(String userName);
    /**
     * 获取全部预积分
     * @return
     */
    public List<ScoreLevel> getAllScoreLevel();
    /**
     * 查询用户任务[各种状态]下的任务
     * 
     * @param userName
     * @param taskStatus
     * @return
     */
    public List<AgentTask> queryAgentTask(String userName, int taskStatus, int endResult,
                                          int beginResult,List<ScoreLevel> l1,String version);
    /**
     * 查询用户提交的任务[审核成功/失败]的量
     * 
     * @param userName
     * @param taskStatus
     * @return
     */
    public int queryAgentCount(String userName, int taskStatus);
   /**
    * 查询附近已有门址
    * @param x
    * @param y
    * @param radius
    * @param pageSize
    * @param pageNo
    * @param isGPS
    */
    public List<Map<String, String>> queryAutoTask(String x, String y, String radius,int pageSize,
            int pageNo, boolean isGPS);
    /**
     * 根据区县和关键字获取任务
     * @param adCode
     * @param dataName
     * @return
     */
    public int queryDistrictCountByKeyWord(String adCode, String dataName);
    /**
     * 分页查询区下的任务
     * 
     * @param districtId
     * @param userName
     * @return
     */
    public List<TaskAppEntity> queryTaskPageByDistrictId(String districtId, String userName,
                                                         String dataName, int endResult,
                                                         int beginResult);
    /**
     * 根据半径查询任务
     * 
     * @param taskType
     * @param x
     * @param y
     * @param radius
     * @return
     */
    public List<TaskAppEntity> queryTaskByRadiusInSearch(int taskType, String x, String y, String radius,
                                                 String userName, String dataName, int page,
                                                 int num, boolean isGPS);
    /**
     * 查询附近任务量-计数
     * 
     * @param taskType
     * @param x
     * @param y
     * @param radius
     * @param userName
     * @return
     */
    public int queryTaskByRadiusCount(int taskType, String x, String y, String radius,
            String dataName, boolean isGPS);
    /**
     * 查询附近任务
     * @param taskType
     * @param x
     * @param y
     * @param radius
     * @param userName
     * @param dataName
     * @param endResult
     * @param beginResult
     * @param isGPS
     * @return
     */
    public List<TaskAppEntity> queryTaskByRadiusRd(int taskType, String x, String y, String radius,
            String userName, String dataName, int endResult,
            int beginResult, boolean isGPS);
    /**
     * 用户定向任务计数
     * @param userName
     * @param dataName
     * @return
     */
    public int queryUserCountByKeyWord(String userName, String dataName);
    /**
     * 用户定向任务查询
     * @param userName
     * @param dataName
     * @param endResult
     * @param beginResult
     * @return
     */
    public List<TaskAppEntity> queryTaskPageByUser(String userName,
            String dataName, int endResult,
            int beginResult);
    /**
     * 用户定向任务查询-按道路
     * @param userName
     * @param dataName
     * @param endResult
     * @param beginResult
     * @return
     */
    public List<TaskAppEntity> queryTaskPageByUserRd(String userName,
            String dataName, int endResult,
            int beginResult);
    /**
     * 获取城市下任务量接口(分区县)
     * @param adCode
     * @return
     */
    public List<DistrictQueryEntity> queryTaskCountByAdCode(String adCode);
    /**
     * 我征服的城市
     * @param userName
     * @return
     */
    public Map<String,Integer> queryMyConquer(String userName);
    /**
     * 被动任务反馈
     * @param userName
     * @param taskId
     * @param dataName
     * @param errorCode
     * @param comment
     * @param deviceInfo
     */
    public void addPassiveTaskBack(String userName,String taskId,String dataName,String errorCode,String comment,String deviceInfo);
    /**
     * 获取属性
     * @param key
     * @return
     */
    public String getProperty(String key);

}
