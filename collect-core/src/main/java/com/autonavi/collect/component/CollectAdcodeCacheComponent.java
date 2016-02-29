package com.autonavi.collect.component;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.util.AdCodeToNameConvert;
@Component
public class CollectAdcodeCacheComponent {
	@PostConstruct
	private void run() {
		// 初始化Adcode缓存
		try {
			AdCodeToNameConvert.getInstance();
		} catch (Exception e) {
			throw new BusinessRunException(
					BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

}
