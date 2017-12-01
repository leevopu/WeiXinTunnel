package com.weixin.corp.service;

import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.weixin.corp.entity.message.pojo.MpNews;
import com.weixin.corp.utils.WeixinUtil;

public class MediaService {
	
	private static Log log = LogFactory.getLog(MediaService.class);
	/**
	 * 获取永久素材总数
	 */
	private static String MATERIAL_COUNT_GET = "https://qyapi.weixin.qq.com/cgi-bin/material/get_count?access_token=ACCESS_TOKEN";
	/**
	 * 图文素材查询（GET）
	 */
	private static String MPNEWS_GET = "https://qyapi.weixin.qq.com/cgi-bin/material/batchget?access_token=ACCESS_TOKEN";

	
	/**
	 * 查询图文素材列表
	 * 弃用!!!
	 */
	public static List<MpNews> getMpNews() {
		//调用接口获取永久素材总数
		JSONObject countObject = WeixinUtil.httpsRequest(MATERIAL_COUNT_GET,WeixinUtil.GET_REQUEST_METHOD, null);
		JSONObject jsonObject = null;
		int mpcount = countObject.getInt("mpnews_count");
		
		JSONArray array=new JSONArray();
		List<MpNews> mpnewsList = null;
		if(mpcount>0){
			int count = 0;
			int mod = mpcount%50;  //取余
			if(mod==0){
				count = mpcount/50;
			}else{
				count = mpcount/50+1;
			}
			
			for (int j = 0; j < count; j++) {
				JSONObject jsonObject1 = null;
				JSONObject node = new JSONObject();
				int offset = j*50;
			    node.put("type",  "mpnews"); //类型
			    node.put("offset", offset ); //从该类型素材的该偏移位置开始返回，0表示从第一个素材 返回
			    node.put("count",  50); //返回素材的数量，取值在1到50之间
			    // 调用接口获取图文素材列表
			    jsonObject1 = WeixinUtil.httpsRequest(MPNEWS_GET,WeixinUtil.GET_REQUEST_METHOD, node.toString());
			    if (null != jsonObject1) {
					if (0 != jsonObject1.getInt("errcode")) {
						log.error("获取永久图文列表失败 errcode:" + jsonObject.getInt("errcode")
								+ "，errmsg:" + jsonObject.getString("errmsg"));
						return null;
					}
				} else {
					return null;
				}
				
				JSONArray jsonArray = JSONObject.fromObject(jsonObject1).getJSONArray("itemlist");
			    for (int i = 0; i < jsonArray.size(); i++) {
			    	JSONObject obj = (JSONObject) jsonArray.get(i);
			    	obj.put("mediaId", obj.get("media_id"));
			    	obj.put("articles", ((JSONObject) obj.get("content")).get("articles"));
			    	obj.remove("media_id");
			    	obj.remove("content");
			    	obj.remove("update_time");
			    	array.add(obj);
				}
			}
			
		}else{
			log.error("微信端没有永久图文素材！");
			return null;
		}
//		Collection collection = JSONArray.toCollection(array, MpNews.class);
		Collection collection = array.toCollection(array, MpNews.class);
		mpnewsList = (List<MpNews>) collection;
		return mpnewsList;
	}
}
