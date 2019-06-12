package com.wangyibo.fulltextdemo.util;

import com.wangyibo.fulltextdemo.config.SurnameConfig;
import com.wangyibo.fulltextdemo.pojo.LastName;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author wangyb
 * @Date 2019/5/16 11:54
 * Modified By:
 * Description:
 */
@UtilityClass
@Slf4j
public class NameUtil {

    /**
     * 将汉字转化成有音调的拼音
     *
     * @param chinese 汉字字符串
     * @return
     */
    public String toPinyinWithTone(String chinese) {
        if (StringUtils.isEmpty(chinese)) {
            return "";
        }
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //生成带有音调数字的拼音
        defaultFormat.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    //对单姓氏进行判断
                    if (i == 0) {
                        Integer flag = 0;
                        List<LastName> lastNameList = SurnameConfig.lastNameList;
                        String charName = String.valueOf(newChar[i]);
                        for (LastName lastName : lastNameList) {
                            if (lastName.getChName().equals(charName)) {
                                flag = 1;
                                pinyinStr.append(lastName.getToneName());
                                break;
                            }
                        }
                        if (flag == 1) {
                            continue;
                        }
                    }
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    log.error("字符串中含有不合法字符，无法转换拼音");
                    return "";
                }
            } else {
                pinyinStr.append(newChar[i]);
            }
        }
        return pinyinStr.toString().toLowerCase();
    }

    /**
     * 将汉字转化成无音调的拼音
     *
     * @param chinese 汉字字符串
     * @return
     */
    public String toPinyinWithoutTone(String chinese) {
        if (StringUtils.isEmpty(chinese)) {
            return "";
        }
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //生成带有音调数字的拼音
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] <= 128) {
                pinyinStr.append(newChar[i]);
            } else {
                try {
                    if (i == 0) {
                        Integer flag = 0;
                        List<LastName> lastNameList = SurnameConfig.lastNameList;
                        String charName = String.valueOf(newChar[i]);
                        for (LastName lastName : lastNameList) {
                            if (lastName.getChName().equals(charName)) {
                                flag = 1;
                                pinyinStr.append(lastName.getWithoutToneName());
                                break;
                            }
                        }
                        if (flag == 1) {
                            continue;
                        }
                    }
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    log.error("字符串中含有不合法字符，无法转换拼音");
                    return "";
                }
            }
        }
        return pinyinStr.toString().toLowerCase();
    }

    /**
     * 将名转化成带空格的无音调拼音，英文保留
     *
     * @param name
     * @return
     */
    public String nameToPinyinWithoutToneSplitWhiteSpace(String name) {
        return toPinyinWithoutTone(nameToSplitWhiteSpace(name));
    }

    /**
     * 将名转化成带空格的有音调拼音，英文保留
     *
     * @param name 汉字字符串
     * @return
     */
    public String nameToPinyinWithToneSplitWhiteSpace(String name) {
        return toPinyinWithTone(nameToSplitWhiteSpace(name));
    }

    /**
     * 将字符串中的汉字用空格隔开
     * @param name 汉字字符串
     */
    private String nameToSplitWhiteSpace(String name){
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        //去除前后多余空格，防止循环判断时数组越界
        String nameWithAllSpace = name.replaceAll("", " ").trim();
        char[] nameChar = nameWithAllSpace.toCharArray();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < nameChar.length; i++) {
            if (String.valueOf(nameChar[i]).equals(" ")) {
                //如果空格前后是英文字母则不处理
                if ((Character.isLowerCase(nameChar[i - 1]) || Character.isUpperCase(nameChar[i - 1])) &&
                        (Character.isLowerCase(nameChar[i + 1]) || Character.isUpperCase(nameChar[i + 1]))) {

                } else {
                    nameBuilder.append(String.valueOf(nameChar[i]));
                }
            } else {
                nameBuilder.append(String.valueOf(nameChar[i]));
            }
        }
        return nameBuilder.toString();
    }

    /**
     * 生成拼音缩写
     *
     * @param pinyinName 拼音，首尾不能有空格
     * @return
     */
    public String getAbbrFromPinyin(String pinyinName) {
        if (StringUtils.isEmpty(pinyinName)) {
            return "";
        } else {
            StringBuilder nameBuilder = new StringBuilder();
            char[] nameChar = pinyinName.toCharArray();
            for (int i = 0; i < nameChar.length; i++) {
                //首字母不是空格，直接写入
                if (i == 0) {
                    nameBuilder.append(String.valueOf(nameChar[i]));
                } else {
                    //末尾不可能是空格，不用考虑越界
                    if (String.valueOf(nameChar[i]).equals(" ")) {
                        nameBuilder.append(nameChar[i + 1]);
                    }
                }
            }
            return nameBuilder.toString();
        }
    }

    /**
     * 将汉字转化成无音调的拼音以空格分隔
     *
     * @param chinese 汉字字符串
     */
    public String toPinyinWithoutToneSplitWhiteSpace(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        for (int i = 0; i < newChar.length; i++) {
            try {
                if (i == 0) {
                    boolean flag = false;
                    List<LastName> lastNameList = SurnameConfig.lastNameList;
                    String charName = String.valueOf(newChar[i]);
                    for (LastName lastName : lastNameList) {
                        if (lastName.getChName().equals(charName)) {
                            flag = true;
                            pinyinStr.append(lastName.getWithoutToneName()).append(" ");
                            break;
                        }
                    }
                    if (flag) {
                        continue;
                    }
                }
                pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0]).append(" ");
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                log.error("字符串中含有不合法字符，无法转换拼音");
                return "";
            }
        }

        return pinyinStr.toString().trim();
    }

    /**
     * 从名字中获取姓，仅考虑两个字的常用复姓
     *
     * @param name 中文名
     */
    public String getLastName(String name) {
        if (name.length() >= 2) {
            String lastName = name.substring(0, 2);
            if (SurnameConfig.twoSurnameList.contains(lastName)) {
                return lastName;
            }
            return name.substring(0, 1);
        } else {
            return name;
        }
    }
}
