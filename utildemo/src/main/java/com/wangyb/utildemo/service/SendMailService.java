package com.wangyb.utildemo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;

/**
 * @author wangyb
 * @Date 2019/1/30 15:08
 * Modified By:
 * Description: 发送电子邮件工具类
 */
@Service
@Slf4j
public class SendMailService {

    private final JavaMailSenderImpl mailSender;

    @Autowired
    public SendMailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 向单个或者多个邮箱发只含Text邮件
     *
     * @param subject  主题
     * @param text     内容
     * @param sender   发送者
     * @param receiver 接收者
     */
    public void sendTextMail(String subject, String text, String sender, String... receiver) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //如果没有填写接收通知信息的邮箱，则不发送
        if (receiver == null || receiver.length < 1) {
            return;
        }
        // 可写入多个接收者
        simpleMailMessage.setTo(receiver);
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
        log.info("通知邮件已发送");
    }

    /**
     * 向多个邮箱发送html格式邮件
     *
     * @param subject   主题
     * @param html      html
     * @param sender    发送者
     * @param receivers 接收者
     * @throws Exception 异常
     */
    public void sendHtmlMails(String subject, String html, String sender, String[] receivers) throws Exception {

        if (receivers == null || receivers.length < 1) {
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(receivers);
        mimeMessageHelper.setFrom(sender);
        mimeMessageHelper.setSubject(subject);

        // 启用html
        mimeMessageHelper.setText(html, true);
        // 发送邮件
        mailSender.send(mimeMessage);

        log.info("邮件已发送");
    }

    /**
     * 发送带excel表格工具类，不生成中间excel文件，直接发
     *
     * @param from     发送者
     * @param to       接收者
     * @param subject  主题
     * @param content  内容
     * @param workbook excel内容
     */
    public void sendAttachmentsMail(String from, String[] to, String subject, String content, HSSFWorkbook workbook, String fileName) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.close();
            DataSource fds = new ByteArrayDataSource(bos.toByteArray(), "application/excel");

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(content, "UTF-8", "html");

            MimeBodyPart mbp2 = new MimeBodyPart();
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(fileName);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);
            message.setContent(mp);

            message.saveChanges();
            mailSender.send(message);
            log.info("带附件的邮件已经发送。");
        } catch (Exception e) {
            log.error("发送带附件的邮件时发生异常！", e.toString());
        }
    }
}
