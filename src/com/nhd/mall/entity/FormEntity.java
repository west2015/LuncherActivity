package com.nhd.mall.entity;

/**订单实体类
 * Created by Administrator on 14-4-24.
 */
public class FormEntity {
    private Integer id;
    private Member member;
    private String message;//留言
    private double freight;//运费
    private String orderClass;
    private String orderNumber;
    private String status;	
    private double payment;//付款总金额
    private Integer windNum;   //疯豆数量
    private String isWind;//是否使用风豆 使用 1；不使用2
    private String isMoney;//是否使用现金  使用 1；不使用2
    private String getway;//提货方式   自行取货 1 ；快递：2
    private Integer addId;//用户地址Id
    private Integer storeId;//门店id
    private OrderProductEntity[]products;
    private FormStoreEntity orderStores;
    private String state; //订单状态 0、未处理 1、用户已付款 2、商家已收款 3、商家已发货 4、用户已收货 5订单完成
    private AddCustomerAddress address;
    private String crDate;
    private String logisticNo;//运单号
    private LogisticEntity logistic;
    private String orderType;   //类型  1是商品，2是优惠券
    private CouponOrder[]coupons;
    private String orderDescription;
    
	private String code;  //提货码


    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrderClass() {
        return orderClass;
    }

    public void setOrderClass(String orderClass) {
        this.orderClass = orderClass;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public CouponOrder[] getCoupons() {
        return coupons;
    }

    public void setCoupons(CouponOrder[] coupons) {
        this.coupons = coupons;
    }

    public Integer getWindNum() {
        return windNum;
    }
    public void setWindNum(Integer windNum) {
        this.windNum = windNum;
    }
    public LogisticEntity getLogistic() {
        return logistic;
    }
    public void setLogistic(LogisticEntity logistic) {
        this.logistic = logistic;
    }
    public String getLogisticNo() {
        return logisticNo;
    }
    public void setLogisticNo(String logisticNo) {
        this.logisticNo = logisticNo;
    }
    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AddCustomerAddress getAddress() {
        return address;
    }
    public void setAddress(AddCustomerAddress address) {
        this.address = address;
    }
    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FormStoreEntity getOrderStores() {
        return orderStores;
    }

    public void setOrderStores(FormStoreEntity orderStores) {
        this.orderStores = orderStores;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public String getIsWind() {
        return isWind;
    }

    public void setIsWind(String isWind) {
        this.isWind = isWind;
    }

    public String getIsMoney() {
        return isMoney;
    }

    public void setIsMoney(String isMoney) {
        this.isMoney = isMoney;
    }

    public String getGetway() {
        return getway;
    }

    public void setGetway(String getway) {
        this.getway = getway;
    }
    public Integer getAddId() {
        return addId;
    }

    public void setAddId(Integer addId) {
        this.addId = addId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public OrderProductEntity[] getProducts() {
        return products;
    }

    public void setProducts(OrderProductEntity[] products) {
        this.products = products;
    }
}
