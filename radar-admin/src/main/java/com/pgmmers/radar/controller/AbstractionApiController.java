package com.pgmmers.radar.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pgmmers.radar.dal.bean.AbstractionQuery;
import com.pgmmers.radar.enums.PluginType;
import com.pgmmers.radar.service.common.CommonResult;
import com.pgmmers.radar.service.engine.vo.DataColumnInfo;
import com.pgmmers.radar.service.enums.DataType;
import com.pgmmers.radar.service.model.AbstractionService;
import com.pgmmers.radar.service.model.FieldService;
import com.pgmmers.radar.service.model.PreItemService;
import com.pgmmers.radar.vo.model.AbstractionVO;
import com.pgmmers.radar.vo.model.FieldVO;
import com.pgmmers.radar.vo.model.PreItemVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/services/v1/abstraction")
public class AbstractionApiController {

    @Autowired
    private AbstractionService abstractionService;

    @Autowired
    private FieldService fieldService;
    @Autowired
    private PreItemService preItemService;

    @GetMapping("/{id}")
    public CommonResult get(@PathVariable Long id) {
        CommonResult result = new CommonResult();
        AbstractionVO abstractionVO = abstractionService.get(id);
        if (abstractionVO != null) {
            result.setSuccess(true);
            result.getData().put("abstraction", abstractionVO);
        }
        return result;
    }

   @GetMapping("/list/{modelId}")
    public CommonResult list(@PathVariable Long modelId) {
        return abstractionService.list(modelId);
    }

   @PostMapping
    public CommonResult query(@RequestBody AbstractionQuery query) {
        return abstractionService.query(query);
    }

   @GetMapping("/datacolumns/{modelId}")
    public CommonResult getDataColumns(@PathVariable Long modelId) {
        List<DataColumnInfo> list = new ArrayList<>();
        // 1、Data
        DataColumnInfo ds = new DataColumnInfo(DataType.FIELDS.getDesc(), DataType.FIELDS.getName());
        List<FieldVO> listField = fieldService.listField(modelId);
        if(listField!=null&&listField.size()!=0){
	        for (FieldVO field : listField) {
	            ds.addChildren(field.getLabel(), field.getFieldName(), field.getFieldType());
	        }
	        list.add(ds);
        }

        // 2、PREPARE
        ds = new DataColumnInfo(DataType.PREITEMS.getDesc(), DataType.PREITEMS.getName());
        List<PreItemVO> listPreItem = preItemService.listPreItem(modelId);
        if(listPreItem!=null&&listPreItem.size()!=0){
	        for (PreItemVO preItem : listPreItem) {
	            PluginType pt = PluginType.get(preItem.getPlugin());
	            if (StringUtils.isNoneBlank(pt.getType())) {
	                ds.addChildren(preItem.getLabel(), preItem.getDestField(), pt.getType());
	            } else {
	                List<DataColumnInfo> children = new ArrayList<DataColumnInfo>();
	                JSONArray array = JSONArray.parseArray(pt.getMeta());
	                for (int i = 0; i < array.size(); i++) {
	                    JSONObject obj = array.getJSONObject(i);
	                    children.add(new DataColumnInfo(obj.getString("title"), obj.getString("column"), obj
	                            .getString("type")));
	                }
	                ds.addChildren(preItem.getLabel(), preItem.getDestField(), children);
	            }
	        }
	        list.add(ds);
    	}

        CommonResult result = new CommonResult();
        result.setSuccess(true);
        result.getData().put("list", list);
        return result;
    }

   @PutMapping
    public CommonResult save(@RequestBody AbstractionVO abstraction) {
        return abstractionService.save(abstraction);
    }

   @DeleteMapping
    public CommonResult delete(@RequestBody Long[] id) {
        return abstractionService.delete(id);
    }

}
