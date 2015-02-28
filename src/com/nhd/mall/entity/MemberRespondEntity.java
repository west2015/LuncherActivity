package com.nhd.mall.entity;

/**登陆响应
 * Created by linchunwei on 14-4-25.
 */
public class MemberRespondEntity {
    private Member datas;
    private MemberStatus status;
    private String success;
    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Member getDatas() {
        return datas;
    }

    public void setDatas(Member datas) {
        this.datas = datas;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }
}
