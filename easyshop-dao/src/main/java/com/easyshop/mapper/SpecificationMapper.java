package com.easyshop.mapper;

import com.easyshop.pojo.Specification;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zlm
 * @since 2019-02-21
 */
public interface SpecificationMapper extends BaseMapper<Specification> {

	Integer updateColumnDelById(List<Integer> list);

}
