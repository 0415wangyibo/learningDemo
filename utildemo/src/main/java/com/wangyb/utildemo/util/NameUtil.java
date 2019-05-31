package com.wangyb.utildemo.util;

import com.wangyb.utildemo.config.SurnameConfig;
import com.wangyb.utildemo.pojo.LastName;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;

/**
 * @author wangyb
 * @Date 2019/5/31 10:18
 * Modified By:
 * Description: 对中文名字的处理工具类，可以在mysql 5.6.4版本以上，结合全局搜索，将人名的姓和名分开，实现对人名的模糊匹配并且标有权值
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
     * 将“Web开发”这种类型转化成“web kai fa”形式，方便全局搜索
     * @param name
     * @return
     */
    public String nameToPinyinWithoutToneSplitWhiteSpace(String name) {
        //去除前后多余空格，防止循环判断时数组越界
        String nameWithAllSpace = name.replaceAll("", " ").trim();
        char[] nameChar = nameWithAllSpace.toCharArray();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0;i<nameChar.length;i++) {
            if (String.valueOf(nameChar[i]).equals(" ")) {
                //如果空格前后是英文字母则不处理
                if ((Character.isLowerCase(nameChar[i-1]) || Character.isUpperCase(nameChar[i-1]))&&
                        (Character.isLowerCase(nameChar[i+1]) || Character.isUpperCase(nameChar[i+1]))){

                }else {
                    nameBuilder.append(String.valueOf(nameChar[i]));
                }
            }else {
                nameBuilder.append(String.valueOf(nameChar[i]));
            }
        }
        return toPinyinWithoutTone(nameBuilder.toString());
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
    public String getFirstName(String name) {
        if (name.length() >= 2) {
            String firstName = name.substring(0, 2);
            if (SurnameConfig.twoSurnameList.contains(firstName)) {
                return firstName;
            }
            return name.substring(0, 1);
        } else {
            return name;
        }
    }

    /**
     * 获取人名首字母缩写
     * @param hanzi 中文名
     * @return
     */
    public static String getNamePinyinAbbr(String hanzi) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

        try {
            char[] nameChars = hanzi.toCharArray();

            StringBuilder abbrName = new StringBuilder();
            for (int i = 0; i < nameChars.length; i++) {
                if (i == 0) {
                    boolean isCorrect = false;
                    for (LastName lastNameCorrect : SurnameConfig.lastNameList) {
                        if (lastNameCorrect.getChName().equals(String.valueOf(nameChars[i]))) {
                            abbrName.append(lastNameCorrect.getWithoutToneName().charAt(0));
                            isCorrect = true;
                            break;
                        }
                    }
                    if (!isCorrect) {
                        String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChars[i], defaultFormat);
                        abbrName.append(strs[0].charAt(0));
                    }
                    continue;
                }
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChars[i], defaultFormat);
                abbrName.append(strs[0].charAt(0));
            }
            return abbrName.toString();
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }

        return "";
    }
}
