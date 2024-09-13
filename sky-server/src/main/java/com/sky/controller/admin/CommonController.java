package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @ClassName CommonController
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/12 23:42
 * @Version 1.0
 **/
@Slf4j
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@RestController
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            String originalFilename = file.getOriginalFilename(); //文件上传时的原始文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); //得到原始文件名的扩展名
            String newName = UUID.randomUUID().toString() + extension; //构造新文件名称，用UUID+扩展名

            String filePath = aliOssUtil.upload(file.getBytes(), newName);//文件的访问路径

            log.info("文件{}上传成功", file);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
