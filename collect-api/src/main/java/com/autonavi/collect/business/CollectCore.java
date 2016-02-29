package com.autonavi.collect.business;

import com.autonavi.collect.exception.BusinessException;

/**
 *  核心API
 * @author xuyaming
 *
 * @param <K> execute返回类型
 * @param <V> execute入参类型
 */
public interface CollectCore<K,V> {
	// 因为某些第三方jar直接抛出Exception（比如redis就会直接抛出Exception），
	// 无法和业务上的Exception区分开，所以这里抛出BusinessException，专门用于业务上的Exception
	K execute(V obj) throws BusinessException;
}
