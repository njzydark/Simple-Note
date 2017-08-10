package com.example.mynote;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sendtion on 2016/6/24.
 */
public class StringUtils {


    /**
     * @param targetStr 瑕佸鐞嗙殑瀛楃涓�
     * @description 鍒囧壊瀛楃涓诧紝灏嗘枃鏈拰img鏍囩纰庣墖鍖栵紝濡�"ab<img>cd"杞崲涓�"ab"銆�"<img>"銆�"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        List<String> splitTextList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<img.*?src=\\\"(.*?)\\\".*?>");
        Matcher matcher = pattern.matcher(targetStr);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                splitTextList.add(targetStr.substring(lastIndex, matcher.start()));
            }
            splitTextList.add(targetStr.substring(matcher.start(), matcher.end()));
            lastIndex = matcher.end();
        }
        if (lastIndex != targetStr.length()) {
            splitTextList.add(targetStr.substring(lastIndex, targetStr.length()));
        }
        return splitTextList;
    }

    /**
     * 鑾峰彇img鏍囩涓殑src鍊�
     * @param content
     * @return
     */
    public static String getImgSrc(String content){
        String str_src = null;
        //鐩墠img鏍囩鏍囩ず鏈�3绉嶈〃杈惧紡
        //<img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
        //寮�濮嬪尮閰峜ontent涓殑<img />鏍囩
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //鑾峰彇鍒板尮閰嶇殑<img />鏍囩涓殑鍐呭
                String str_img = m_img.group(2);

                //寮�濮嬪尮閰�<img />鏍囩涓殑src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    str_src = m_src.group(3);
                }
                //缁撴潫鍖归厤<img />鏍囩涓殑src

                //鍖归厤content涓槸鍚﹀瓨鍦ㄤ笅涓�涓�<img />鏍囩锛屾湁鍒欑户缁互涓婃楠ゅ尮閰�<img />鏍囩涓殑src
                result_img = m_img.find();
            }
        }
        return str_src;
    }

    /**
     * 鍏抽敭瀛楅珮浜樉绀�
     * @param target  闇�瑕侀珮浜殑鍏抽敭瀛�
     * @param text	     闇�瑕佹樉绀虹殑鏂囧瓧
     * @return spannable 澶勭悊瀹屽悗鐨勭粨鏋滐紝璁板緱涓嶈toString()锛屽惁鍒欐病鏈夋晥鏋�
     * SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * vHolder.tv_itemName_search.setText(textString);
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));// 闇�瑕侀噸澶嶏紒
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

}
