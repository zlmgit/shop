package com.easyshop.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.easyshop.pojo.Brand;
import com.easyshop.pojo.JsonBrandSpec;
import com.easyshop.pojo.JsonCustomAttributeItems;
import com.easyshop.pojo.Specification;
import com.easyshop.pojo.TypeTemplate;
import com.easyshop.service.BrandService;
import com.easyshop.service.SpecificationService;
import com.easyshop.service.TypeTemplateService;
import com.easyshop.utils.JsonUtils;
import com.easyshop.utils.PageResult;
import com.easyshop.utils.ResultMsg;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zlm
 * @since 2019-02-22
 */
@Controller
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
	
	@Reference
	private TypeTemplateService typeTemplateService;
	
	@Reference
	private BrandService brandService;
	
	@Reference
	private SpecificationService ss;
	
	/**
	 * 01-分页显示全部信息
	 * @param typeTemplate
	 * @param model
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list/{pageIndex}")	
	public String getList(TypeTemplate typeTemplate,Model model,@PathVariable("pageIndex")Integer pageIndex,@RequestParam(value="pageSize",defaultValue="5")Integer pageSize) {
		
		Page<TypeTemplate> page= new Page<TypeTemplate>(pageIndex, pageSize);
		
		EntityWrapper<TypeTemplate> wrapper = new EntityWrapper<TypeTemplate>();
		
		if(typeTemplate!=null&&typeTemplate.getName()!=null&&!typeTemplate.getName().equals("")) {
			wrapper.like("name", typeTemplate.getName());// 查询的条件 根据品牌的名字模糊查询 like
		}
		wrapper.eq("del", 0);// 0表示未删除 查询未删除 如果删除了就不查询出来 0表示未删除 1表示删除 s(数据库并非是真删除而是作逻辑删除)
		Page<TypeTemplate> results = typeTemplateService.selectPage(page, wrapper);
		//获取总条数
		int totalCount=((Long)results.getTotal()).intValue();
		//获取总页数
		int totalPage = ((Long)results.getPages()).intValue();
		//查询是否有上一页
		boolean hasPrevious = results.hasPrevious();
		//查询是否有下一页
		boolean hasNext = results.hasNext();
		//查询每页显示的数据
		List<TypeTemplate> typeTemplates = results.getRecords();
		
		
		bscToJson(typeTemplates);				
					
		PageResult<TypeTemplate> pageResult = new PageResult<TypeTemplate>(totalCount,totalPage, pageIndex, pageSize, typeTemplates, typeTemplate);
		model.addAttribute("pageResult", pageResult);
		model.addAttribute("hasPrevious", hasPrevious);
		model.addAttribute("hasNext", hasNext);
		return "type_template";
	}
	private void bscToJson(List<TypeTemplate> typeTemplates) {
		int typeLen= typeTemplates.size();	
		
		for(int j=0;j<typeLen;j++) {
			
			TypeTemplate template = typeTemplates.get(j);
			
			//将	关联品牌转换成字符串重新存入		
			StringBuffer brandsBuffer = new StringBuffer();
			List<JsonBrandSpec> jsonBrands = JsonUtils.jsonToList(template.getBrandIds(), JsonBrandSpec.class);
			int lenBrands =jsonBrands.size();
			for(int i=0;i<lenBrands;i++) {
				if(i<lenBrands-1) {
					brandsBuffer.append(jsonBrands.get(i).getText()+",");
				}else {
					brandsBuffer.append(jsonBrands.get(i).getText());
				}
			}
			template.setBrandIds(brandsBuffer.toString());
			
			//将	关联规格转换成字符串重新存入	
			StringBuffer specsBuffer = new StringBuffer();
			List<JsonBrandSpec> jsonSpecs = JsonUtils.jsonToList(template.getSpecIds(), JsonBrandSpec.class);
			int lenSpec =jsonSpecs.size();
			for(int k=0;k<lenSpec;k++) {
				if(k<lenSpec-1) {
					specsBuffer.append(jsonSpecs.get(k).getText()+",");
				}else {
					specsBuffer.append(jsonSpecs.get(k).getText());
				}
			}
			template.setSpecIds(specsBuffer.toString());
			
			//将	自定义属性换成字符串重新存入	
			String customAttributeItems = template.getCustomAttributeItems();
			if(customAttributeItems!=null&&!customAttributeItems.equals("")) {
				StringBuffer casBuffer = new StringBuffer();
				List<JsonCustomAttributeItems> cas = JsonUtils.jsonToList(customAttributeItems, JsonCustomAttributeItems.class);
				int lenCas =cas.size();
				for(int z=0;z<lenCas;z++) {
					if(z<lenCas-1) {
						casBuffer.append(cas.get(z).getText()+",");
					}else {
						casBuffer.append(cas.get(z).getText());
					}
				}
				template.setCustomAttributeItems(casBuffer.toString());
			}			
		}
	}
	/**
	 * 02-新增获得规格,品牌信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getBrandsAndSpec")
	public HashMap<String, Object> getBrandsAndSpec(){
		//获得所有品牌为brands设置标记
		List<Brand> brands = brandService.selectList(new EntityWrapper<Brand>().eq("del", 0));
		for(int i=0;i<brands.size();i++) {
			Brand brand = brands.get(i);						
			brand.setCkBox(brand.getId()+"_"+brand.getName());
		}
		//获得所有规格为specifications设置标记
		List<Specification> specifications = ss.selectList(new EntityWrapper<Specification>().eq("del",0));
		for(int i=0;i<specifications.size();i++) {
			Specification specification = specifications.get(i);
			specification.setCkBox(specification.getId()+"_"+specification.getSpecName());
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("brands", brands);
		map.put("specifications", specifications);
		return map;
	}
	/**
	 * 03-新增品牌
	 */
	@ResponseBody
	@RequestMapping("/add")
	public ResultMsg add(TypeTemplate typeTemplate,String[] brands,String[] specifications,String[] customAttributeItems) {
		typeTemplate.setDel(0);
		//brand_ids列转化
		List<JsonBrandSpec> bs=new ArrayList<>();
		for(int i=0;i<brands.length;i++) {
			String[] strings = brands[i].split("_");
			JsonBrandSpec jbs=new JsonBrandSpec();
			jbs.setId(Integer.parseInt(strings[0]));
			jbs.setText(strings[1]);
			bs.add(jbs);
		}
		String brandids = JsonUtils.objectToJson(bs);
		typeTemplate.setBrandIds(brandids);
		
		//spec_ids列转化
		List<JsonBrandSpec> spec=new ArrayList<>();
		for(int i=0;i<specifications.length;i++) {
			String[] strings = specifications[i].split("_");
			JsonBrandSpec sps=new JsonBrandSpec();
			sps.setId(Integer.parseInt(strings[0]));
			sps.setText(strings[1]);
			spec.add(sps);
		}
		String spec_ids = JsonUtils.objectToJson(spec);
		typeTemplate.setSpecIds(spec_ids);
		
		//custom_attribute_items列转化
		List<JsonCustomAttributeItems> jcas=new ArrayList<>();
		for(int i=0;i<customAttributeItems.length;i++) {
			JsonCustomAttributeItems js=new JsonCustomAttributeItems();
			js.setText(customAttributeItems[i]);
			jcas.add(js);
		}
		typeTemplate.setCustomAttributeItems(JsonUtils.objectToJson(jcas));
		ResultMsg result=null;
		try {
			boolean b = typeTemplateService.insert(typeTemplate);
			if (b == true) {
				result = new ResultMsg(true, "删除成功");
			} else {
				result = new ResultMsg(false, "删除失败");
			}
		} catch (Exception e) {
			result = new ResultMsg(false, "删除失败");
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 04-修改模板信息获得回显信息
	 */
	@ResponseBody
	@RequestMapping("/getTypeTemplateId/{id}")
	public HashMap<String, Object> getTypeTemplateId(@PathVariable("id") Integer id){
		
		TypeTemplate template = typeTemplateService.selectById(id);   //根据id查询模板信息
		
		List<Brand> brands = brandService.selectList(new EntityWrapper<Brand>().eq("del", 0));//查询出所有的品牌名称		
		String brandIds = template.getBrandIds();//获得模板信息中的JSON字符串转成集合
		List<JsonBrandSpec> brandList = JsonUtils.jsonToList(brandIds,JsonBrandSpec.class);
		for(int i=0;i<brandList.size();i++) {
			JsonBrandSpec jsonBrandSpec = brandList.get(i);
			int id2 = jsonBrandSpec.getId().intValue();
			for(int j=0;j<brands.size();j++) {
				Brand brand = brands.get(j);
				int id3 = brand.getId().intValue();
				if(id2==id3) {
					brand.setFlag(true);
				}
			}
		}
		for(int i=0;i<brands.size();i++) {
			Brand brand = brands.get(i);						
			brand.setCkBox(brand.getId()+"_"+brand.getName());  //为brands设置id与name标记
		}
		
		
		List<Specification> specifications = ss.selectList(new EntityWrapper<Specification>().eq("del", 0));//查询出所有的规格名称
		String specIds = template.getSpecIds();   //获得模板信息中规格信息的JSON字符串转成集合
		List<JsonBrandSpec> specList = JsonUtils.jsonToList(specIds, JsonBrandSpec.class);
		for(int i=0;i<specList.size();i++) {
			JsonBrandSpec jsonBrandSpec = specList.get(i);
			int id2 = jsonBrandSpec.getId().intValue();
			for(int j=0;j<specifications.size();j++) {
				Specification specification = specifications.get(j);
				int id3 = specification.getId().intValue();
				if(id2==id3) {
					specification.setFlag(true);
				}
			}
		}
		for(int i=0;i<specifications.size();i++) {
			Specification specification = specifications.get(i);
			specification.setCkBox(specification.getId()+"_"+specification.getSpecName());//为specifications设置id与name的标记
		}
		
		String jsonCustomAttributeItems = template.getCustomAttributeItems();
		List<JsonCustomAttributeItems> customAttributeItems = JsonUtils.jsonToList(jsonCustomAttributeItems, JsonCustomAttributeItems.class);
		HashMap<String, Object> map= new HashMap<>();
		map.put("template", template);
		map.put("brands", brands);
		map.put("specifications", specifications);
		map.put("customAttributeItems", customAttributeItems);
		return map;
	}
	/**
	 * 03-更新品牌
	 */
	@ResponseBody
	@RequestMapping("/update")
	public ResultMsg update(TypeTemplate typeTemplate,String[] brands,String[] specifications,String[] customAttributeItems) {
		
		//brand_ids列转化
		List<JsonBrandSpec> bs=new ArrayList<>();
		for(int i=0;i<brands.length;i++) {
			String[] strings = brands[i].split("_");
			JsonBrandSpec jbs=new JsonBrandSpec();
			jbs.setId(Integer.parseInt(strings[0]));
			jbs.setText(strings[1]);
			bs.add(jbs);
		}
		String brandids = JsonUtils.objectToJson(bs);
		typeTemplate.setBrandIds(brandids);
		
		//spec_ids列转化
		List<JsonBrandSpec> spec=new ArrayList<>();
		for(int i=0;i<specifications.length;i++) {
			String[] strings = specifications[i].split("_");
			JsonBrandSpec sps=new JsonBrandSpec();
			sps.setId(Integer.parseInt(strings[0]));
			sps.setText(strings[1]);
			spec.add(sps);
		}
		String spec_ids = JsonUtils.objectToJson(spec);
		typeTemplate.setSpecIds(spec_ids);
		
		//custom_attribute_items列转化
		if(customAttributeItems!=null&&customAttributeItems.length>0) {
			List<JsonCustomAttributeItems> jcas=new ArrayList<>();
			for(int i=0;i<customAttributeItems.length;i++) {
				JsonCustomAttributeItems js=new JsonCustomAttributeItems();
				js.setText(customAttributeItems[i]);
				jcas.add(js);
			}
			typeTemplate.setCustomAttributeItems(JsonUtils.objectToJson(jcas));
		}else {
			typeTemplate.setCustomAttributeItems("");
		}
		
		ResultMsg result=null;
		try {
			boolean b = typeTemplateService.updateById(typeTemplate);
			if (b == true) {
				result = new ResultMsg(true, "更新成功");
			} else {
				result = new ResultMsg(false, "更新失败");
			}
		} catch (Exception e) {
			result = new ResultMsg(false, "更新失败");
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 04-删除单个信息
	 */
	@ResponseBody
	@RequestMapping("/deletetypeTemplate")
	public ResultMsg deleteSome(TypeTemplate typeTemplate) {		
		typeTemplate.setDel(1);
		ResultMsg result = null;
		try {
			boolean b =typeTemplateService.updateById(typeTemplate);
			if (b ) {
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
	 * 05-批量删除
	 */
	@ResponseBody
	@RequestMapping("/deleteSome")
	public ResultMsg deleteSome(Long[] ids) {
		List<TypeTemplate> typeTemplates = new ArrayList<>();
		for(int i=0;i<ids.length;i++) {
			TypeTemplate typeTemplat=new TypeTemplate();
			typeTemplat.setId(ids[i]);
			typeTemplat.setDel(1);
			typeTemplates.add(typeTemplat);
		}				
		Boolean b;
		ResultMsg result = null;
		try {
			b =typeTemplateService.updateBatchById(typeTemplates);
			if (b ) {
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

