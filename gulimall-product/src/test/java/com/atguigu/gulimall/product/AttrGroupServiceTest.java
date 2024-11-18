package com.atguigu.gulimall.product;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.service.AttrGroupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AttrGroupServiceTest {
    @Autowired
    private AttrGroupService attrGroupService;

    @Test
    public void test() {
        Map<String, Object> params = new HashMap<>();
        PageUtils pageUtils = attrGroupService.queryPage(params, 101L);
    }
}
