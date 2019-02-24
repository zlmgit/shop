package com.easyshop.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.easyshop.pojo.Brand;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zlm
 * @since 2019-02-19
 */
public interface BrandMapper extends BaseMapper<Brand> {
	Integer updateColumnDelById(List<Integer> ids);
}
