package com.cjy.doubleblindserver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cjy.doubleblindserver.dao.UserDao;
import com.cjy.doubleblindserver.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DoubleBlindServerApplicationTests {
    @Autowired
    private UserDao userDao;

    @Test
    void testGetAll() {
        User user = new User();
//        user.setName("cjy");
//        user.setPassword("11111");
//        user.setCard("1212121");
//        user.setRest(5000L);
//        user.setPubkey("1221231313231");
//        userDao.insert(user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name","cjy");
        queryWrapper.eq("password","1233333");
        user = userDao.selectOne(queryWrapper);
        System.out.println(user);
//        user.setRest(user.getRest()-1023);
//        userDao.updateById(user);
//        List<User> users = userDao.selectList(null);
//        System.out.println(users);
    }

}
