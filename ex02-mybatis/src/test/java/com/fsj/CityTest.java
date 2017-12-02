package com.fsj;

import com.alibaba.fastjson.JSON;
import com.fsj.controller.CityController;
import com.fsj.dao.CityMapper;
import com.fsj.entity.City;
import com.fsj.util.CommonUtil;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class CityTest {
    private MockMvc mvc1;
    private MockMvc mvc2;
    private String url_get_all;

    @Autowired
    CityMapper cityMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mvc1 = MockMvcBuilders.standaloneSetup(new CityController()).build();
        mvc2 = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        url_get_all = "/mybatis/queryAll";
    }

    @Test
    public void testQueryAll() throws Exception {
        List<City> expected = cityMapper.queryAll();
        List<City> actual = cityMapper.queryAll();
        Assert.assertEquals("queryAll测试失败", JSON.toJSONString(expected), JSON.toJSONString(actual));
    }

    /**
     * 验证controller是否正常响应并打印返回结果
     * http://www.ityouknow.com/springboot/2017/05/09/springboot-deploy.html
     * 此处MockMvc对象请求失败，可能只适用于springboot1.3.6旧版本
     * @throws Exception
     */
    @Test
    public void testRequest() throws Exception {
        mvc1.perform(MockMvcRequestBuilders.get(url_get_all).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    /*output
    * org.springframework.web.util.NestedServletException: Request processing failed; nested exception is java.lang.NullPointerException
*/
    }

    @Test
    public void testRequest2() throws Exception {
        MvcResult res = mvc2.perform(MockMvcRequestBuilders.get(url_get_all).accept(MediaType.APPLICATION_JSON))
                .andReturn();
        int status = res.getResponse().getStatus();
        String content = res.getResponse().getContentAsString();
        List<City> expected = cityMapper.queryAll();

        Assert.assertEquals(200, status);
        Assert.assertEquals(JSON.toJSONString(expected), content);//json元素顺序不同，测试不过

        /*json对象比较，在python中自动排序，对象比较为True
         Expected :[{"country":"US","id":"1","name":"San Francisco","state":"CA"},{"country":"CN","id":"2","name":"Beijing","state":"BJ"},{"country":"CN","id":"3","name":"Guangzhou","state":"GD"}]
         Actual   :[{"id":"1","name":"San Francisco","state":"CA","country":"US"},{"id":"2","name":"Beijing","state":"BJ","country":"CN"},{"id":"3","name":"Guangzhou","state":"GD","country":"CN"}]
         * */
    }
}
