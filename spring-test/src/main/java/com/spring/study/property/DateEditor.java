package com.spring.study.property;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by free on 17-3-8.
 */
public class DateEditor extends PropertyEditorSupport {

    private  Map<String, DateFormat> dateFormatMap;
    private  DateFormat dateFormat;

    public DateEditor(DateFormat dateFormat){
        this.dateFormat=dateFormat;
    }
    public DateEditor(Map<String,DateFormat> dateFormatMap){
        this.dateFormatMap=dateFormatMap;
    }
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        parseDate(text);
    }

    private void parseDate(String text) {
        if (!StringUtils.hasText(text) ){
            setValue(null);
            return;
        }else {
            if (dateFormatMap==null){
                setValue(null);
                return;
            }

            int textLenth=text.trim().length();

            for (Map.Entry<String,DateFormat> entry : dateFormatMap.entrySet()){
                if(entry.getKey().trim().length() == textLenth){
                    dateFormat=entry.getValue();
                    break;
                }
            }

            if (this.dateFormat==null){
                setValue(null);
                return;
            }
            try {
                setValue(dateFormat.parse(text));
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
            }
        }



    }

}
