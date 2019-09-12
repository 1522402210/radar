package com.pgmmers.radar.controller;


import com.pgmmers.radar.dal.bean.ModelQuery;
import com.pgmmers.radar.enums.StatusType;
import com.pgmmers.radar.service.common.CommonResult;
import com.pgmmers.radar.service.model.ModelService;
import com.pgmmers.radar.vo.admin.UserVO;
import com.pgmmers.radar.vo.model.ModelVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/services/v1/model")
public class ModelApiController  {

    public static Logger logger = LoggerFactory.getLogger(ModelApiController.class);

    @Autowired
    private ModelService modelService;


    @GetMapping("/{id}")
    public CommonResult get(@PathVariable Long id) {
        CommonResult result = new CommonResult();
        ModelVO modelVO = modelService.get(id);
        if (modelVO != null) {
            result.setSuccess(true);
            result.getData().put("model", modelVO);
        }
        return result;
    }   

    @GetMapping("/list")
	public CommonResult list(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserVO user = (UserVO) session.getAttribute("user");
        CommonResult result = new CommonResult();
        if (user == null) {
            result.setMsg("session已过期");
            return result;
        }
        result.setSuccess(true);
        result.getData().put("modelList",  modelService.listModel(user.getCode(), null));
        return result;
	}

    @GetMapping("/list/{merchantCode}")
	public CommonResult list(@PathVariable String merchantCode) {
        CommonResult result = new CommonResult();
        result.setSuccess(true);
        result.getData().put("modelList",  modelService.listModel(merchantCode, null));
        return result;
	}

    @PostMapping
    public CommonResult query(@RequestBody ModelQuery query, HttpServletRequest request) {
	    CommonResult result = new CommonResult();

        HttpSession session = request.getSession();
        UserVO user = (UserVO) session.getAttribute("user");
        if (user == null) {
            result.setMsg("session已过期");
            return result;
        }
        query.setMerchantCode(user.getCode());
        return modelService.query(query);
    }


    @PutMapping
    public CommonResult save(@RequestBody ModelVO model, HttpServletRequest request) {
    	HttpSession session = request.getSession();
        UserVO user = (UserVO) session.getAttribute("user");
        model.setCode(user.getCode());
        return modelService.save(model);
    }


    @DeleteMapping
    public CommonResult delete(@RequestBody Long[] id) {
        return modelService.delete(id);
    }

    @PostMapping("/build/{id}")
    public CommonResult build(@PathVariable Long id) {
        CommonResult result = new CommonResult();
        try {
            result = modelService.build(id);
        } catch (Exception e) {
            logger.error("", e);
            result.setMsg("错误:" + e.getMessage());
        }
        return result;
    }


    @PostMapping("/enable/{id}")
    public CommonResult enable(@PathVariable Long id) {
        ModelVO modelVO = modelService.get(id);
        modelVO.setStatus(StatusType.ACTIVE.getKey());
        return modelService.save(modelVO);
    }

    @PostMapping("/disable/{id}")
    public CommonResult disable(@PathVariable Long id) {
        ModelVO modelVO = modelService.get(id);
        modelVO.setStatus(StatusType.INACTIVE.getKey());
        return modelService.save(modelVO);
    }

    @PostMapping("/copy")
	public CommonResult copy(@RequestBody ModelVO model, HttpServletRequest request) {
    	HttpSession session = request.getSession();
        UserVO user = (UserVO) session.getAttribute("user");
        return modelService.copy(model.getId(), user.getCode(), model.getModelName(), model.getLabel());
	}

    @GetMapping("/list/template")
    public CommonResult template() {
        CommonResult result = new CommonResult();
        result.setSuccess(true);
        result.getData().put("modelList",  modelService.listTemplateModel(true));
        return result;
    }
}
