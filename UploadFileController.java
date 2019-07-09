package com.mzs.springboot2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:    上传文件
 * @Author:         apple
 * @CreateDate:     2019/7/9
 * @UpdateUser:     apple
 * @UpdateDate:     2019/7/9
 */
@Controller
@RequestMapping("/file")
@Slf4j
public class UploadFileController {

    @GetMapping("/upload/page")
    public String uploadPage() {
        return "/upload.html";
    }

    /**
     * 上传文件
     * （对于HttpServletRequest类型的解析）
     *
     * @param request 上传请求
     * @return 处理结果
     */
    @PostMapping("/upload/request")
    @ResponseBody
    public Map<String, Object> uploadRequest(HttpServletRequest request) {
        MultipartHttpServletRequest multiRequest = null;
        if (request instanceof MultipartHttpServletRequest) {
            multiRequest = (MultipartHttpServletRequest) request;
        } else {
            dealResultMap(false, generateFailResolve());
        }
        MultipartFile multipartFile = multiRequest.getFile("file");
        if (multipartFile == null) {
            if (log.isErrorEnabled()) {
                log.error(generateFailResolve());
            }
            return dealResultMap(false, generateFailResolve());
        }
        return transferTo(multipartFile);
    }

    /**
     * 上传文件
     * （对于MultipartFile类型的解析）
     *
     * @param multipartFile MultipartFile类型的文件
     * @return 处理结果
     */
    @PostMapping("/upload/multipart")
    @ResponseBody
    public Map<String, Object> uploadMultipartFile(MultipartFile multipartFile) {
        return transferTo(multipartFile);
    }

    /**
     * 上传文件
     * （对于Part类型的解析）
     *
     * @param file Part类型的文件
     * @return 处理结果
     */
    @PostMapping("/upload/part")
    @ResponseBody
    public Map<String, Object> uploadPart(Part file) {
        String fileName = file.getSubmittedFileName();
        try {
            file.write(fileName);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(generateUploadMsg(false, fileName), e.getMessage());
            }
            return dealResultMap(false, generateUploadMsg(false, fileName));
        }
        if (log.isInfoEnabled()) {
            log.info(generateUploadMsg(true, fileName));
        }
        return dealResultMap(true, generateUploadMsg(true, fileName));
    }

    /**
     * 上传文件
     *
     * @param multipartFile MultipartFile类型的文件
     * @return 处理后的信息map
     */
    private Map<String, Object> transferTo(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        fileName = fileName.substring(fileName.lastIndexOf("\\"));
        File file = new File(fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(generateUploadMsg(false, fileName), e.getMessage());
            }
            return dealResultMap(false, generateUploadMsg(false, fileName));
        }
        if (log.isInfoEnabled()) {
            log.info(generateUploadMsg(true, fileName));
        }
        return dealResultMap(true, generateUploadMsg(true, fileName));
    }

    /**
     * 生成上传处理后的信息
     * （无论成功与否）
     *
     * @param success  是否上传成功
     * @param fileName 上传的文件名
     * @return 处理后的信息
     */
    private String generateUploadMsg(boolean success, String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("文件-[");
        builder.append(fileName);
        if (success) {
            builder.append("] 上传成功");
        } else {
            builder.append("] 上传失败");
        }
        return builder.toString();
    }

    /**
     * 生成解析失败的信息
     *
     * @return 解析失败的信息
     */
    private String generateFailResolve() {
        return "文件解析失败";
    }


    /**
     * 定义返回的信息格式
     *
     * @param success 处理成功
     * @param msg 处理后的信息
     * @return 信息map
     */
    private Map<String, Object> dealResultMap(boolean success, String msg) {
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("success", success);
        resultMap.put("msg", msg);
        return resultMap;
    }

}
