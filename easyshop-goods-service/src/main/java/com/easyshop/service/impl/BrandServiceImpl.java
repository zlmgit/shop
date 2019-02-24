package com.easyshop.service.impl;

import com.easyshop.mapper.BrandMapper;
import com.easyshop.pojo.Brand;
import com.easyshop.service.BrandService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zlm
 * @since 2019-02-19
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
	@Autowired
	private BrandMapper brandMapper;
	public Integer updateColumnDelById(List<Integer> ids) {
		return brandMapper.updateColumnDelById(ids);
	}
}
