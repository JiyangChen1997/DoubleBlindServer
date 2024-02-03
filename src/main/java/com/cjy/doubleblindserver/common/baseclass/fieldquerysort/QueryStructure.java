package com.cjy.doubleblindserver.common.baseclass.fieldquerysort;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.QueryCondition;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.SortCondition;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 14:16
 */
@Data
public class QueryStructure<T> {
    private QueryWrapper<T> queryWrapper = new QueryWrapper<>();

    public QueryStructure<T> where(String field, String condition, Object value) {
        switch (condition) {
            case "contain":
                queryWrapper.like(field, value);
                break;
            case "NotContain":
                queryWrapper.notLike(field, value);
                break;
            case ">":
                queryWrapper.gt(field, value);
                break;
            case ">=":
                queryWrapper.ge(field, value);
                break;
            case "<":
                queryWrapper.lt(field, value);
                break;
            case "<=":
                queryWrapper.le(field, value);
                break;
            case "=":
                queryWrapper.eq(field, value);
                break;
            case "<>":
                queryWrapper.ne(field, value);
                break;
            case "in":
                queryWrapper.in(field, value);
                break;
            case "notIn":
                queryWrapper.notIn(field, value);
                break;
            case "or":
                queryWrapper.or();
                break;
            case "isNull":
                queryWrapper.isNull(field);
                break;
            case "inSql":
                queryWrapper.inSql(field, (String) value);
                break;
            case "notInSql":
                queryWrapper.notInSql(field, (String) value);
                break;
            default:
                break;
        }
        return this;
    }

    public QueryStructure<T> wheresByList(List<QueryCondition> queryConditions) {
        if (!CollectionUtils.isEmpty(queryConditions)) {
            queryConditions.forEach(e ->{
                switch (e.getOperator()) {
                    case "contain":
                        queryWrapper.like(e.getField(), e.getValue());
                        break;
                    case "NotContain":
                        queryWrapper.notLike(e.getField(), e.getValue());
                        break;
                    case ">":
                        queryWrapper.gt(e.getField(), e.getValue());
                        break;
                    case ">=":
                        queryWrapper.ge(e.getField(), e.getValue());
                        break;
                    case "<":
                        queryWrapper.lt(e.getField(), e.getValue());
                        break;
                    case "<=":
                        queryWrapper.le(e.getField(), e.getValue());
                        break;
                    case "=":
                        queryWrapper.eq(e.getField(), e.getValue());
                        break;
                    case "<>":
                        queryWrapper.ne(e.getField(), e.getValue());
                        break;
                    case "in":
                        queryWrapper.in(e.getField(), e.getValue());
                        break;
                    case "notIn":
                        queryWrapper.notIn(e.getField(), e.getValue());
                        break;
                    case "or":
                        queryWrapper.or();
                        break;
                    case "isNull":
                        queryWrapper.isNull(e.getField());
                        break;
                    case "inSql":
                        queryWrapper.inSql(e.getField(), (String) e.getValue());
                        break;
                    case "notInSql":
                        queryWrapper.notInSql(e.getField(), (String) e.getValue());
                        break;
                    default:
                        break;
                }
            });
        }
        return this;
    }
    public QueryStructure<T> orderBy(String field, String sortMode) {
        switch (sortMode) {
            case "asc":
                queryWrapper.orderByAsc(field);
                break;
            case "desc":
                queryWrapper.orderByDesc(field);
                break;
            default:
                break;
        }
        return this;
    }

    public QueryStructure<T> orderBysByList(List<SortCondition> sortConditions) {
        if (!CollectionUtils.isEmpty(sortConditions)) {
            sortConditions.forEach(e -> {
                switch (e.getSort()) {
                    case "asc":
                        queryWrapper.orderByAsc(e.getField());
                        break;
                    case "desc":
                        queryWrapper.orderByDesc(e.getField());
                        break;
                    default:
                        queryWrapper.orderByDesc("create_time");
                }
            });
        }else {
            queryWrapper.orderByDesc("create_time");
        }
        return this;
    }
    public QueryWrapper<T> build() { return this.queryWrapper;}
}
