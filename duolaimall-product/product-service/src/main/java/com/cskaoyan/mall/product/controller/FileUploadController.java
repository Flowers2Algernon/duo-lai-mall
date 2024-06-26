package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.execption.BusinessException;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.constant.ProductCodeEnum;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("admin/product")
public class FileUploadController {

    // 获取文件上传对应的地址
    @Value("${minio.endpointUrl}")
    public String endpointUrl;

    @Value("${minio.accessKey}")
    public String accessKey;

    @Value("${minio.secretKey}")
    public String secretKey;

    @Value("${minio.bucketName}")
    public String bucketName;

    //  文件上传控制器
    @PostMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws Exception{
        //  准备获取到上传的文件路径！
        String url = "";

        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpointUrl)
                        .credentials(accessKey, secretKey)
                        .build();
        // 检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if(isExist) {
            System.out.println("Bucket already exists.");
        } else {
            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
        //  定义一个文件的名称 : 文件上传的时候，名称不能重复！
        String fileName = System.currentTimeMillis()+ UUID.randomUUID().toString();
        // 使用putObject上传一个文件到存储桶中。
        InputStream inputStream = file.getInputStream();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

        } finally {
            inputStream.close();
        }


        url = endpointUrl+"/"+bucketName+"/"+fileName;

        System.out.println("url:\t"+url);
        //  将文件上传之后的路径返回给页面！
        return Result.ok(url);
    }


}
