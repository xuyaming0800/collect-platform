package cn.dataup.importtask.service;

import java.util.List;

public interface SelectService {

	public Long setGeocodingOrImportData(String tableName, Integer geoOrImport) throws Exception;

	public List<String> getTables(String startWtih);

	public Long getCountSuccessCalculateGeoCoding(String tableName, Integer geoOrImport) throws Exception;

	public String getExceptionAllinformation(Exception e);

}
