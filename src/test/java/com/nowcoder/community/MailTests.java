package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("2586119459@qq.com","陈海土","加油呀");
    }

    //待优化  邮件数不能发太多
    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","你在做啥呀？一位爱你却又不愿意透露姓名的网友");
        String content = templateEngine.process("/mail/demo",context);
        mailClient.sendMail("3524901564@qq.com","来自远方的爱",content);
    }
}
