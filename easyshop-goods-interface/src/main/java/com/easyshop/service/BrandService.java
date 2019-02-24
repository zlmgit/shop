package com.easyshop.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.easyshop.pojo.Brand;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zlm
 * @since 2019-02-19
 */
public interface BrandService extends IService<Brand> {
	Integer updateColumnDelById(List<Integer> ids);
}
