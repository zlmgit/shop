package com.easyshop.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.easyshop.pojo.Brand;
import com.easyshop.pojo.Specification;
import com.easyshop.pojo.SpecificationOption;
import com.easyshop.service.SpecificationOptionService;
import com.easyshop.service.SpecificationService;
import com.easyshop.utils.PageResult;
import com.easyshop.utils.ResultMsg;

import javassist.expr.NewArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zlm
 * @since 2019-02-21
 */
@Controller
@RequestMapping("/specification")
public class SpecificationController {

	@Reference
	private SpecificationService specificationService;
	
	@Reference
	private SpecificationOptionService sos;

	/**
	 * 01-展示分页
	 * @param specification
	 * @param model
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list/{pageIndex}")	
	public String getList(Specification specification,Model model,@PathVariable("pageIndex")Integer pageIndex,@RequestParam(value="pageSize",defaultValue="5")Integer pageSize) {
		
		Page<Specification> page= new Page<Specification>(pageIndex, pageSize);
		
		EntityWrapper<Specification> wrapper = new EntityWrapper<Specification>();
		
		if(specification!=null&&specification.getSpecName()!=null&&!specification.getSpecName().equals("")) {
			wrapper.like("spec_name", specification.getSpecName());// 查询的条件 根据品牌的名字模糊查询 like
		}
		wrapper.eq("del", 0);// 0表示未删除 查询未删除 如果删除了就不查询出来 0表示未删除 1表示删除 s(数据库并非是真删除而是作逻辑删除)
		Page<Specification> results = specificationService.selectPage(page, wrapper);
		//获取总条数
		int totalCount=((Long)results.getTotal()).intValue();
		//获取总页数
		int totalPage = ((Long)results.getPages()).intValue();
		//查询是否有上一页
		boolean hasPrevious = results.hasPrevious();
		//查询是否有下一页
		boolean hasNext = results.hasNext();
		//查询每页显示的数据
		List<Specification> specifications = results.getRecords();
		PageResult<Specification> pageResult = new PageResult<Specification>(totalCount,totalPage, pageIndex, pageSize, specifications, specification);
		model.addAttribute("pageResult", pageResult);
		model.addAttribute("hasPrevious", hasPrevious);
		model.addAttribute("hasNext", hasNext);
		return "specification";
	}
	/**
	 * 02-新增品牌
	 */
	@ResponseBody
	@RequestMapping("/add")
	public ResultMsg add(Specification specification,String[] optionNames,Integer[] orders) {
		ResultMsg result = null;
		try {
			boolean b = specificationService.addSpecificationAndOption(specification,optionNames,orders);
			if (b == true) {
				result = new ResultMsg(true, "新增成功");
			} else {
				result = new ResultMsg(false, "新增失败");
			}
		} catch (Exception e) {
			result = new ResultMsg(false, "新增失败");
			e.printStackTrace();
		}		
		return result;
	}
	/**
	 * 03-修改信息数据回显
	 */
	@ResponseBody
	@RequestMapping("/getSpecificationAndOptionById")
	public HashMap<String, Object>	getSpecificationAndOptionById(Integer id){
		Specification specification = specificationService.selectById(id);
		List<SpecificationOption> specificationOptions = sos.selectList(new EntityWrapper<SpecificationOption>().eq("spec_id", id));
		HashMap<String, Object> map = new HashMap<>();
		map.put("specification", specification);
		map.put("specificationOption", specificationOptions);
		return map;
	}
	/**
	 * 04-修改信息
	 */
	@ResponseBody
	@RequestMapping("/update")
	public ResultMsg update(Specification specification,String[] optionNames,Integer[] orders) {
		ResultMsg result = null;
		try {
			boolean b = specificationService.updateSpecificationAndOption(specification,optionNames,orders);
			if (b == true) {
				result = new ResultMsg(true, "修改成功");
			} else {
				result = new ResultMsg(false, "修改失败");
			}
		} catch (Exception e) {
			result = new ResultMsg(false, "修改失败");
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 05-删除单个信息
	 */
	@ResponseBody
	@RequestMapping("/delete/{id}")
	public ResultMsg delete(@PathVariable("id")Integer id) {
		boolean b;
		ResultMsg result = null;
		try {
			Specification specification = specificationService.selectById(id);
			specification.setDel(1); //状态修改为1 更新回去
			b = specificationService.updateById(specification);
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
		/*List<Specification> specifications = specificationService.selectBatchIds(list);
		for (Specification specification : specifications) {
			specification.setDel(1);
		}*/
		Integer b;
		ResultMsg result = null;
		try {
			b =specificationService.updateColumnDelById(list);			
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

