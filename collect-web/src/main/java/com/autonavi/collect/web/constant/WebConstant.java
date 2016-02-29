package com.autonavi.collect.web.constant;

public class WebConstant {
	public final static String USE_CONTENT_DES="USE_CONTENT_DES";
	public final static String DEV_MODE="DEV_MODE";
	public final static String ORGIN_IMG_URL="ORGIN_IMG_URL";
	public final static String COLLECT_IMG_URL="COLLECT_IMG_URL";
	public final static String COLLECT_IMG_REAL_PATH="COLLECT_IMG_REAL_PATH";
	
	public final static String IMG_FILTER_FILEPATHS="IMG_FILTER_FILEPATHS";
	public final static String IMG_FILTER_REDIRECT_ADDRESS="IMG_FILTER_REDIRECT_ADDRESS";
	public final static String IMG_FILTER_APP_ROOT_PATH="IMG_FILTER_APP_ROOT_PATH";
	public final static String IMG_FILTER_IMG_ROOT_PATH="IMG_FILTER_IMG_ROOT_PATH";
	
	public final static String COLLECT_MAX_PASSIVE_TASK_SIZE="COLLECT_MAX_PASSIVE_TASK_SIZE";
	
	public static String myip="";
	
	
	public enum UNALLOT_QUERY_TYPE {
		// 0 省市区检索 1周边检索 2 省市数量查找
		District(0), Arround(1),District_Count(2),Arround_All(3);

		private int code;

		public int getCode() {
			return code;
		}

		private UNALLOT_QUERY_TYPE(int code) {
			this.code=code;
		}
	}
	public enum MYTASK_QUERY_TYPE {
		// 0 包 1步骤 2 步骤图片
		PACKAGES(0), TASKS(1),IMGS(2),DELETE(3),COUNT(4),COUNTS(5);

		private int code;

		public int getCode() {
			return code;
		}

		private MYTASK_QUERY_TYPE(int code) {
			this.code=code;
		}
	}
	public enum CHECK_TASK_ARROUND {
		ARROUND(0), POINT(1),ARROUND_H5(2), POINT_H5(3);

		private int code;

		public int getCode() {
			return code;
		}

		private CHECK_TASK_ARROUND(int code) {
			this.code=code;
		}
	}
	
	public enum ALLOT_STATUS{
		//0 未分配 1已经分配 附近区域任务搜索使用
		UNALLOT("0"),ALLOT("1");
		private String code;
		public String getCode(){
			return code;
		}
		private ALLOT_STATUS(String code){
			this.code=code;
		}
	}

}
