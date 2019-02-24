package com.easyshop.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.easyshop.pojo.Brand;
import com.easyshop.service.BrandService;
import com.easyshop.utils.PageResult;
import com.easyshop.utils.ResultMsg;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zlm
 * @since 2019-02-19
 */
@Controller
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	/**
	 * 01-分页显示全部信息
	 * @param brand
	 * @param model
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list/{pageIndex}")	
	public String getList(Brand brand,Model model,@PathVariable("pageIndex")Integer pageIndex,@RequestParam(value="pageSize",defaultValue="5")Integer pageSize) {
		
		Page<Brand> page= new Page<Brand>(pageIndex, pageSize);
		
		EntityWrapper<Brand> wrapper = new EntityWrapper<Brand>();
		
		if(brand!=null&&brand.getName()!=null&&!brand.getName().equals("")) {
			wrapper.like("name", brand.getName());// 查询的条件 根据品牌的名字模糊查询 like
		}
		wrapper.eq("del", 0);// 0表示未删除 查询未删除 如果删除了就不查询出来 0表示未删除 1表示删除 s(数据库并非是真删除而是作逻辑删除)
		Page<Brand> results = brandService.selectPage(page, wrapper);
		//获取总条数
		int totalCount=((Long)results.getTotal()).intValue();
		//获取总页数
		int totalPage = ((Long)results.getPages()).intValue();
		//查询是否有上一页
		boolean hasPrevious = results.hasPrevious();
		//查询是否有下一页
		boolean hasNext = results.hasNext();
		//查询每页显示的数据
		List<Brand> brands = results.getRecords();
		PageResult<Brand> pageResult = new PageResult<Brand>(totalCount,totalPage, pageIndex, pageSize, brands, brand);
		model.addAttribute("pageResult", pageResult);
		model.addAttribute("hasPrevious", hasPrevious);
		model.addAttribute("hasNext", hasNext);
		return "brand";
	}
	/**
	 * 02-新增品牌
	 */
	@ResponseBody
	@RequestMapping("/add")
	public ResultMsg add(Brand brand) {
		if(brand.getFirstChar().length()>1) {
			return new ResultMsg(false,"新增失败");//如果首字符长度大于1,不新增,数据库字段设计1个
		}
		brand.setDel(0);	
		try {
			boolean b = brandService.insert(brand);
			if(b) {
				return new ResultMsg(true,"新增成功");
			}else {
				return new ResultMsg(false,"新增失败");
			}
		} catch (Exception e) {			
			e.printStackTrace();
			return new ResultMsg(false,"新增失败");
		}		
	}
	/**
	 * 03-修改异步回显
	 */
	@ResponseBody
	@RequestMapping("/getBrandById")
	public Brand getBrandById(Integer id) {
		return brandService.selectById(id);
	}
	/**
	 * 04-修改信息
	 */
	@ResponseBody
	@RequestMapping("/update")
	public ResultMsg update(Brand brand) {
		if(brand.getFirstChar().length()>1) {
			return new ResultMsg(false,"更新失败");//如果首字符长度大于1,不新增,数据库字段设计1个
		}
		boolean b;
		ResultMsg result = null;
		try {
			b = brandService.updateById(brand);
			if (b == true) {
				
				result = new ResultMsg(true, "更新成功");
			} else {
				
				result = new ResultMsg(false, "更新失败");
			}
		} catch (Exception e) {
			
			result = new ResultMsg(false, "更新失败");
		}
		return result;
	}
	/**
	 * 04-删除单个信息
	 */
	@ResponseBody
	@RequestMapping("/delete/{id}")
	public ResultMsg delete(@PathVariable("id")Integer id) {
		boolean b;
		ResultMsg result = null;
		try {
			Brand brand = brandService.selectById(id);
			brand.setDel(1); //状态修改为1 更新回去
			b = brandService.updateById(brand);
			if (b == true) {
				result = new ResultMsg(true, "删除成功");
			} else {
				result = new ResultMsg(false, "删除失败");
			}
		} catch (Exception e) {
			result = new ResultMsg(false, "删除失败");
		}
		return result;
	}
	/**
	 * 04-批量删除
	 */
	@ResponseBody
	@RequestMapping("/deleteSome")
	public ResultMsg deleteSome(Integer[] ids) {
		List<Integer> list = Arrays.asList(ids);
		List<Brand> brands = brandService.selectBatchIds(list);
		for (Brand brand : brands) {
			brand.setDel(1);
		}
		Integer b;
		ResultMsg result = null;
		try {
			b =brandService.updateColumnDelById(list);			
			if (b > 0) {
				result = new ResultMsg(true, "删除成功");
			} else {
				result = new ResultMsg(false, "删除失败");
			}
		} catch (Exception e) {
			result = new ResultMsg(false, "删除失败");
		}
		return result;
	}	
}

