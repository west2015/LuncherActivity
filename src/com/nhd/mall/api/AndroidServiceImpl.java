package com.nhd.mall.api;
import com.huake.util.messagedigestutil.MessageDigestUtil;
import com.nhd.mall.entity.AddCarEntity;
import com.nhd.mall.entity.AddCollectEntity;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.BaiduBindingEntity;
import com.nhd.mall.entity.BeanDetailList;
import com.nhd.mall.entity.BranchDetailKeyEntity;
import com.nhd.mall.entity.BranchList;
import com.nhd.mall.entity.CarList;
import com.nhd.mall.entity.CollectionList;
import com.nhd.mall.entity.CommentList;
import com.nhd.mall.entity.CouponDetailEntity;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.CustomerAddressEntityList;
import com.nhd.mall.entity.DoorEntity;
import com.nhd.mall.entity.EmployList;
import com.nhd.mall.entity.EventEntity;
import com.nhd.mall.entity.EventEntityList;
import com.nhd.mall.entity.FeedBackEntity;
import com.nhd.mall.entity.FormDetailEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormList;
import com.nhd.mall.entity.GoodsFilterList;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.PostFeedBackEntity;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.ProductEntity;
import com.nhd.mall.entity.ProductList;
import com.nhd.mall.entity.PublicCommentEntity;
import com.nhd.mall.entity.QuanStatus;
import com.nhd.mall.entity.RegionList;
import com.nhd.mall.entity.RegisterEntity;
import com.nhd.mall.entity.SearchHotEntity;
import com.nhd.mall.entity.Status;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.entity.Supplier;
import com.nhd.mall.entity.Version;
import com.nhd.mall.entity.WuliuEntity;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**********************************************************
 * 内容摘要
 * <p>
 * Server实现 通过Spring-android第三方jar包执行处理http请求 执行请求并通过过反射机制返回实体类 作者 ：wuchenbiao
 * 创建时间 ：012-12-15 上午11:01:49 历史记录 : 日期 : 2012-12-15 上午11:01:49 修改人：wuchenbiao
 * 描述 :
 *********************************************************** 
 */
public class AndroidServiceImpl implements Server {
	/** 存储加密字段的时间格式 */
	private static final String ZONE_DATE_FORMAT = "EEE, yyyy-MM-dd HH:mm:ss zzz";

	/** 存储Http request url */
	private String baseUrl;
	/** 存储Http request的加密钥 */
	private String publicKey;
	/** 存储Http request的加密公钥 */
	private String secureKey;

	public AndroidServiceImpl(String baseUrl, String apiKey, String secret) {
		this.baseUrl = baseUrl;
		this.publicKey = apiKey;
		this.secureKey = secret;
	}

	/** http请求头 */
	private HttpHeaders requestHeaders;
	/** http请求头的媒体类型 */
	private List<MediaType> acceptableMediaTypes;
	/** http请求实体 */
	private HttpEntity<?> requestEntity;
	/** http请求模板 用于执行http请求 */
	private RestTemplate restTemplate;

	private void getModel() {
		if (requestHeaders == null) {
			requestHeaders = new HttpHeaders();
		}

		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		acceptableMediaTypes.add(new MediaType("application", "JSON", Charset
				.forName("UTF-8")));

		// 设置请求头的协议支持
		requestHeaders.setAccept(acceptableMediaTypes);

		// add url Signature of OAuth.
		String date = getDateStringWithZone(Calendar.getInstance(),
				ZONE_DATE_FORMAT, TimeZone.getTimeZone("UTC"), Locale.US);
		requestHeaders.set("Date", date);
		requestEntity = new HttpEntity<Object>(requestHeaders);

		if (restTemplate == null) {
			restTemplate = new RestTemplate();

			// Add the Jackson message converter
			MappingJacksonHttpMessageConverter stringHttpMessageConverternew = new MappingJacksonHttpMessageConverter();
			ObjectMapper newObjectMapper = new ObjectMapper();

			// 忽略不被自身识别的json节点
			newObjectMapper
					.configure(
							org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
							false);
			stringHttpMessageConverternew.setObjectMapper(newObjectMapper);

			List<HttpMessageConverter<?>> list = restTemplate
					.getMessageConverters();

			list.add(stringHttpMessageConverternew);

			HttpMessageConverter<?> formHttpMessageConverter = new FormHttpMessageConverter();

			list.add(formHttpMessageConverter);

			restTemplate.setMessageConverters(list);
		}
	}

	private String getDateStringWithZone(Calendar cal, String format,
			TimeZone timeZone, Locale locale) {
		if (cal == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
		sdf.setTimeZone(timeZone);
		return sdf.format(cal.getTime());
	}

	private String buildURL(HttpMethod httpVerb, HttpHeaders requestHeaders,
			String path, Object... urlVariables) {
		String originPath = path;
		if (urlVariables != null) {
			/** URI模板 */
			UriTemplate uriTemplate = new UriTemplate(path);

			URI expanded = uriTemplate.expand(urlVariables);
			path = expanded.getRawPath();
		}
		String httpMethod = httpVerb.toString();
		String date = requestHeaders.getFirst("Date");
		String signature = MessageDigestUtil.calculateSignature(httpMethod,
                date, path, this.secureKey);
		try {
			StringBuilder s = new StringBuilder();
			s.append(originPath);
			s.append("&apiKey=");
			s.append(URLEncoder.encode(this.publicKey, "UTF-8"));
			s.append("&signature=");
			s.append(URLEncoder.encode(signature, "UTF-8"));

			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(
					"UnsupportedEncodingException when URLEncoder.encode", ex);
		}
	}

	/**
	 * MD5加密 会员注册 登录的时候使用
	 * 
	 * @param s
	 *            加密参数
	 * @return 加密后的参数
	 */
	public String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法�?MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘�?
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public synchronized Version getCheckUpdates(String category) throws IOException,RuntimeException {
        this.getModel();
        final String url = baseUrl+"/api/versions/now/{category}?";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url, category);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Version.class,category).getBody();
	}
   //获取首页数据接口
    @Override
    public synchronized  MainEntity getMainEntity(Integer storeId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl+"/api/v1/index/show?storeId={storeId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,storeId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,MainEntity.class,storeId).getBody();
    }
   //获取门店列表
    @Override
    public synchronized StoreListEntity getStoreList(int pageSize) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl+"/api/v1/store/list?pageSize={pageSize}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,pageSize);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,StoreListEntity.class,pageSize).getBody();
    }
   //获取商品详情
    @Override
    public synchronized ProductEntity getProductDetail(Integer productId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl+"/api/v1/product/detail?productId={productId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,productId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,ProductEntity.class,productId).getBody();
    }
    //商品提交到购物车
    @Override
    public synchronized  Object postAddToCar(AddCarEntity entity) throws IOException, RuntimeException {
        this.getModel();
		final String url = baseUrl + "/api/v1/car/add?";
		String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
		HttpEntity<AddCarEntity> httpEntity = new HttpEntity<AddCarEntity>(entity, requestHeaders);
		return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    @Override
    public synchronized Object postAddCustomerAddress(AddCustomerAddress address) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/address/add?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<AddCustomerAddress> httpEntity = new HttpEntity<AddCustomerAddress>(address, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    @Override
    public synchronized CustomerAddressEntityList getAddressList(Long memberId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl+"/api/v1/address/list?memberId={memberId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CustomerAddressEntityList.class,memberId).getBody();
    }
    @Override
    public synchronized Object getDeleteAddress(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/address/del?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,id).getBody();
    }

    @Override
    public synchronized Object postModifyAddress(CustomerAddressEntity entity) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/address/update?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<CustomerAddressEntity> httpEntity = new HttpEntity<CustomerAddressEntity>(entity, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    @Override
    public synchronized CommentList getCommentList(Integer productId,Integer pageNum) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/comment/list?productId={productId}&pageNum={pageNum}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,productId,pageNum);
        System.out.println(restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CommentList.class,productId,pageNum).getBody());
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CommentList.class,productId,pageNum).getBody();
    }
    @Override
    public synchronized Object postPublicComment(PublicCommentEntity entity) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/comment/add?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<PublicCommentEntity> httpEntity = new HttpEntity<PublicCommentEntity>(entity, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    //搜索商品列表
    @Override
    public synchronized ProductList getProductList(Integer pageNum, Integer storeId, String search_for, Integer categoryId,
    Integer brandId, Integer counterId, String brandName, String name, String orderBy, Integer tagId, Integer themeId) throws IOException, RuntimeException {
        this.getModel();
        if(search_for.equals("tag")){
            final String url = baseUrl+"/api/v1/product/list?pageNum={pageNum}&storeId={storeId}&search_for={search_for}&tagId={tagId}&orderBy={orderBy}";
            String signPath = buildURL(HttpMethod.GET, requestHeaders, url,pageNum,storeId,search_for,tagId,orderBy);
            return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,ProductList.class,pageNum,storeId,search_for,tagId,orderBy).getBody();
        }
        else if(search_for.equals("theme")){
            final String url = baseUrl+"/api/v1/product/list?pageNum={pageNum}&storeId={storeId}&search_for={search_for}&themeId={themeId}&orderBy={orderBy}";
            String signPath = buildURL(HttpMethod.GET, requestHeaders, url,pageNum,storeId,search_for,themeId,orderBy);
            return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,ProductList.class,pageNum,storeId,search_for,themeId,orderBy).getBody();
        }
        else if(search_for.equals("normal")){
            final String url = baseUrl+"/api/v1/product/list?pageNum={pageNum}&storeId={storeId}&search_for={search_for}&categoryId={categoryId}&orderBy={orderBy}";
            String signPath = buildURL(HttpMethod.GET, requestHeaders, url,pageNum,storeId,search_for,categoryId,orderBy);
            return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,ProductList.class,pageNum,storeId,search_for,categoryId,orderBy).getBody();
        }
        else{
            return null;
        }
    }

    @Override
    public synchronized CarList getCarList(Long memberId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/car/list?memberId={memberId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,memberId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CarList.class,memberId).getBody();
    }

    @Override
    public synchronized Object addCollectionPost(AddCollectEntity entity) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/collection/add?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<AddCollectEntity> httpEntity = new HttpEntity<AddCollectEntity>(entity, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }

    @Override
    public synchronized CollectionList getCollectionList(Integer pageNum, Long memberId, Integer type) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/collection/list?pageNum={pageNum}&memberId={memberId}&type={type}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,pageNum,memberId,type);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CollectionList.class,pageNum,memberId,type).getBody();
    }
    @Override
    public synchronized GoodsFilterList getGoodsFilter() throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/category/list?";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,GoodsFilterList.class).getBody();
}
    //获取会员优惠券列表
    @Override
    public synchronized CouponListEntity getCouponList(Long memberId, Integer status, Integer pageNum,Integer type) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/coupon/memberlist?memberId={memberId}&status={status}&pageNum={pageNum}&type={type}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,memberId,status,pageNum,type);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CouponListEntity.class,memberId,status,pageNum,type).getBody();
    }
    //获取门店优惠券列表
    @Override
    public synchronized CouponListEntity getCouponList(Long storeId, Integer pageNum) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/coupon/list?storeId={storeId}&pageNum={pageNum}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,storeId,pageNum);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CouponListEntity.class,storeId,pageNum).getBody();
    }
    //提交百度推送相关信息
    @Override
    public synchronized Status postBaiDuPushMsg(BaiduBindingEntity baiduBindingMsg)	throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/push/addMessage?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<BaiduBindingEntity> httpEntity = new HttpEntity<BaiduBindingEntity>(baiduBindingMsg, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Status.class).getBody();
    }
    //删除收藏的商品
    @Override
    public synchronized Object deleteCollectionGet(Integer[] ids) throws IOException, RuntimeException {
        this.getModel();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                sb.append(String.valueOf(ids[i]));
            }
            else{
                sb.append(String.valueOf(ids[i])+",");
            }
        }
        final String url = baseUrl + "/api/v1/collection/del?ids="+sb.toString();
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class).getBody();
    }
    //删除购物车订单
    @Override
    public synchronized Object deleteCarGet(Integer[] ids) throws IOException, RuntimeException {
        this.getModel();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                sb.append(String.valueOf(ids[i]));
            }
            else{
                sb.append(String.valueOf(ids[i])+",");
            }
        }
        final String url = baseUrl + "/api/v1/car/del?ids="+sb.toString();
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class).getBody();
    }
    //领取优惠券
    @Override
    public synchronized QuanStatus getCoupon(Long memberId, Integer couponId,Integer count) throws IOException,RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/coupon/fetch?memberId={memberId}&couponId={couponId}&count={count}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,memberId,couponId,count);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,QuanStatus.class,memberId,couponId,count).getBody();
    }
    //使用优惠券
    @Override
    public synchronized Object getCouponUse(Integer couponId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/coupon/use?couponId={couponId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,couponId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,couponId).getBody();
    }
    //删除优惠券
    @Override
    public synchronized Object getCouponDelete(Integer[] ids) throws IOException, RuntimeException {
        this.getModel();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                sb.append(String.valueOf(ids[i]));
            }
            else{
                sb.append(String.valueOf(ids[i])+",");
            }
        }
        final String url = baseUrl + "/api/v1/coupon/del?ids="+sb.toString();
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class).getBody();
    }

    //提交订单接口(单件商品的接口)
    @Override
    public synchronized Object postOrderForm(FormEntity form) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/add?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<FormEntity> httpEntity = new HttpEntity<FormEntity>(form, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    //提交订单接口(购物车那边的)
    @Override
    public synchronized Object postOrderCarForm(FormList form) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/addOrderList?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<FormList> httpEntity = new HttpEntity<FormList>(form, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    @Override
    public synchronized FormList getFormList(Long memberId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/list?memberId={memberId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url,memberId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,FormList.class,memberId).getBody();
    }
   //订单支付成功后调用这个接口
    @Override
    public synchronized Object getFormPay(Integer id,String returnCode,String out_trade_no,double total_fee) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/paySuccess?id={id}&returnCode={returnCode}&out_trade_no={out_trade_no}&total_fee={total_fee}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id,returnCode,out_trade_no,total_fee);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,id,returnCode,out_trade_no,total_fee).getBody();
    }
   //确认收货后调用这个接口
    @Override
    public synchronized Object getFormSure(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/orderBack?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,id).getBody();
    }

    @Override
    public synchronized FormDetailEntity getFormDetail(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/detail?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,FormDetailEntity.class,id).getBody();
    }
    @Override
    public synchronized Object deleteFormGet(Integer[] ids) throws IOException, RuntimeException {
        this.getModel();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                sb.append(String.valueOf(ids[i]));
            }
            else{
                sb.append(String.valueOf(ids[i])+",");
            }
        }
        final String url = baseUrl + "/api/v1/order/delOrders?ids="+sb.toString();
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class).getBody();
    }
    //登陆接口
    @Override
    public synchronized MemberRespondEntity postLogin(Member member) throws IOException, RuntimeException {
        getModel();
        final String url = baseUrl + "/api/v1/member/login?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<Member> httpEntity = new HttpEntity<Member>(member,requestHeaders);

        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,
                MemberRespondEntity.class).getBody();
    }
    @Override
    public MemberRespondEntity postBeanLogin(Member member) throws IOException, RuntimeException {
        getModel();
        final String url = baseUrl + "/api/v1/wind/login?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<Member> httpEntity = new HttpEntity<Member>(member,requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,
                MemberRespondEntity.class).getBody();
    }

    //修改用户资料
    @Override
    public synchronized MemberRespondEntity postMemberModify(Member member) throws IOException, RuntimeException {
        getModel();
        final String url = baseUrl + "/api/v1/member/update?";

        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<Member> httpEntity = new HttpEntity<Member>(member,requestHeaders);

        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,
                MemberRespondEntity.class).getBody();
    }

    // 上传头像
    @Override
    public synchronized Member postUploadAvatar(Long id, File source)throws IOException, RuntimeException {
        this.getModel();

        final String url = baseUrl + "/api/v1/member/{id}/avatar/upload?";

        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        String signPath = buildURL(HttpMethod.POST, requestHeaders, url, id);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();

        parts.add("source", new FileSystemResource(source));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                parts, requestHeaders);

        return restTemplate.exchange(signPath, HttpMethod.POST, requestEntity,Member.class, id).getBody();
    }
    //注册接口
    @Override
    public synchronized Object postRegister(RegisterEntity entity) throws IOException, RuntimeException {
        this.getModel();

        final String url = baseUrl + "/api/v1/member/register?";

//        if(member.getId()==null)
//            member.setPassword(MD5(member.getPassword()));

        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);

        HttpEntity<RegisterEntity> httpEntity = new HttpEntity<RegisterEntity>(entity, requestHeaders);

        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,
                Object.class).getBody();
    }
    //注销时删除设备绑定
    @Override
    public synchronized Object postDelDeviceInfo(Long memberId) throws IOException, RuntimeException {
        this.getModel();

        final String url = baseUrl + "/api/push/delDeviceInfo?memberId={memberId}";

        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId);

        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,memberId).getBody();
    }
     //获取热门搜索关键字
    @Override
    public synchronized SearchHotEntity getSearchHotTags() throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/product/hotTags?";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,SearchHotEntity.class).getBody();
    }
  //获取物流信息
    @Override
    public synchronized WuliuEntity getWuliu(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/getLogistic?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,WuliuEntity.class,id).getBody();
    }
   //提交意见反馈
    @Override
    public synchronized Object postFeedBack(PostFeedBackEntity messages) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/member/feedback?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<PostFeedBackEntity> httpEntity = new HttpEntity<PostFeedBackEntity>(messages, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity,Object.class).getBody();
    }
    //查询意见反馈列表
    @Override
    public synchronized FeedBackEntity[] GetFeedBackList(Long memberId,Integer pageNum) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/member/memberMessages?memberId={memberId}&pageNum={pageNum}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId,pageNum);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,FeedBackEntity[].class,memberId,pageNum).getBody();
    }
    //判断是否已经收藏了
    @Override
    public synchronized Object iscollectGet(Long memberId, Integer productId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/collection/checkCollect?memberId={memberId}&productId={productId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId,productId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,memberId,productId).getBody();
    }
    //删除我的订单详情里面的商品列表
    @Override
    public synchronized Object getFormDetailDelete(Integer[] ids) throws IOException, RuntimeException {
        this.getModel();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                sb.append(String.valueOf(ids[i]));
            }
            else{
                sb.append(String.valueOf(ids[i])+",");
            }
        }
        final String url = baseUrl + "/api/v1/order/delProducts?ids="+sb.toString();
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class).getBody();
    }
   //获取总店详情
    @Override
    public synchronized StoreEntity getFinalStore(Integer storeId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/store/detail?storeId={storeId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url, storeId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,StoreEntity.class,storeId).getBody();
    }
    //获取楼导航
    @Override
    public synchronized DoorEntity[] getDoorEntity(Integer storeId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/store/findFloors?storeId={storeId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url, storeId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,DoorEntity[].class,storeId).getBody();
    }
   //修改密码
    @Override
    public synchronized Object modifyPswPost(RegisterEntity entity) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/member/pwd?";
      //  if(member.getId()==null) member.setPassword(MD5(member.getPassword()));
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<RegisterEntity> httpEntity = new HttpEntity<RegisterEntity>(entity, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity, Object.class).getBody();
    }
    @Override
    public synchronized Object getBeanPayUrl(Long memberId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/wind/add?memberId={memberId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,memberId).getBody();
}
    //查询用户疯豆数
    @Override
    public  synchronized Object getMemberBean(Long memberId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/wind/find?memberId={memberId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,memberId).getBody();
    }
    //查询用户疯豆使用详情
    @Override
    public synchronized BeanDetailList getbBeanDetail(Long memberId, Integer pageNum) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/wind/list?memberId={memberId}&pageNum={pageNum}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,memberId,pageNum);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,BeanDetailList.class,memberId,pageNum).getBody();
    }
   //删除疯豆明细
    @Override
    public synchronized Object getBeanDelete(Integer[] ids) throws IOException, RuntimeException {
        this.getModel();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ids.length;i++){
            if(i==ids.length-1){
                sb.append(String.valueOf(ids[i]));
            }
            else{
                sb.append(String.valueOf(ids[i])+",");
            }
        }
        final String url = baseUrl + "/api/v1/wind/del?ids="+sb.toString();
        String signPath = buildURL(HttpMethod.GET, requestHeaders,url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class).getBody();
    }
    //搜索返回的结果
    @Override
    public synchronized ProductDetailEntity[] getSearchProducts(String query, String type, Integer first, Integer max) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/product/findProducts?query={query}&type={type}&first={first}&max={max}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,query,type,first,max);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,ProductDetailEntity[].class,query,type,first,max).getBody();
    }
    //发送短信验证码
    @Override
    public synchronized Object getSendMt(String phone) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/member/sendMt?phone={phone}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,phone);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,phone).getBody();
    }

    @Override
    public synchronized Object postSupplier(Supplier supplier) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/supplier/add?";
        String signPath = buildURL(HttpMethod.POST, requestHeaders, url);
        HttpEntity<Supplier> httpEntity = new HttpEntity<Supplier>(supplier, requestHeaders);
        return restTemplate.exchange(signPath, HttpMethod.POST, httpEntity, Object.class).getBody();
    }
    @Override
    public synchronized EmployList getEmploy() throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/advertise/list?";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,EmployList.class).getBody();
    }
   //获取省市区的接口
    @Override
    public synchronized RegionList getRegion(Integer parentId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/region/findRegion?parentId={parentId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,parentId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,RegionList.class,parentId).getBody();
    }

    @Override
    public synchronized Object getThemePic(Integer themeId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/theme/detail?themeId={themeId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,themeId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,Object.class,themeId).getBody();
    }
   //获取首页点击进入查看优惠券
    @Override
    public synchronized  CouponListEntity getBuyQuan(Integer storeId,Integer pageNum,Integer type,boolean flag) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/coupon/list?storeId={storeId}&pageNum={pageNum}&type={type}&flag={flag}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,storeId,pageNum,type,flag);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CouponListEntity.class,storeId,pageNum,type,flag).getBody();
    }

    @Override
    public synchronized CouponDetailEntity getCouponDetail(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/coupon//detail?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,CouponDetailEntity.class,id).getBody();
    }

    @Override
    public synchronized BranchDetailKeyEntity getBranchDetail(Integer brandId,Integer floorId) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/brand/detail?brandId={brandId}&floorId={floorId}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,brandId,floorId);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,BranchDetailKeyEntity.class,brandId,floorId).getBody();
    }
   //获取券订单详细
    @Override
    public synchronized  FormDetailEntity getCouponFormDetail(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/order/couponDetail?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,FormDetailEntity.class,id).getBody();
    }

    @Override
    public synchronized EventEntityList getEventProducts(Integer id) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/activity/detail?id={id}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,id);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,EventEntityList.class,id).getBody();
    }
    //获取或者查询品牌列表
    @Override
    public synchronized BranchList getBranch(Integer storeId, Integer floorId, Integer categoryId, Integer pageNum, String name) throws IOException, RuntimeException {
        this.getModel();
        final String url = baseUrl + "/api/v1/brand/list?storeId={storeId}&floorId={floorId}&categoryId={categoryId}&pageNum={pageNum}&name={name}";
        String signPath = buildURL(HttpMethod.GET, requestHeaders, url,storeId,floorId,categoryId,pageNum,name);
        return restTemplate.exchange(signPath, HttpMethod.GET, requestEntity,BranchList.class,storeId,floorId,categoryId,pageNum,name).getBody();
    }
}
