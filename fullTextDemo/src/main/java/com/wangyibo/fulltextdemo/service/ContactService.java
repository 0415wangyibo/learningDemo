package com.wangyibo.fulltextdemo.service;

import com.wangyibo.fulltextdemo.entity.Contact;
import com.wangyibo.fulltextdemo.mapper.ContactMapper;
import com.wangyibo.fulltextdemo.util.NameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangyb
 * @Date 2019/6/11 17:13
 * Modified By:
 * Description:
 */
@Service
public class ContactService {

    private final ContactMapper contactMapper;

    @Autowired
    public ContactService(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    @Transactional(readOnly = true)
    public Contact selectContactsByContact(String contact) {
        return contactMapper.selectContactsByContact(contact);
    }

    /**
     * 添加或者更新联系人
     */
    public Boolean insertContact(Contact contact) {
        //参数校验，不合法则不进行操作
        if (null == contact || StringUtils.isEmpty(contact.getName())
                || StringUtils.isEmpty(contact.getContact())) {
            return false;
        }
        Contact newContact = new Contact(contact.getName(), contact.getSex(), contact.getContact(), contact.getChPosition());
        Contact oldContact = this.selectContactsByContact(contact.getContact());
        if (null != oldContact) {
            newContact.setCreateTime(oldContact.getCreateTime());
            newContact.setId(oldContact.getId());
            contactMapper.updateByPrimaryKey(newContact);
        } else {
            newContact.setCreateTime(new Date());
            contactMapper.insert(newContact);
        }
        return true;
    }

    private List<Contact> selectContactsByName(String lastName, String firstName, String abbrName, String fullName,
                                               String toneLastName,String toneFirstName,String toneFullName) {
        return contactMapper.selectContactsByCondition(lastName, firstName, abbrName, fullName,toneLastName,toneFirstName,toneFullName);
    }

    private List<Contact> changeSortByLength(List<Contact> contactsList, Integer length) {
        if (null != contactsList && contactsList.size() != 0) {
            Integer contactsSize = contactsList.size();
            Boolean needSort = true;
            for (int i = 0; (i < contactsSize - 1) && needSort; i++) {
                needSort = false;
                for (int j = 0; j < contactsSize - 1 - i; j++) {
                    Contact contactOne = contactsList.get(j);
                    Contact contactTwo = contactsList.get(j + 1);
                    //如果两个权值相差小于0.1，则进行排序
                    if (Math.abs(contactOne.getRelevance() - contactTwo.getRelevance()) < 0.1) {
                        //让笔画相差较小的排在前面
                        if (Math.abs(NameUtil.getStrokeCount(contactOne.getName()) - length) > Math.abs(NameUtil.getStrokeCount(contactTwo.getName()) - length)) {
                            contactsList.set(j, contactTwo);
                            contactsList.set(j + 1, contactOne);
                            //如果一轮中有元素改动，则下一轮继续比较排序，否则说明已经排好序
                            needSort = true;
                        }
                    }
                }
            }
        }
        return contactsList;
    }

    private List<Contact> selectContactsByPosition(String lastName, String position, String fullPosition,
                                                   String positionAbbr,String toneLastName,String toneNewPositionPinyin,
                                                   String toneFullPosition) {
        return contactMapper.selectContactsByPosition(lastName, position, fullPosition, positionAbbr,toneLastName,
                toneNewPositionPinyin, toneFullPosition);
    }

    private List<Contact> changeSortByPositionLength(List<Contact> contactsList, Integer length) {
        if (null != contactsList && contactsList.size() != 0) {
            Integer contactsSize = contactsList.size();
            Boolean needSort = true;
            for (int i = 0; (i < contactsSize - 1) && needSort; i++) {
                needSort = false;
                for (int j = 0; j < contactsSize - 1 - i; j++) {
                    Contact contactOne = contactsList.get(j);
                    Contact contactTwo = contactsList.get(j + 1);
                    //如果两个权值相差小于4，则进行排序
                    if (Math.abs(contactOne.getRelevance() - contactTwo.getRelevance()) < 1) {
                        //让长度相差较小的排在前面
                        if (Math.abs(contactOne.getPosition().length() + contactOne.getLastName().length() - length) >
                                Math.abs(contactTwo.getPosition().length() + contactTwo.getLastName().length() - length)) {
                            contactsList.set(j, contactTwo);
                            contactsList.set(j + 1, contactOne);
                            //如果一轮中有元素改动，则下一轮继续比较排序，否则说明已经排好序
                            needSort = true;
                        }
                    }
                }
            }
        }
        return contactsList;
    }

    /**
     * 智能查找联系人
     *
     * @param name 关键字
     */
    public List<Contact> smartSelectContacts(String name) {
        if (StringUtils.isEmpty(name)) {
            return new ArrayList<>();
        }
        //去除所有空格
        name = name.replaceAll(" ", "");
        //获得姓
        String lastHanZiName = NameUtil.getLastName(name);
        //获得姓无音调拼音
        String lastName = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(lastHanZiName);
        //获得姓有音调拼音
        String toneLastName = NameUtil.nameToPinyinWithToneSplitWhiteSpace(lastHanZiName);
        //获得名
        String firstHanZiName = name.substring(lastHanZiName.length());
        //获得名无音调拼音
        String firstName = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(firstHanZiName);
        //获得名有音调拼音
        String toneFirstName = NameUtil.nameToPinyinWithToneSplitWhiteSpace(firstHanZiName);
        //获得姓名无音调拼音
        String fullName = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(name);
        //获得姓名有音调拼音
        String toneFullName = NameUtil.nameToPinyinWithToneSplitWhiteSpace(name);
        //获得姓名拼音缩写
        String abbrName = NameUtil.getAbbrFromPinyin(fullName);
        List<Contact> contactList= this.selectContactsByName(lastName, firstName, abbrName + abbrName, fullName,
                toneLastName,toneFirstName,toneFullName);
        //根据笔画数进行排序，做到对同音不同名的名字进行区别，如：李媛、李元返回的结果顺序是不同的
        return this.changeSortByLength(contactList, NameUtil.getStrokeCount(name));
    }

    public List<Contact> smartSelectContactsByPosition(String position) {
        if (StringUtils.isEmpty(position)) {
            return new ArrayList<>();
        }
        //去除所有空格
        position = position.replaceAll(" ", "");
        //获得姓
        String lastHanZiName = NameUtil.getLastName(position);
        //获得姓无音调拼音
        String lastName = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(lastHanZiName);
        //获得姓有音调拼音
        String toneLastName = NameUtil.nameToPinyinWithToneSplitWhiteSpace(lastHanZiName);
        //获得原始职位无音调拼音
        String fullPosition = NameUtil.nameToPinyinWithoutToneSplitWhiteSpace(position);
        //获得原始职位有音调拼音
        String toneFullPosition = NameUtil.nameToPinyinWithToneSplitWhiteSpace(position);
        //获得职位无音调拼音
        String newPositionPinyin = fullPosition.substring(lastName.length()).trim();
        //获得职位有音调拼音
        String toneNewPositionPinyin = toneFullPosition.substring(toneLastName.length()).trim();
        //获得原始职位拼音缩写
        String positionAbbr = NameUtil.getAbbrFromPinyin(fullPosition);
        return this.selectContactsByPosition(lastName, newPositionPinyin, fullPosition,
                positionAbbr+positionAbbr, toneLastName,toneNewPositionPinyin,toneFullPosition);
//        return this.changeSortByPositionLength(contactsList, fullPosition.length() + toneFullPosition.length() + 1);
    }
}
