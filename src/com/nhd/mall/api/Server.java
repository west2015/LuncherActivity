package com.nhd.mall.api;

import com.nhd.mall.activity.CouponDetailActivity;
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

import java.io.File;
import java.io.IOException;

/************************************************************
 * 内容摘要 ：
 * <p>
 * http请求
 * 
 * 作者 ：vendor 创建时间 ：2013-1-16 下午10:47:03
 * 
 * 历史记录 : 日期 : 2013-1-16 下午10:47:03 修改人：vendor 描述 :
 ************************************************************/
public interface Server {
	// 查看版本
    public Version getCheckUpdates(String category) throws IOException,RuntimeException;
    // 查询首页数据
    public MainEntity getMainEntity(Integer storeId) throws IOException,RuntimeException;
    // 查询门店列表
    public StoreListEntity getStoreList(int pageSize) throws IOException,RuntimeException;
    // 查询商品详情
    public ProductEntity getProductDetail(Integer productId) throws IOException,RuntimeException;
    //将物品提交至购物车
    public Object postAddToCar(AddCarEntity entity) throws IOException,RuntimeException;
    //添加用户收货地址
    public Object postAddCustomerAddress(AddCustomerAddress address) throws IOException,RuntimeException;
    //获取用户收货地址列表
    public CustomerAddressEntityList getAddressList(Long memberId) throws IOException,RuntimeException;
    //删除某个收货地址
    public Object getDeleteAddress(Integer id) throws IOException,RuntimeException;
    //修改某个收货地址
    public Object postModifyAddress(CustomerAddressEntity entity) throws IOException,RuntimeException;
    //查询评论列表
    public CommentList getCommentList(Integer productId,Integer pageNum) throws IOException,RuntimeException;
    //提交个人对商品的评论
    public Object postPublicComment(PublicCommentEntity entity) throws IOException,RuntimeException;
    //提交个人对商品的评论
    public ProductList getProductList(Integer pageNum,Integer storeId,String search_for,Integer categoryId,Integer brandId,Integer counterId,String brandName,String name,String orderBy,Integer tagId,Integer themeId) throws IOException,RuntimeException;
    //查询购物车物品
    public CarList getCarList(Long memberId) throws IOException,RuntimeException;
    //商品或品牌添加到收藏
    public Object addCollectionPost(AddCollectEntity entity) throws IOException,RuntimeException;
    //收藏商品列表 type 1 品牌 2 商品
    public CollectionList getCollectionList(Integer pageNum,Long memberId,Integer type) throws IOException,RuntimeException;
    //获取商品多级分类
    public GoodsFilterList getGoodsFilter() throws IOException,RuntimeException;
    //获取会员优惠券列表
    public CouponListEntity getCouponList(Long memberId,Integer status,Integer pageNum,Integer type) throws IOException,RuntimeException;
    //获取门店优惠券列表
    public CouponListEntity getCouponList(Long storeId,Integer pageNum) throws IOException,RuntimeException;
    //领取优惠券
    public QuanStatus getCoupon(Long memberId,Integer couponId,Integer count) throws IOException,RuntimeException;
    //使用优惠券
    public Object getCouponUse(Integer couponId) throws IOException,RuntimeException;
    //删除优惠券
    public Object getCouponDelete(Integer[] ids) throws IOException,RuntimeException;
    //提交百度推送相关信息
    public Status postBaiDuPushMsg(BaiduBindingEntity baiduBindingMsg) throws IOException, RuntimeException;
    //删除收藏的商品或品牌
    public Object deleteCollectionGet(Integer[]ids) throws IOException, RuntimeException;
    //删除购物车订单
    public Object deleteCarGet(Integer[]ids) throws IOException, RuntimeException;
    //提交订单接口（直接购买）
    public Object postOrderForm(FormEntity form) throws IOException, RuntimeException;
    //提交订单接口（购物车那边的）
    public Object postOrderCarForm(FormList form) throws IOException, RuntimeException;
    //订单列表显示
    public FormList getFormList(Long memberId) throws IOException, RuntimeException;
    //订单支付成功调用接口
    public Object getFormPay(Integer id,String returnCode,String out_trade_no,double total_fee ) throws IOException, RuntimeException;
    //订单确认收货调用接口
    public Object getFormSure(Integer id) throws IOException, RuntimeException;
    //订单确认收货调用接口
    public FormDetailEntity getFormDetail(Integer id) throws IOException, RuntimeException;
    //订单删除
    public Object deleteFormGet(Integer[]ids) throws IOException, RuntimeException;
    //登陆接口
    public MemberRespondEntity postLogin(Member member) throws IOException, RuntimeException;
    //疯豆用户登录接口
    public MemberRespondEntity postBeanLogin(Member member) throws IOException, RuntimeException;
    //查询用户风斗数量
    public Object getMemberBean(Long memberId)throws IOException, RuntimeException;
    //查询用户疯豆使用详情
    public BeanDetailList getbBeanDetail(Long memberId,Integer pageNum)throws IOException, RuntimeException;
    //删除用户疯豆详情
    public Object getBeanDelete(Integer[]ids)throws IOException, RuntimeException;
    //搜索返回的商品
    public ProductDetailEntity[] getSearchProducts(String query,String type,Integer first,Integer max)throws IOException, RuntimeException;
    public MemberRespondEntity postMemberModify(Member member)throws IOException, RuntimeException;
    //上传头像
    public Member postUploadAvatar(Long id, File source)throws IOException, RuntimeException;
    //注册
    public Object postRegister(RegisterEntity entity)throws IOException, RuntimeException;
    //注销时删除设备绑定
    public Object postDelDeviceInfo(Long memberId)throws IOException, RuntimeException;
   //获取热门搜索关键字
    public SearchHotEntity getSearchHotTags()throws IOException, RuntimeException;
    //获取物流信息
    public WuliuEntity getWuliu(Integer id)throws IOException, RuntimeException;
    //发表意见反馈
    public Object postFeedBack(PostFeedBackEntity messages)throws IOException, RuntimeException;
    //查询意见反馈列表
    public FeedBackEntity[] GetFeedBackList(Long memberId,Integer pageNum)throws IOException, RuntimeException;
    //判断是否已经收藏了
    public Object iscollectGet(Long memberId,Integer productId)throws IOException, RuntimeException;
    //删除我的订单详情里面的商品列表
    public Object getFormDetailDelete(Integer[]ids)throws IOException, RuntimeException;
    //获取总店实体类详情
    public StoreEntity getFinalStore(Integer storeId)throws IOException, RuntimeException;
    //获取门店楼层导航
    public DoorEntity[] getDoorEntity(Integer storeId)throws IOException, RuntimeException;
    //修改密码
    public Object modifyPswPost(RegisterEntity entity)throws IOException, RuntimeException;
    //疯豆充值接口
    public Object getBeanPayUrl(Long memberId)throws IOException, RuntimeException;
    //获取短信验证码
    public Object getSendMt(String phone)throws IOException, RuntimeException;
    //供应商入驻
    public Object postSupplier(Supplier supplier)throws IOException, RuntimeException;
    //获取招聘信息列表
    public EmployList getEmploy()throws IOException, RuntimeException;
    //获取省市区的接口
    public RegionList getRegion(Integer parentId)throws IOException, RuntimeException;
    //首页主题是否有图片中转
    public Object getThemePic(Integer themeId)throws IOException, RuntimeException;
    //获取首页需要购买的优惠券列表
    public CouponListEntity getBuyQuan(Integer storeId,Integer pageNum,Integer type,boolean flag)throws IOException, RuntimeException;
    //获取首页需要购买的优惠券列表
    public CouponDetailEntity getCouponDetail(Integer id)throws IOException, RuntimeException;
    //根据品牌id查询品牌详细
    public BranchDetailKeyEntity getBranchDetail(Integer brandId,Integer floorId)throws IOException, RuntimeException;
    //根据订单id查询券订单详细
    public FormDetailEntity getCouponFormDetail(Integer id )throws IOException, RuntimeException;
    //根据订单id查询券订单详细
    public EventEntityList getEventProducts(Integer id )throws IOException, RuntimeException;
    //获取或者查询品牌列表
    public BranchList getBranch(Integer storeId,Integer floorId,Integer catetoryId,Integer pageNum,String name )throws IOException, RuntimeException;
}
