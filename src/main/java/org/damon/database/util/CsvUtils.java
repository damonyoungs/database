package org.damon.database.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2021/4/29 9:45
 */
public class CsvUtils {

    private CsvUtils(){

    }

    public static void getCsvFile(CsvModel csvModel) {
        HttpServletResponse response = getResponse();
        if (null != response) {
            try (OutputStream os = response.getOutputStream()) {
                os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                for (CsvModelData data : csvModel.getData()) {
                    if (CollUtil.isNotEmpty(data.getHead())){
                        os.write(getBytes(data.getHead()));
                    }
                    os.write(getBytes(data.getTitle()));
                    for (List<String> list : data.getRow()) {
                        os.write(getBytes(list));
                    }
                    os.write(getLine());
                    os.write(getLine());
                }
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(csvModel.getFileName() + ".csv", "UTF-8") + "\"");
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] getLine(){
        return System.getProperty("line.separator").getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] getBytes(List<String> list) {
        String str = JSONUtil.toJsonStr(list);
        String result = str.substring(1, str.length() - 1)+System.getProperty("line.separator");
        return result.getBytes(StandardCharsets.UTF_8);
    }

    private static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            if (null != servletRequestAttributes.getResponse()) {
                return servletRequestAttributes.getResponse();
            }
        }
        return null;
    }

}
