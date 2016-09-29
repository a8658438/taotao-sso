package com.taotao.sso.service;

import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbUser;

/**
 * 用户管理service
 * @author Administrator
 *
 */
public interface UserService {
	/**
	 * 用户数据校验
	 * @param param
	 * @param type
	 * @return
	 */
	TaotaoResult checkData(String param,Integer type);
	/**
	 * 用户注册接口
	 * @param user
	 * @return
	 */
	TaotaoResult register(TbUser user);
	/**
	 * 用户注册接口
	 * @param user
	 * @return
	 */
	TaotaoResult login(TbUser user);
	/**
	 * 通过token查询用户信息
	 * @param user
	 * @return
	 */
	TaotaoResult getUserByToken(String token);
	/**
	 * 通过token对用户进行退出操作
	 * @param user
	 * @return
	 */
	TaotaoResult logout(String token);
}
