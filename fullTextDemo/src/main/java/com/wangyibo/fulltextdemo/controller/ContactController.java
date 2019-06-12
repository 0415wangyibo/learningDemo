package com.wangyibo.fulltextdemo.controller;

import com.wangyibo.fulltextdemo.entity.Contact;
import com.wangyibo.fulltextdemo.service.ContactService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangyb
 * @Date 2019/6/12 8:51
 * Modified By:
 * Description:
 */
@RestController
@RequestMapping("contact")
@Api(tags = "联系人智能查询")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/addition")
    @ApiOperation("添加或者修改联系人，若电话相同则视为同一个人")
    public Boolean insertContact(Contact contact) {
        return contactService.insertContact(contact);
    }

    @GetMapping("/name")
    @ApiOperation("根据名字智能查找联系人，中文名")
    public List<Contact> smartSelectContacts(String name) {
        return contactService.smartSelectContacts(name);
    }

    @GetMapping("/position")
    @ApiOperation("根据职业、职位智能查找联系人，参数为中文")
    public List<Contact> smartSelectContactsByPosition(String position) {
        return contactService.smartSelectContactsByPosition(position);
    }
}
