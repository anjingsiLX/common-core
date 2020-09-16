package top.doudou.commons.aspect;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: 傻男人
 * @Date: 2020/8/19 9:20
 * @Version: 1.0
 * @Description: 将日志写入文件中
 */
@Slf4j
public class WriteLogToFile implements Serializable {

    /**
     * 将日志写入文件中
     * @param file 文件
     * @param log  内容
     */
    public static void logToFile(String file,String log){
        checkFile(file);
        FileOutputStream outputStream = null;
        FileChannel fileChannel = null;
        try{
            outputStream = new FileOutputStream(file,true);
            fileChannel = outputStream.getChannel();
            String res = logTime() +"    "+ log;
            System.out.println(res);
            String lineBreak = getLineBreak();
            writeCommonLog(fileChannel,res,lineBreak);
        }catch (IOException e){
            closeStream(fileChannel,outputStream);
        }
    }

    /**
     * 将日志写入文件中
     * @param file 文件
     * @param log  内容
     * @param exception  异常的信息
     */
    public static void logToFile(String file,String log,Exception exception){
        checkFile(file);
        FileOutputStream outputStream = null;
        FileChannel fileChannel = null;
        try{
            outputStream = new FileOutputStream(file,true);
            fileChannel = outputStream.getChannel();
            String res = logTime() +"    "+ log;
            System.out.println(res);
            String lineBreak = getLineBreak();
            ByteBuffer byteBuffer = writeCommonLog(fileChannel,res,lineBreak);
            if(null != exception){
                byteBuffer.flip();
                String exceptionMsg = exception.getClass().getName()+"  "+ exception.getLocalizedMessage();
                writeLogToFileChannel(fileChannel,byteBuffer,exceptionMsg,lineBreak);
                writeExceptionLog(fileChannel,exception,lineBreak);
            }
        }catch (IOException ioe){
            closeStream(fileChannel,outputStream);
        }
    }

    /**
     * 打印通用日志
     * @param fileChannel
     * @param log
     * @param lineBreak
     */
    private static ByteBuffer writeCommonLog(FileChannel fileChannel, String log, String lineBreak){
        ByteBuffer byteBuffer = ByteBuffer.allocate(log.getBytes().length+lineBreak.getBytes().length);
        writeLogToFileChannel(fileChannel,byteBuffer,log,lineBreak);
        return byteBuffer;
    }

    private static void writeLogToFileChannel(FileChannel fileChannel,ByteBuffer byteBuffer,String log,String lineBreak){
        byteBuffer.put(log.getBytes());
        byteBuffer.put(lineBreak.getBytes());
        byteBuffer.flip();
        try {
            fileChannel.write(byteBuffer);
        }catch (IOException ioException){

        }
    }

    /**
     * 打印异常的信息
     * @param fileChannel 文件通道
     * @param e 异常信息
     * @param lineBreak 换行符
     */
    private static void writeExceptionLog(FileChannel fileChannel, Exception e, String lineBreak){
        StackTraceElement[] stackTrace = e.getStackTrace();
        if(stackTrace.length <= 0 ){
            return;
        }
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement item = stackTrace[i];
            String msg = "    at  "+item.getClassName()+"."+item.getMethodName()+"("+item.getFileName()+":"+item.getLineNumber()+")";
            ByteBuffer buffer = ByteBuffer.allocate(msg.getBytes().length+lineBreak.getBytes().length);
            buffer.put(msg.getBytes());
            buffer.put(lineBreak.getBytes());
            buffer.flip();
            try {
                fileChannel.write(buffer);
            }catch (IOException ioException){
                closeStream(fileChannel,null);
            }
        }
    }

    /**
     * 日志前的时间
     * @return
     */
    private static String logTime(){
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    }


    /**
     * 关闭流
     * @param fileChannel
     * @param outputStream
     */
    private static void closeStream(FileChannel fileChannel, OutputStream outputStream){
        if(null != fileChannel){
            try {
                fileChannel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        if(null != outputStream){
            try {
                outputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static void checkFile(String path){
        File file = new File(path);
        if(!file.exists()){
            FileUtil.touch(file);
        }
    }
    /**
     * 获取换行符
     * @return
     */
    private static String getLineBreak(){
        String property = System.getProperty("os.name");
        if(property.startsWith("Windows")){
            return "\r\n";
        }
        if(property.startsWith("Linux")){
            return "\n";
        }
        if(property.startsWith("Mac")){
            return "\r";
        }
        return "\r\n";
    }
}
