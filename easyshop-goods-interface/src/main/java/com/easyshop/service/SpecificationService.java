package com.easyshop.service;

import com.easyshop.pojo.Specification;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zlm
 * @since 2019-02-21
 */
public interface SpecificationService extends IService<Specification> {

	boolean addSpecificationAndOption(Specification specification, String[] optionNames, Integer[] orders);

	boolean updateSpecificationAndOption(Specification specification, String[] optionNames, Integer[] orders);

	Integer updateColumnDelById(List<Integer> list);	
}
