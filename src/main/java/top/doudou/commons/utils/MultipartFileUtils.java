package top.doudou.commons.utils;

import org.springframework.web.multipart.MultipartFile;


/**
 * @Author: anyp
 * @Date: 2020/3/27
 * @Description:  MultipartFile工具类
 */
public class MultipartFileUtils {

    /**
     * 判断文件是否为空
     * @param file
     * @return
     */
    public static boolean isEmpty(MultipartFile file){
        if(file == null || file.isEmpty() || file.getSize() == 0){
            return true;
        }
        return false;
    }

    public static void checkEmpty(MultipartFile file){
        if(isEmpty(file))
            throw new BizException("文件不存在或者文件已损坏");
    }

    /**
     * 获取文件类型
     * @param file
     * @return
     */
    public static String getFileType(MultipartFile file){
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
    }

}
