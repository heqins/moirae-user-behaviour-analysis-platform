package com.admin.server.helper;

import com.admin.server.helper.DorisHelper;
import com.admin.server.model.dto.DbColumnValueDto;
import com.admin.server.service.impl.UserServiceImpl;
import com.admin.server.utils.KeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DorisTest {

    @Resource
    private DorisHelper dorisHelper;

    @Test
    public void testDorisSelectColumnValue() {
        List<DbColumnValueDto> dbColumnValueDtos = dorisHelper.selectColumnValues("user_behaviour_analysis", "event_logd", "event_time");

        System.out.println("test");
    }
}
