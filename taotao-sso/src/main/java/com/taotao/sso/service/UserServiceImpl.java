package com.taotao.sso.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import com.taotao.common.utils.JsonUtils;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.redis.dao.JedisClient;
import com.taotao.sso.constant.SsoConstant;

/**
 * 用户管理service
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private TbUserMapper userMapper;
	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Autowired
	private JedisClient jedisClient;
	@Value("${USER_SESSION_SAVE_TIME}")
	private Integer USER_SESSION_SAVE_TIME;
	
	@Override
	public TaotaoResult checkData(String param, Integer type) {
		//创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		//判断查询的类型
		if (type == SsoConstant.CHECK_USERNAME) {
			criteria.andUsernameEqualTo(param);
		}else if (type == SsoConstant.CHECK_PHONE) {
			criteria.andPhoneEqualTo(param);
		}else if (type == SsoConstant.CHECK_EMAIL) {
			criteria.andEmailEqualTo(param);
		}
		//查询数据
		List<TbUser> list = userMapper.selectByExample(example);
		return list != null && list.size() > 0 ? TaotaoResult.ok(false) : TaotaoResult.ok(true);
	}

	@Override
	public TaotaoResult register(TbUser user) {
		//补全用户信息
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//对密码进行加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult login(TbUser user) {
		//查询用户信息
		TbUserExample example = new TbUserExample();
		example.createCriteria().andUsernameEqualTo(user.getUsername());
		List<TbUser> list = userMapper.selectByExample(example);
		
		//验证是否登录成功
		if (CollectionUtils.isEmpty(list)) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser loginUser = list.get(0);
		if (!loginUser.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		
		//登录成功，将数据写入redis,此处不用捕获异常，因为保存不入也是登录失败
		//用户信息写入redis前，先清空密码，防止泄密
		loginUser.setPassword(null);
		//使用uuid生成token
		String token = UUID.randomUUID().toString();
		jedisClient.set(USER_SESSION_REDIS_KEY+":"+token, JsonUtils.objectToJson(loginUser));
		//设置过期时间
		jedisClient.expire(USER_SESSION_REDIS_KEY+":"+token, USER_SESSION_SAVE_TIME);
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		//查询redis中的数据
		String result = jedisClient.get(USER_SESSION_REDIS_KEY+":"+token);
		if (StringUtils.isEmpty(result)) {
			return TaotaoResult.build(400, "token失效，请重新登录");
		}
		//重新设置token的失效时间
		jedisClient.expire(USER_SESSION_REDIS_KEY+":"+token, USER_SESSION_SAVE_TIME);
		return TaotaoResult.ok(JsonUtils.jsonToPojo(result, TbUser.class));
	}

	@Override
	public TaotaoResult logout(String token) {
		Long del = jedisClient.del(USER_SESSION_REDIS_KEY+":"+token);
		return TaotaoResult.ok();
	}

}
