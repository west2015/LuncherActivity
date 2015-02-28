package com.nhd.mall.entity;

/**专柜列表实体类
 * Created by Administrator on 14-4-16.
 */
public class CounterListEntity {
    private CounterEntity[]counters;

    public CounterEntity[] getCounters() {
        return counters;
    }

    public void setCounters(CounterEntity[] counters) {
        this.counters = counters;
    }
}
