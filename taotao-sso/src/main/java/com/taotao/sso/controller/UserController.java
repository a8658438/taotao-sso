package com.taotao.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.utils.ExceptionUtil;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.constant.SsoConstant;
import com.taotao.sso.service.UserService;

/**
 * 用户controller
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	/**
	 * 校验用户注册参数
	 * @param param
	 * @param type
	 * @param callBack
	 * @return
	 */
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public Object checkData(@PathVariable String param,@PathVariable Integer type,String callBack){
		//验证参数是否正确
		if (SsoConstant.CHECK_EMAIL != type && SsoConstant.CHECK_PHONE != type && SsoConstant.CHECK_USERNAME != type) {
			TaotaoResult result = TaotaoResult.build(400, "type格式不正确，可选参数1、2、3分别代表username、phone、email");
			return packgeResult(callBack,result);
		}
		//查询数据
		return packgeResult(callBack, userService.checkData(param, type));
	}

	/**
	 * 将返回参数封装为jsonp
	 * @param callBack
	 * @param result
	 * @return
	 */
	private Object packgeResult(String callBack, TaotaoResult result) {
		//如果存在callback，跨域jsonp
		if (StringUtils.isNotEmpty(callBack)) {
			MappingJacksonValue jacksonValue = new MappingJacksonValue(result);
			jacksonValue.setJsonpFunction(callBack);
			return jacksonValue;
		}
		return result;
	}
	/**
	 * 用户注册接口
	 * @param callBack
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/register",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult register(TbUser user) {
		try {
			TaotaoResult result = userService.register(user);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	/**
	 * 用户登录接口
	 * @param callBack
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(TbUser user) {
		try {
			TaotaoResult result = userService.login(user);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	/**
	 * 通过token查询用户登录信息
	 * @param callBack
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token,String callBack) {
		try {
			TaotaoResult result = userService.getUserByToken(token);
			return packgeResult(callBack, result);//支持jsonp
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	/**
	 * 通过token退出当前用户
	 * @param callBack
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/logout/{token}")
	@ResponseBody
	public Object logout(@PathVariable String token,String callBack) {
		try {
			TaotaoResult result = userService.logout(token);
			return packgeResult(callBack, result);//支持jsonp
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
}
