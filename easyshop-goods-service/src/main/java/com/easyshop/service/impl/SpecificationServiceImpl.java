package com.easyshop.service.impl;

import com.easyshop.pojo.Specification;
import com.easyshop.pojo.SpecificationOption;
import com.easyshop.mapper.SpecificationMapper;
import com.easyshop.mapper.SpecificationOptionMapper;
import com.easyshop.service.SpecificationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zlm
 * @since 2019-02-21
 */
@Service
public class SpecificationServiceImpl extends ServiceImpl<SpecificationMapper, Specification> implements SpecificationService {

	@Autowired
	private SpecificationMapper sm;
	@Autowired
	private SpecificationOptionMapper som;
	@Override
	
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED)
	public boolean addSpecificationAndOption(Specification specification, String[] optionNames, Integer[] orders) {
		//先新增规格选项
		specification.setDel(0);
		Integer row = sm.insert(specification);
		//mybatis-plus增对象后会自动返回主键,获取该主键
		Long sid = specification.getId();
		//int j = 100/0;
		int sum =0;
		if(row>0) {
			for(int i=0;i<optionNames.length;i++) {
				SpecificationOption so = new SpecificationOption();
				so.setSpecId(sid);              //设置外键
				so.setOptionName(optionNames[i]);
				so.setOrders(orders[i]);
				so.setDel(0);
				Integer insert = som.insert(so);				
				if (insert > 0) {
					sum++;
				}
			}
		}
		if(sid>0&&sum==optionNames.length) {
			return true;
		}else {
			return false;
		}		
	}
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED)
	public boolean updateSpecificationAndOption(Specification specification, String[] optionNames, Integer[] orders) {
		
		Long id = specification.getId();
		//先删除全部,再重新插入
		Integer delete = som.delete(new EntityWrapper<SpecificationOption>().eq("spec_id", id).eq("del", 0));
		int sum=0;
		if(delete>0) {
			for(int i=0;i<optionNames.length;i++) {
				SpecificationOption sOption = new SpecificationOption();
				sOption.setDel(0);
				sOption.setOptionName(optionNames[i]);
				sOption.setSpecId(id);
				sOption.setOrders(orders[i]);
				Integer insert = som.insert(sOption);
				if (insert > 0) {
					sum++;
				}
			}
		}
		if(delete>0&&sum==optionNames.length) {
			return true;
		}else {
			return false;
		}			
	}
	@Override
	public Integer updateColumnDelById(List<Integer> list) {
		// TODO Auto-generated method stub
		return sm.updateColumnDelById(list);
	}
}
