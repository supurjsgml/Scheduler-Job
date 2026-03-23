package com.app.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;

@SuppressWarnings("unchecked")
@UtilityClass
public class CommonUtil {
    
	public final ObjectMapper om = new ObjectMapper()
			.registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    private final ModelMapper mapper = new ModelMapper();

    
    /**
     * mybatis paging 문자열 생성
     * 
     * @param 
     * @return String
     * @author guney
     * @date 2024. 3. 7.
     */
    public String strPaging(int curPage, int pageSize) {
        String rtnValue = "";

        int perPage = (pageSize == -1 || pageSize >= 500) ? 500 : pageSize;
        int startNum = curPage == 0 ? 0 : (curPage - 1) * pageSize;

        if (startNum >= 0 && perPage > 0) {
            rtnValue = String.format("LIMIT %s, %s", startNum, perPage);
        }

        return rtnValue;
    }
    
    /**
     * Camel Case > Snake Case 변환
     * 
     * @param String str : 변환문자
     * @return String
     * @author guney
     * @date 2024. 3. 20.
     */
    public String convertCamelToSnake(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str);
    }
    
    /**
     * Snake Case > Camel Case 변환
     * 
     * @param String str : 변환문자
     * @return String
     * @author guney
     * @date 2024. 3. 20.
     */
    public String convertSnakeToCamel(String str) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }
    
    /**
     * List모델 type 변환
     * 
     * @param List<S> : 변환할 list 객체
     * @return List<D> : 변경할 타입 class
     * @author guney
     * @date 2024. 3. 25.
     */
    public <S, D> List<D> convertList(List<S> sourceDataList, Class<D> destinationClass) {
        if (sourceDataList == null || sourceDataList.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceDataList.stream().map(sourceData -> mapper.map(sourceData, destinationClass)).toList();
    }
    
    /**
     * 모델 type 변환
     * 
     * @param S : 변환할 객체
     *        D : 변경할 타입 class
     * @return D
     * @author guney
     * @date 2024. 3. 25.
     */
    public <S, D> D convertObject(S sourceData, Class<D> destinationClass) {
        if (ObjectUtils.isEmpty(sourceData) || sourceData == null) return null;
        return om.convertValue(sourceData, destinationClass);
    }
    
    /**
     * 문자열 객체 변환
     * @param <D>
     * @param stringData
     * @param destinationClass
     * @return
     * @author guney
     * @date 2026. 3. 20.
     */
    public <D> D readValue(String stringData, Class<D> destinationClass) {
    	if (ObjectUtils.isEmpty(stringData) || stringData == null) return null;
    	
    	try {
			return om.readValue(stringData, destinationClass);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    /**
     * 객체복사
     * 
     * @param S : sourceData
     *        D : target
     * @return D
     * @author guney
     * @date 2024. 4. 8.
     */
    public <S, D> D copyBean(S sourceData, D target) {
        BeanUtils.copyProperties(sourceData, target);
        return target;
    }
    
    /**
     * 객체 > map 변환
     * 
     * @param 
     * @return HashMap<String,Object>
     * @author guney
     * @date 2024. 4. 9.
     */
    public <T> Map<String, Object> convertMap(T o) {
        HashMap<String, Object> returnMap = null;
        
        if (o instanceof Map) {
            returnMap = new HashMap<>();
            Map<String, Object> readMap = om.convertValue(o, Map.class);
            
            for (Entry<String, Object> r : readMap.entrySet()) {
                returnMap.put(CommonUtil.convertCamelToSnake(r.getKey()), r.getValue());
            }
        }
        
        return returnMap;
    }
    
    /**
     * 변수 스네이크 케이스로 변환
     * 
     * @param 
     * @return T
     * @author guney
     * @date 2024. 4. 23.
     */
    public <T> T mapParamConvert(T o) {
        Map<String, Object> returnMap = null;
        
        if (o instanceof Map) {
            returnMap = new HashMap<>();
            Map<String, Object> readMap = om.convertValue(o, Map.class);
            
            for (Entry<String, Object> r : readMap.entrySet()) {
                returnMap.put(convertCamelToSnake(r.getKey()), r.getValue());
            }
        }
        
        return ObjectUtils.isNotEmpty(returnMap) ? (T) returnMap : o;
    }
    
    /**
     * QureyParam 생성(키워드 암호화)
     * 
     * @param String url : 요청url
     *      , String kwd : 키워드 검색어
     *      , Object o   : Dto
     * @return String
     * @author guney
     * @throws UnsupportedEncodingException 
     * @date 2024. 3. 15.
     */
    public String getQureyParam(String url, String kwd, Object o) throws UnsupportedEncodingException {
        kwd = URLEncoder.encode(kwd, StandardCharsets.UTF_8.toString());
        
        Map<String, Object> m = om.convertValue(o, HashMap.class);
        UriComponentsBuilder p = UriComponentsBuilder.fromUriString(url);
        
        for (Entry<String, Object> e : m.entrySet()) {
            if (ObjectUtils.isNotEmpty(e.getValue())) p.queryParam(e.getKey(), e.getValue());
        }
        
        return p.build().toUri().toString();
    }
    
    /**
     * QureyParam 생성
     * 
     * @param 
     * @return String
     * @author guney
     * @date 2024. 3. 16.
     */
    public String getQureyParam(String url, Object o) throws UnsupportedEncodingException {
        Map<String, Object> m = om.convertValue(o, HashMap.class);
        UriComponentsBuilder p = UriComponentsBuilder.fromUriString(url);
        
        for (Entry<String, Object> e : m.entrySet()) {
            if (ObjectUtils.isNotEmpty(e.getValue())) p.queryParam(e.getKey(), e.getValue());
        }
        
        return p.build().toUri().toString();
    }
    
    /**
     * String 소숫점 포맷
     * 
     * @param double d : 소숫점 수
     *        int len  : 포맷 자릿수
     * @return String
     * @author guney
     * @date 2024. 5. 8.
     */
    public String decimalFormat(double d, int len) {
        String lenStr = "0.";
        for (int i = 0; i < len; i++) {
            lenStr = lenStr.concat("0");
        }
        
        return new DecimalFormat(lenStr).format(d);
    }
    
    /**
     * 맨 앞글자 소문자로 변경
     * 
     * @param String str
     * @return String
     * @author guney
     * @date 2024. 5. 22.
     */
    public String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
    
    /**
     * 맨 앞글자 대문자로 변경
     * 
     * @param String str
     * @return String
     * @author guney
     * @date 2024. 5. 22.
     */
    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * 널을 체크
     * @param str
     * @return boolean
     * @author guney
     * @date 2026. 3. 7.
     */
    public boolean stringisBlankOrNull(String str) {
        return StringUtils.isBlank(str) || "null".equalsIgnoreCase(str);
    }
}
