package cn.dataup.mgr.web.bean;

public class ResultEntity {
	

	private String code;
	
	private String msg;

	private Object result;
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object resultData) {
		this.result = resultData;
	}

}
